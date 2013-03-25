package db

import slick.lifted.ColumnBase
import scala.slick.driver.PostgresDriver.simple._
import java.sql.Date


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 12.03.13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
case class UserExam(id: Option[Long], studentId: Long, examID: Long, begDate: Date, endDate: Date, pass: String) {

}

object UserExam extends Table[UserExam]("userExams") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def studentId = column[Long]("studentId")

  def examId = column[Long]("examId")

  def begDate = column[Date]("begDate")

  def endDate = column[Date]("endDate")

  def pass = column[String]("pass")

  def student = foreignKey("stundet", studentId, Student)(_.id)

  def exam = foreignKey("exam", examId, Exam)(_.id)


  def * : ColumnBase[UserExam] = id.? ~ studentId ~ examId ~ begDate ~ endDate ~ pass <>(apply _, unapply _)
}
