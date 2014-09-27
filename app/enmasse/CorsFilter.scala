package enmasse

import scala.concurrent.ExecutionContext.Implicits._

import play.api.mvc._

object CorsFilter extends Filter {

  def apply(next: (RequestHeader) => Result)(rh: RequestHeader) = {
    def cors(result: PlainResult): Result = {
      result.withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS, PUT, DELETE",
        "Access-Control-Allow-Headers" -> "x-requested-with,content-type,Cache-Control,Pragma,Date")
    }
    next(rh) match {
      case plain: PlainResult => cors(plain)
      case async: AsyncResult => async.transform(cors)
    }
  }

}