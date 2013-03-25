package db

import slick.lifted.ColumnBase
import scala.slick.driver.PostgresDriver.simple._
import slick.driver.BasicInvokerComponent

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 12.03.13
 * Time: 08:59
 * To change this template use File | Settings | File Templates.
 */
case class Question(override val id: Option[Long], question: String, examId: Long) extends SchoolEntity[Question] {
  def withId(id: Long): Question = copy(id = Some(id))
}

import Database.threadLocalSession


object Question extends Table[Question]("questions") with SchoolEntityTable[Question] {

  def insertApply(question: String, examId: Long) = Question(None, question, examId)

  def insertUnapply(q: Question) = Some(q.question -> q.examId)

  def question = column[String]("question")

  def examId = column[Long]("examId")

  def exam = foreignKey("exam", examId, Exam)(_.id)

  def * : ColumnBase[Question] = id.? ~ question ~ examId <>(apply _, unapply _)

  def insertProjection: BasicInvokerComponent#KeysInsertInvoker[Question, Long] =
    question ~ examId <>(insertApply _, insertUnapply _) returning id

  def forExam(examId: Long): Seq[Question] = session(forExamQuery.list(examId))

  val forExamQuery = for {
    id <- Parameters[Long]
    q <- Question if q.examId === id
  } yield q

  def empty(examId: Long) = Question(None, "", examId)
}
