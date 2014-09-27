package enmasse

import java.sql.Date

import scala.slick.driver.PostgresDriver.simple._

object Table {

  object Job extends PostgresTable {
    type Row = Schema.Job
    class Tbl(tag: Tag) extends Table[Row](tag, "job") {
      def id = column[Long]("id")
      def data = column[String]("data")
      def state = column[Int]("state")
      def * = (id, data, state) <> (Schema.Job.tupled, Schema.Job.unapply)
    }
    override val q = TableQuery[Tbl]
  }

  object MapInput extends PostgresTable {
    type Row = Schema.MapInput
    class Tbl(tag: Tag) extends Table[Row](tag, "map_input") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def jobId = column[Long]("job_id")
      def done = column[Boolean]("done")
      def * = (id, k, v, jobId, done) <> (Schema.MapInput.tupled, Schema.MapInput.unapply)
    }
    override val q = TableQuery[Tbl]
  }

  object Intermediate extends PostgresTable {
    type Row = Schema.Intermediate
    class Tbl(tag: Tag) extends Table[Row](tag, "intermediate") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def jobId = column[Long]("job_id")
      def done = column[Boolean]("done")
      def * = (id, k, v, jobId, done) <> (Schema.Intermediate.tupled, Schema.Intermediate.unapply)
    }
    override val q = TableQuery[Tbl]
  }

  object ReduceOuput extends PostgresTable {
    type Row = Schema.ReduceOutput
    class Tbl(tag: Tag) extends Table[Row](tag, "reduce_output") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def jobId = column[Long]("job_id")
      def * = (id, k, v, jobId) <> (Schema.ReduceOutput.tupled, Schema.ReduceOutput.unapply)
    }
    override val q = TableQuery[Tbl]
  }

}