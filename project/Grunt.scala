import play.PlayRunHook
import sbt._

object Grunt {

  def apply(base: File): PlayRunHook = {
    new PlayRunHook {
      var process: Option[Process] = None

      override def beforeStarted(): Unit = {
        Process("grunt dev", base).!
        process = Some(Process("grunt watch:inside", base).run)
      }

      override def afterStopped(): Unit = {
        process.map(p => p.destroy())
        process = None
      }
    }
  }

}