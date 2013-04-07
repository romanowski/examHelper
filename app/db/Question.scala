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
case class Question(override val id: Option[Long], question: String, examId: Long, override val order: Int) extends SchoolEntity[Question] with OrderedSchoolEntity[Question] {

  def withId(id: Long): Question = copy(id = Some(id))

  def withOrder(order: Int): Question = copy(order = order)

  def orderedFor: Long = examId
}

import Database.threadLocalSession


object Question extends Table[Question]("questions") with OrderedSchoolEntityTable[Question] {

  def insertApply(question: String, examId: Long, order: Int) = Question(None, question, examId, order)

  def insertUnapply(q: Question) = Some((q.question, q.examId, q.order))

  def question = column[String]("question")

  def examId = column[Long]("examId")

  def exam = foreignKey("exam", examId, Exam)(_.id)

  def order = column[Int]("order")

  def * : ColumnBase[Question] = id.? ~ question ~ examId ~ order <>(apply _, unapply _)

  def insertProjection: BasicInvokerComponent#KeysInsertInvoker[Question, Long] =
    question ~ examId ~ order <>(insertApply _, insertUnapply _) returning id

  def forExam(examId: Long): Seq[Question] = session(forExamQuery.list(examId))

  val forExamQuery = for {
    id <- Parameters[Long]
    q <- Question if q.examId === id
    _ <- Query.orderBy(q.order)
  } yield q

  def empty(examId: Long) = Question(None, "", examId, 0)

  override def orderFor: Column[Long] = examId
}


