package db

import slick.lifted.ColumnBase
import scala.slick.driver.PostgresDriver.simple._

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 12.03.13
 * Time: 16:17        
 * To change this template use File | Settings | File Templates.
 */
case class UserAnswer(id: Option[Long], answerId: Long, questionId: Long, userExamId: Long) {

}

object UserAnswer extends Table[UserAnswer]("userAnswers") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def answerId = column[Long]("answerId")

  def questionId = column[Long]("questionId")

  def userExamId = column[Long]("userExamId")

  def * : ColumnBase[UserAnswer] = id.? ~ answerId ~ questionId ~ userExamId <>(apply _, unapply _)
}
