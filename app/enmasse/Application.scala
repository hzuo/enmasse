package enmasse

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
          Store.createJob(spec.name, spec.dataOrigin, data, spec.map, spec.reduce)
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
        External.Job(id, name, dataOrigin, map, reduce, createdAt)
    }
    Ok(Json.toJson(externalized))
  }

  def moreTasks = Action {
    val ((mode, fn), tasks) = Store.moreTasks(1000)
    val modeFlag = mode match {
      case Map => true
      case Reduce => false
    }
    val externalizedTasks = tasks.map {
      case Schema.Input(id, k, v, jobId, done) =>
        External.Task(id, k, v)
    }
    val taskSet = External.TaskSet(modeFlag, fn, externalizedTasks)
    Ok(Json.toJson(taskSet))
  }

  def completeTasks = Action(parse.tolerantJson) { request =>
    request.body.validate[External.TaskSetResult] match {
      case s: JsSuccess[External.TaskSetResult] =>
        val spec = s.value
        if (spec.mode) {
          for (External.TaskGrpResult(id, emits) <- spec.output) {
            val kvs = for (External.Emit(k, v) <- emits) yield (k, v)
            Store.completeMapInput(id, kvs)
          }
        } else {
          for (External.TaskGrpResult(id, emits) <- spec.output) {
            val kvs = for (External.Emit(k, v) <- emits) yield (k, v)
            Store.completeIntermediate(id, kvs)
          }
        }
        Ok
      case e: JsError =>
        val js = JsError.toFlatJson(e)
        Logger.error(Json.stringify(js))
        BadRequest(js)
    }
  }

}