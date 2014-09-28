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

  import External._

  def addJob = Action.async(parse.tolerantJson) { request =>
    request.body.validate[AddJob] match {
      case s: JsSuccess[AddJob] =>
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
	
    Ok
  }

}
