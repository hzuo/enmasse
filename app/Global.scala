import play.api._
import play.api.mvc._

object Global extends WithFilters(enmasse.CorsFilter) with GlobalSettings