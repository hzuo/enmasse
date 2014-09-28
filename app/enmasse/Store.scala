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
      case (v, k) => Schema.Input(Random.nextLong(), k.toString, v, job.id, false)
    }.toIterable
    DB.withTransaction { implicit session =>
      Table.Job.q += job
      Table.File.q += file
      Table.MapInput.q ++= mapInputs
    }
  }

  // TODO: once sent out, set send to false
  // prioritize send true
  // undone send falses will be done later

  def moreTasks(max: Int): Tasks = DB.withTransaction { implicit session =>

    def random = SimpleFunction[Long]("random").apply(Seq.empty)

    def fromMapInputs(job: Schema.Job): Iterable[Task] = {
      val mapInputs = Table.MapInput.q.filter(x => x.jobId === job.id && !x.done).sortBy(_ => random).take(max).list()
      if (mapInputs.isEmpty) {
        Table.Job.q.filter(_.id === job.id).map(_.state).update(1)
      }
      mapInputs.map(x => MapTask(x.k, x.v))
    }

    def fromIntermediates(job: Schema.Job): Iterable[Task] = {
      val howMany = Math.max(max / 10, 1)
      val intermediates: List[(String, Schema.Input)] = {
        Table.Intermediate.q
          .filter(x => x.jobId === job.id && !x.done).sortBy(_ => random).take(howMany).map(_.k)
          .innerJoin(Table.Intermediate.q).on(_ === _.k)
          .list()
      }
      if (intermediates.isEmpty) {
        Table.Job.q.filter(_.id === job.id).map(_.state).update(2)
      }
      val grouped = intermediates.groupBy(_._1).mapValues(_.map(_._2))
      grouped.map(x => ReduceTask(x._1, x._2.map(_.v)))
    }

    var ret: Option[Iterable[Task]] = Some(Nil)
    var modeAndFn: (Mode, String) = null
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
                modeAndFn = (Map, job.map)
              } else {
                ret = Some(fromIntermediates(job))
                modeAndFn = (Reduce, job.reduce)
              }
            case 1 =>
              ret = Some(fromIntermediates(job))
              modeAndFn = (Reduce, job.reduce)
          }
      }
    }
    assert(modeAndFn != null)
    val (mode, fn) = modeAndFn
    Tasks(mode, fn, ret.toIterable.flatten)

  }

  def completeMapInput(id: Long, kvs: Iterable[(String, String)]) = {
    // outside the transaction block, generating randoms take a long time
    val augmented = kvs.map { case (k, v) => (Random.nextLong(), k, v) }
    DB.withTransaction { implicit session =>
      val mapInputQuery = Table.MapInput.q.filter(_.id === id)
      mapInputQuery.firstOption match {
        case None =>
          throw new AssertionError
        case Some(mapInput) =>
          if (!mapInput.done) {
            mapInputQuery.map(_.done).update(true)
            val intermediates = augmented.map { case (id, k, v) => Schema.Input(id, k, v, mapInput.jobId, false) }
            Table.Intermediate.q ++= intermediates
          }
      }
    }
  }

  def completeIntermediate(jobId: Long, intermediateKey: String, kvs: Iterable[(String, String)]) = {
    // outside the transaction block, generating randoms take a long time
    val augmented = kvs.map { case (k, v) => (Random.nextLong(), k, v) }
    DB.withTransaction { implicit session =>
      val intermediateQuery = Table.Intermediate.q.filter(x => x.jobId === jobId && x.key === key)
      intermediateQuery.firstOption match {
        case None =>
          throw new AssertionError
        case Some(intermediate) =>
          if (!intermediate.done) {
            intermediateQuery.map(_.done).update(true)
            val outputs = augmented.map { case (id, k, v) => Schema.Output(id, k, v, intermediate.jobId) }
            Table.ReduceOuput.q ++= outputs
          }
      }
    }
  }

  def getJobs(): List[Schema.Job] = DB.withSession { implicit session =>
    Table.Job.q.list()
  }

}