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

  def createJob(data: String) = {
    val job = Schema.Job(Random.nextLong(), data, 0)
    val mapInputs = io.Source.fromString(data).getLines.zipWithIndex.map {
      case (v, k) => Schema.Input(Random.nextLong(), k.toString, v, job.id, false)
    }.toIterable
    DB.withTransaction { implicit session =>
      Table.Job.q += job
      Table.MapInput.q ++= mapInputs
    }
  }

  // TODO: once sent out, set send to false
  // prioritize send true
  // undone send falses will be done later

  def moreTasks(max: Int): List[Schema.Input] = DB.withTransaction { implicit session =>

    def random = SimpleFunction[Long]("random").apply(Seq.empty)

    def fromMapInputs(job: Schema.Job) = {
      val mapInputs = Table.MapInput.q.filter(x => x.id === job.id && !x.done).sortBy(_ => random).take(max).list()
      if (mapInputs.isEmpty) {
        Table.Job.q.filter(_.id === job.id).map(_.state).update(1)
        fromIntermediates(job)
      } else {
        mapInputs
      }
    }

    def fromIntermediates(job: Schema.Job) = {
      val intermediates = Table.Intermediate.q.filter(x => x.id === job.id && !x.done).sortBy(_ => random).take(max).list()
      if (intermediates.isEmpty) {
        Table.Job.q.filter(_.id === job.id).map(_.state).update(2)
      }
      intermediates
    }

    var ret: Option[List[Schema.Input]] = Some(Nil)
    while (ret.isDefined && ret.get.isEmpty) {
      val jobOpt = Table.Job.q.filter(_.state =!= 2).sortBy(_ => random).firstOption()
      jobOpt match {
        case None =>
          ret = None
        case Some(job) =>
          job.state match {
            case 0 =>
              ret = Some(fromMapInputs(job))
            case 1 =>
              ret = Some(fromIntermediates(job))
            case _ =>
              // state can only be 0, 1, or 2
              throw new AssertionError
          }
      }
    }
    ret.toList.flatten

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

}