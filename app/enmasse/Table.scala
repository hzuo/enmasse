package enmasse

import java.sql.Date

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc._

object Table {

  object Job extends PostgresTable {
    type Row = Schema.Job
    class Tbl(tag: Tag) extends Table[Row](tag, "job") {
      def id = column[Long]("id")
      def name = column[String]("name")
      def dataOrigin = column[String]("data_origin")
      def map = column[String]("map")
      def reduce = column[String]("reduce")
      def createdAt = column[Long]("created_at")
      def state = column[Int]("state")
      def * = (id, name, dataOrigin, map, reduce, createdAt, state) <> (Schema.Job.tupled, Schema.Job.unapply)

      def pk = primaryKey("job_pk", id)
    }
    override val q = TableQuery[Tbl]
  }

  object File extends PostgresTable {
    type Row = Schema.File
    class Tbl(tag: Tag) extends Table[Row](tag, "file") {
      def jobId = column[Long]("job_id")
      def data = column[String]("data", O.DBType("TEXT"))
      def * = (jobId, data) <> (Schema.File.tupled, Schema.File.unapply)

      def fk = foreignKey("file_fk_job", jobId, Job.q)(_.id)
    }
    override val q = TableQuery[Tbl]
  }

  object MapInput extends PostgresTable {
    type Row = Schema.Record
    class Tbl(tag: Tag) extends Table[Row](tag, "map_input") {
      def k = column[String]("k")
      def v = column[String]("v", O.DBType("TEXT"))
      def jobId = column[Long]("job_id")
      def * = (k, v, jobId) <> (Schema.Record.tupled, Schema.Record.unapply)

      def fk = foreignKey("map_input_fk_job", jobId, Job.q)(_.id)
    }
    override val q = TableQuery[Tbl]
  }

  object Intermediate extends PostgresTable {
    type Row = Schema.Record
    class Tbl(tag: Tag) extends Table[Row](tag, "intermediate") {
      def k = column[String]("k")
      def v = column[String]("v", O.DBType("TEXT"))
      def jobId = column[Long]("job_id")
      def * = (k, v, jobId) <> (Schema.Record.tupled, Schema.Record.unapply)

      def fk = foreignKey("intermediate_fk_job", jobId, Job.q)(_.id)
    }
    override val q = TableQuery[Tbl]
  }

  object ReduceOutput extends PostgresTable {
    type Row = Schema.Record
    class Tbl(tag: Tag) extends Table[Row](tag, "reduce_output") {
      def k = column[String]("k")
      def v = column[String]("v")
      def jobId = column[Long]("job_id")
      def * = (k, v, jobId) <> (Schema.Record.tupled, Schema.Record.unapply)

      def fk = foreignKey("reduce_output_fk_job", jobId, Job.q)(_.id)
    }
    override val q = TableQuery[Tbl]
  }

  object JobPulse extends PostgresTable {
    type Row = Schema.JobPulse
    class Tbl(tag: Tag) extends Table[Row](tag, "job_pulse") {
      def jobId = column[Long]("job_id")
      def totalMapTasks = column[Long]("total_map_tasks")
      def totalReduceTasks = column[Long]("total_reduce_tasks")
      def * = (jobId, totalMapTasks, totalReduceTasks) <> (Schema.JobPulse.tupled, Schema.JobPulse.unapply)

      def fk = foreignKey("job_pulse_fk_job", jobId, Job.q)(_.id)
    }
    override val q = TableQuery[Tbl]
  }

}
