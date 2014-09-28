package enmasse

import java.sql.BatchUpdateException
import java.util.Date

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

import play.api._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {

  def index = Action {

    Ok(views.html.index("Your new application is ready."))
  }

  def preflight(path: String) = Action {
    Ok.withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Allow" -> "*",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Origin, X-PINGOTHER, Content-Type, Accept, Referer, User-Agent");
  }

  def addJob = Action.async(parse.tolerantJson) { request =>
    request.body.validate[External.AddJob] match {
      case s: JsSuccess[External.AddJob] =>
        val spec = s.value
        Store.download(spec.dataOrigin).map { data =>
          try {
            Store.createJob(spec.name, spec.dataOrigin, data, spec.map, spec.reduce)
          } catch {
            case e: BatchUpdateException => e.getNextException.printStackTrace()
          }
          Ok
        }
      case e: JsError =>
        val js = JsError.toFlatJson(e)
        Logger.error(Json.stringify(js))
        Future.successful {
          BadRequest(js)
        }
    }
  }

  def getJobs = Action {
    val jobs = Store.getJobs
    val externalized = jobs.map {
      case Schema.Job(id: Long, name: String, dataOrigin: String, map: String, reduce: String, createdAt: Long, state: Int) =>
        External.Job(id.toString, name, dataOrigin, map, reduce, createdAt)
    }
    Ok(Json.toJson(externalized))
  }

  def moreTasks = Action {
    import External._
    val jsonOpt = Store.moreTasks(100).map(_ match {
      case Left(mapTasks) => Json.toJson(mapTasks)
      case Right(reduceTasks) => Json.toJson(reduceTasks)
    })
    Ok(Json.toJson(jsonOpt.toList))
  }

  def completeTasks = Action(parse.tolerantJson(maxLength = Int.MaxValue)) { request =>
    request.body.validate[External.TaskSetResult] match {
      case s: JsSuccess[External.TaskSetResult] =>
        val spec = s.value
        val jobId = spec.jobId.toLong
        try {
          if (spec.mode) {
            for (External.TaskGrpResult(preimageKey, emits) <- spec.output) {
              val kvs = for (External.Emit(k, v) <- emits) yield (k, v)
              Store.completeMapInput(jobId, preimageKey, kvs)
            }
          } else {
            for (External.TaskGrpResult(preimageKey, emits) <- spec.output) {
              val kvs = for (External.Emit(k, v) <- emits) yield (k, v)
              Store.completeIntermediate(jobId, preimageKey, kvs)
            }
          }
        } catch {
          case e: BatchUpdateException => e.getNextException.printStackTrace()
        }
        Ok
      case e: JsError =>
        val js = JsError.toFlatJson(e)
        Logger.error(Json.stringify(js))
        BadRequest(js)
    }
  }

}