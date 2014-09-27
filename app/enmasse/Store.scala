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

  def moreTasks(max: Int): List[Schema.Input] = DB.withTransaction { implicit session =>

    def random = SimpleFunction[Long]("random").apply(Seq.empty)

    def fromMapInputs(job: Schema.Job) = {
      val mapInputs = Table.MapInput.q.filter(x => x.jobId === job.id && !x.done).sortBy(_ => random).take(max).list()
      if (mapInputs.isEmpty) {
        Table.Job.q.filter(_.id === job.id).map(_.state).update(1)
        fromIntermediates(job)
      } else {
        mapInputs
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

  def completeMapInput(jobId: Long, id: Long, kvs: Iterable[(String, String)]) = DB.withTransaction { implicit session =>
  	
  }

  def completeIntermediate(jobId: Long, id: Long, kvs: Iterable[(String, String)]) = DB.withTransaction { implicit session =>

  }

}