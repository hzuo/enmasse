package enmasse

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc._

trait PostgresTable {

  type Row
  type Tbl <: Table[Row]

  def q: TableQuery[Tbl]

}