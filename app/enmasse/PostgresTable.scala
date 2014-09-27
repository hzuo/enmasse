package enmasse

import scala.slick.driver.PostgresDriver.simple._

trait PostgresTable {

  type Row
  type Tbl <: Table[Row]

  def q: TableQuery[Tbl]

}