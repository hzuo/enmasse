package enmasse

import java.sql.Date

import play.api.libs.functional.syntax._
import play.api.libs.json._

object External {

  case class AddJob(name: String, dataOrigin: String, map: String, reduce: String)
  case class Job(id: Long, name: String, dataOrigin: String, map: String, reduce: String, createdAt: Long)

  implicit val addJobFmt = Json.format[AddJob]
  implicit val jobFmt = Json.format[Job]

  implicit val mapTaskFmt = Json.format[MapTask]
  implicit val reduceTaskFmt = Json.format[ReduceTask]
  implicit val mapTasksFmt = Json.format[MapTasks]
  implicit val reduceTasksFmt = Json.format[ReduceTasks]

  case class Emit(k: String, v: String)
  case class TaskGrpResult(preimageKey: String, emits: Seq[Emit])
  case class TaskSetResult(attractorId: String, jobId: Long, mode: Boolean, output: Seq[TaskGrpResult])

  implicit val emitFmt = Json.format[Emit]
  implicit val taskGroupResultFmt = Json.format[TaskGrpResult]
  implicit val taskSetResultFmt = Json.format[TaskSetResult]

}