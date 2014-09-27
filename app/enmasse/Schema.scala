package enmasse

object Schema {

  case class Job(id: Long, data: String)
  case class MapInput(id: Long, k: String, v: String, jobId: Long)
  case class Intermediate(id: Long, k: String, v: String, mapInputId: Long)
  case class ReduceOutput(id: Long, k: String, v: String, intermediateId: Long)

}