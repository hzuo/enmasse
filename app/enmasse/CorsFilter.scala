package enmasse

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._

import play.api.mvc._

object CorsFilter extends Filter {

  override def apply(nextFilter: RequestHeader => Future[SimpleResult])(requestHeader: RequestHeader): Future[SimpleResult] = {
    nextFilter(requestHeader).map { result =>
      result.withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Expose-Headers" -> "WWW-Authenticate, Server-Authorization",
        "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS, PUT, DELETE",
        "Access-Control-Allow-Headers" -> "x-requested-with,content-type,Cache-Control,Pragma,Date")
    }
  }

}