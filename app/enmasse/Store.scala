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

  def moreTasks(max: Int): ((Mode, String), List[Schema.Input]) = DB.withTransaction { implicit session =>

    def random = SimpleFunction[Long]("random").apply(Seq.empty)

    def fromMapInputs(job: Schema.Job): (Mode, List[Schema.Input]) = {
      val mapInputs = Table.MapInput.q.filter(x => x.jobId === job.id && !x.done).sortBy(_ => random).take(max).list()
      if (mapInputs.isEmpty) {
        Table.Job.q.filter(_.id === job.id).map(_.state).update(1)
        (Reduce, fromIntermediates(job))
      } else {
        (Map, mapInputs)
      }
    }

    def fromIntermediates(job: Schema.Job) = {
      val intermediates = Table.Intermediate.q.filter(x => x.jobId === job.id && !x.done).sortBy(_ => random).take(max).list()
      if (intermediates.isEmpty) {
        Table.Job.q.filter(_.id === job.id).map(_.state).update(2)
      }
      intermediates
    }

    var ret: Option[List[Schema.Input]] = Some(Nil)
    var modeAndFn: (Mode, String) = null
    while (ret.isDefined && ret.get.isEmpty) {
      val jobOpt = Table.Job.q.filter(_.state =!= 2).sortBy(_ => random).firstOption()
      jobOpt match {
        case None =>
          ret = None
        case Some(job) =>
          job.state match {
            case 0 =>
              val (mode0, ret0) = fromMapInputs(job)
              modeAndFn = mode0 match {
                case Map => (Map, job.map)
                case Reduce => (Reduce, job.reduce)
              }
              ret = Some(ret0)
            case 1 =>
              modeAndFn = (Reduce, job.reduce)
              ret = Some(fromIntermediates(job))
          }
      }
    }
    assert(modeAndFn != null)
    (modeAndFn, ret.toList.flatten)

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

  def completeIntermediate(id: Long, kvs: Iterable[(String, String)]) = {
    // outside the transaction block, generating randoms take a long time
    val augmented = kvs.map { case (k, v) => (Random.nextLong(), k, v) }
    DB.withTransaction { implicit session =>
      val intermediateQuery = Table.Intermediate.q.filter(_.id === id)
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