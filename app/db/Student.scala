package db

import slick.lifted.ColumnBase
import scala.slick.driver.PostgresDriver.simple._

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 12.03.13
 * Time: 08:29
 * To change this template use File | Settings | File Templates.
 */
case class Student(id: Option[Long], name: String, surname: String) {
}


object Student extends Table[Student]("students") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def surname = column[String]("surname")


  def * : ColumnBase[Student] = id.? ~ name ~ surname <>(apply _, unapply _)
}