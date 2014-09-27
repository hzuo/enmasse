package enmasse

import java.sql.Date

import scala.slick.driver.PostgresDriver.simple._

object Table {

  object Job extends PostgresTable {
    type Row = Schema.Job
    class Tbl(tag: Tag) extends Table[Row](tag, "job") {
      def id = column[Long]("id")
      def data = column[String]("data")
      def * = (id, data) <> (Schema.Job.tupled, Schema.Job.unapply)
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
      def * = (id, k, v, jobId) <> (Schema.MapInput.tupled, Schema.MapInput.unapply)
    }
    override val q = TableQuery[Tbl]
  }

  object Intermediate extends PostgresTable {
    type Row = Schema.Intermediate
    class Tbl(tag: Tag) extends Table[Row](tag, "intermediate") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def mapInputId = column[Long]("map_input_id")
      def * = (id, k, v, mapInputId) <> (Schema.Intermediate.tupled, Schema.Intermediate.unapply)
    }
    override val q = TableQuery[Tbl]
  }

  object ReduceOuput extends PostgresTable {
    type Row = Schema.ReduceOutput
    class Tbl(tag: Tag) extends Table[Row](tag, "intermediate") {
      def id = column[Long]("id")
      def k = column[String]("k")
      def v = column[String]("v")
      def intermediateId = column[Long]("intermediate_id")
      def * = (id, k, v, intermediateId) <> (Schema.ReduceOutput.tupled, Schema.ReduceOutput.unapply)
    }
    override val q = TableQuery[Tbl]
  }

}