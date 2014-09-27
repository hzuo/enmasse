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
    val jobId = Random.nextLong()
    val mapInputs = io.Source.fromString(data).getLines.zipWithIndex.map {
      case (v, k) => Schema.MapInput(Random.nextLong(), k.toString, v, jobId, false)
    }.toIterable
    DB.withTransaction { implicit session =>
      Table.Job.q += Schema.Job(jobId, data)
      Table.MapInput.q ++= mapInputs
    }
  }

}