package enmasse

import java.sql.Date

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc._

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
    type Row = Schema.Input
    class Tbl(tag: Tag) extends Table[Row](tag, "map_input") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def jobId = column[Long]("job_id")
      def done = column[Boolean]("done")
      def * = (id, k, v, jobId, done) <> (Schema.Input.tupled, Schema.Input.unapply)
    }
    override val q = TableQuery[Tbl]
  }

  object Intermediate extends PostgresTable {
    type Row = Schema.Input
    class Tbl(tag: Tag) extends Table[Row](tag, "intermediate") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def jobId = column[Long]("job_id")
      def done = column[Boolean]("done")
      def * = (id, k, v, jobId, done) <> (Schema.Input.tupled, Schema.Input.unapply)
    }
    override val q = TableQuery[Tbl]
  }

  object ReduceOuput extends PostgresTable {
    type Row = Schema.Output
    class Tbl(tag: Tag) extends Table[Row](tag, "reduce_output") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def jobId = column[Long]("job_id")
      def * = (id, k, v, jobId) <> (Schema.Output.tupled, Schema.Output.unapply)
    }
    override val q = TableQuery[Tbl]
  }

}