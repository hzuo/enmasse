package enmasse

object Schema {

  case class Job(id: Long, name: String, dataOrigin: String, map: String, reduce: String, createdAt: Long, state: Int)
  case class File(jobId: Long, content: String)
  case class Record(k: String, v: String, jobId: Long)

}