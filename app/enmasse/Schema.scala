package enmasse

object Schema {

  case class Job(id: Long, data: String)
  case class MapInput(id: Long, k: String, v: String, jobId: Long, done: Boolean)
  case class Intermediate(id: Long, k: String, v: String, jobId: Long, done: Boolean)
  case class ReduceOutput(id: Long, k: String, v: String, jobId: Long)

}