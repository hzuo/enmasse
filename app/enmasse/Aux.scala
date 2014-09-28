package enmasse

sealed trait Mode
case object Map extends Mode
case object Reduce extends Mode

sealed trait Task
case class MapTask(k: String, v: String) extends Task
case class ReduceTask(k: String, v: Seq[String]) extends Task

case class Tasks(mode: Mode, fn: String, tasks: Iterable[Task])
