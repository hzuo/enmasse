package enmasse

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.slick.driver.PostgresDriver.simple._
import scala.util._

import play.api.Play.current
import play.api.cache.Cache
import play.api.db.slick.DB
import play.api.libs.ws._

object Store {

  def download(url: String): Future[String] = {
    WS.url(url).get().map(_.body)
  }

  def createJob(name: String, dataOrigin: String, content: String, map: String, reduce: String) = {
    val job = Schema.Job(Random.nextLong(), name, dataOrigin, map, reduce, System.currentTimeMillis(), 0)
    val file = Schema.File(job.id, content)
    val mapInputs = io.Source.fromString(content).getLines.zipWithIndex.map {
      case (v, k) => Schema.Record(k.toString, v, job.id)
    }.toIterable
    val jobPulse = Schema.JobPulse(job.id, mapInputs.size, -1)
    DB.withTransaction { implicit session =>
      Table.Job.q += job
      Table.File.q += file
      Table.MapInput.q ++= mapInputs
      Table.JobPulse.q += jobPulse
    }
  }

  // TODO: once sent out, set send to false
  // prioritize send true
  // undone send falses will be done later

  def moreTasks(max: Int): Option[Either[MapTasks, ReduceTasks]] = {
    val (meta, ret) = DB.withTransaction { implicit session =>

      def random = SimpleFunction[Long]("random").apply(Seq.empty)

      def fromMapInputs(job: Schema.Job): Seq[MapTask] = {
        val mapInputs = Table.MapInput.q.filter(x => x.jobId === job.id).sortBy(_ => random).take(max).list()
        if (mapInputs.isEmpty) {
          Table.Job.q.filter(_.id === job.id).map(_.state).update(1)
          val totalReduceTasks = Table.Intermediate.q.length.run
          Table.JobPulse.q.filter(_.jobId === job.id).map(_.totalReduceTasks).update(totalReduceTasks)
        }
        mapInputs.map(x => MapTask(x.k, x.v))
      }

      def fromIntermediates(job: Schema.Job): Seq[ReduceTask] = {
        val howMany = Math.max(max / 10, 1)
        val intermediates: List[(String, Schema.Record)] = {
          Table.Intermediate.q
            .filter(x => x.jobId === job.id).sortBy(_ => random).take(howMany).map(_.k)
            .innerJoin(Table.Intermediate.q).on(_ === _.k)
            .list()
        }
        if (intermediates.isEmpty) {
          Table.Job.q.filter(_.id === job.id).map(_.state).update(2)
        }
        val grouped = intermediates.groupBy(_._1).mapValues(_.map(_._2))
        grouped.map(x => ReduceTask(x._1, x._2.map(_.v))).toSeq
      }

      var ret: Option[Seq[Task]] = Some(Nil)
      var meta: (Long, Mode, String) = null
      while (ret.isDefined && ret.get.isEmpty) {
        val jobOpt = Table.Job.q.filter(_.state =!= 2).sortBy(_ => random).take(1).firstOption()
        jobOpt match {
          case None =>
            ret = None
          case Some(job) =>
            job.state match {
              case 0 =>
                val tasks = fromMapInputs(job)
                if (!tasks.isEmpty) {
                  ret = Some(tasks)
                  meta = (job.id, Map, job.map)
                } else {
                  ret = Some(fromIntermediates(job))
                  meta = (job.id, Reduce, job.reduce)
                }
              case 1 =>
                ret = Some(fromIntermediates(job))
                meta = (job.id, Reduce, job.reduce)
            }
        }
      }
      if (ret.isDefined && meta == null)
        throw new AssertionError
      (meta, ret)
    }
    ret.map { tasks =>
      val (jobId, mode, fn) = meta
      mode match {
        case Map =>
          val mapTasks = tasks.map(_.asInstanceOf[MapTask])
          Left(MapTasks(jobId.toString, true, fn, mapTasks))
        case Reduce =>
          val reduceTasks = tasks.map(_.asInstanceOf[ReduceTask])
          Right(ReduceTasks(jobId.toString, false, fn, reduceTasks))
      }
    }
  }

  def completeMapInput(jobId: Long, mapInputKey: String, intermediates: Iterable[(String, String)]) = {
    DB.withTransaction { implicit session =>
      Table.Intermediate.q ++= intermediates.map { case (k, v) => Schema.Record(k, v, jobId) }
      Table.MapInput.q.filter(x => x.jobId === jobId && x.k === mapInputKey).delete
    }
  }

  def completeIntermediate(jobId: Long, intermediateKey: String, outputs: Iterable[(String, String)]) = {
    DB.withTransaction { implicit session =>
      Table.ReduceOutput.q ++= outputs.map { case (k, v) => Schema.Record(k, v, jobId) }
      Table.Intermediate.q.filter(x => x.jobId === jobId && x.k === intermediateKey).delete
    }
  }

  def getJobs(): List[Schema.Job] = DB.withSession { implicit session =>
    Table.Job.q.list()
  }

  def getOutputs(jobId: Long) = DB.withSession { implicit session =>
    Table.ReduceOutput.q.filter(_.jobId === jobId).list()
  }

  def getProgress(jobId: Long): Double = DB.withSession { implicit session =>
    val jobPulseOpt = Table.JobPulse.q.filter(_.jobId === jobId).firstOption
    if (jobPulseOpt.isEmpty) {
      throw new IllegalArgumentException
    } else {
      val x = jobPulseOpt.get
      val denom = if (x.totalReduceTasks == -1) x.totalMapTasks else x.totalReduceTasks
      val num = if (x.totalReduceTasks == -1) Table.MapInput.q.length.run else Table.Intermediate.q.length.run
      val b = if (x.totalReduceTasks == -1) 0 else 0.5
      (num.toDouble / (denom.toDouble * 2)) + b
    }
  }

}