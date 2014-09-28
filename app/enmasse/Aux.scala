package enmasse

sealed trait Mode
case object Map extends Mode
case object Reduce extends Mode

sealed trait Task
case class MapTask(k: String, v: String) extends Task
case class ReduceTask(k: String, v: Seq[String]) extends Task

case class MapTasks(jobId: String, mode: Boolean, fn: String, tasks: Iterable[MapTask])
case class ReduceTasks(jobId: String, mode: Boolean, fn: String, tasks: Iterable[ReduceTask])
