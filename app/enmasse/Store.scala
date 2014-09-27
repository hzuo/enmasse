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
      case (v, k) => Schema.MapInput(Random.nextLong(), k.toString, v, job.id, false)
    }.toIterable
    DB.withTransaction { implicit session =>
      Table.Job.q += job
      Table.MapInput.q ++= mapInputs
    }
  }

  def moreTasks(max: Int) = DB.withTransaction { implicit session =>
    val jobOpt = Table.Job.q.filter(_.state =!= 2).firstOption()
    jobOpt.toList.flatMap { job =>
      job.state match {
        case 0 =>
          val pending = Table.MapInput.q.filter(!_.done).take(max).list()
          if (pending.isEmpty)
            throw new AssertionError
          pending
        case 1 =>
          val pending = Table.Intermediate.q.filter(!_.done).take(max).list()
          if (pending.isEmpty)
            throw new AssertionError
          pending
        case _ =>
          throw new AssertionError
      }
    }
  }

}