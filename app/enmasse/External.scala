package enmasse

import java.sql.Date

import play.api.libs.functional.syntax._
import play.api.libs.json._

object External {

  case class AddJob(name: String, dataOrigin: String, map: String, reduce: String)
  case class Job(id: Long, name: String, dataOrigin: String, map: String, reduce: String, createdAt: Long)

  implicit val addJobFmt = Json.format[AddJob]
  implicit val jobFmt = Json.format[Job]

  case class Task(id: Long, k: String, v: String)
  case class TaskSet(mode: Boolean, fn: String, tasks: Seq[Task])

  implicit val taskFmt = Json.format[Task]
  implicit val taskSetFmt = Json.format[TaskSet]

  case class Emit(k: String, v: String)
  case class TaskGrpResult(id: Long, emits: Seq[Emit])
  case class TaskSetResult(attractorId: Long, mode: Boolean, output: Seq[TaskGrpResult])

  implicit val emitFmt = Json.format[Emit]
  implicit val taskGroupResultFmt = Json.format[TaskGrpResult]
  implicit val taskSetResultFmt = Json.format[TaskSetResult]

}