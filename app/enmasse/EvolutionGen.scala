package enmasse

import scala.slick.driver.PostgresDriver.simple._

object EvolutionGen extends App {

  val xs = List[PostgresTable](Table.Job, Table.File, Table.MapInput, Table.Intermediate, Table.ReduceOutput)

  val ups = xs.map(_.q.ddl.createStatements).flatten

  println(ups.mkString("# --- !Ups\n\n", ";\n\n", ";\n\n"))

  val downs = xs.reverse.map(_.q.ddl.dropStatements).flatten

  println(downs.mkString("# --- !Downs\n\n", ";\n\n", ";\n\n"))

}