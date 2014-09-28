package enmasse

import java.sql.Date

import play.api.libs.functional.syntax._
import play.api.libs.json._

object External {

  case class AddJob(name: String, dataOrigin: String, map: String, reduce: String)
  case class Job(id: Long, name: String, dataOrigin: String, map: String, reduce: String, createdAt: Long)

  implicit val addJobFmt = Json.format[AddJob]
  implicit val jobFmt = Json.format[Job]

}