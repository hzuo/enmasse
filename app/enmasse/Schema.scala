package enmasse

object Schema {

  case class Job(id: Long, data: String, state: Int)
  case class Input(id: Long, k: String, v: String, jobId: Long, done: Boolean)
  case class Output(id: Long, k: String, v: String, jobId: Long)

}