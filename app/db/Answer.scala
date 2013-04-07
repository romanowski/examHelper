package db

import slick.lifted.ColumnBase
import scala.slick.driver.PostgresDriver.simple._


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 12.03.13
 * Time: 09:02
 * To change this template use File | Settings | File Templates.
 */
case class Answer(override val id: Option[Long], text: String,
                  correct: Boolean, questionId: Long, override val order: Int) extends SchoolEntity[Answer] with OrderedSchoolEntity[Answer] {
  def withId(id: Long): Answer = copy(id = Option(id))

  def withOrder(order: Int): Answer = copy(order = order)

  def orderedFor: Long = questionId
}

import Database.threadLocalSession

object Answer extends Table[Answer]("answers") with OrderedSchoolEntityTable[Answer] {


  def insertApply(text: String, correct: Boolean, questionId: Long, order: Int): Answer =
    Answer(None, text, correct, questionId, order)

  def insertUnapply(ans: Answer) = Some((ans.text, ans.correct, ans.questionId, ans.order))

  def text = column[String]("text")

  def correct = column[Boolean]("correnct")

  def questionId = column[Long]("questionId")

  def question = foreignKey("question", questionId, Question)(_.id)

  def order = column[Int]("order")


  def * : ColumnBase[Answer] = id.? ~ text ~ correct ~ questionId ~ order <>(apply _, unapply _)

  override def insertProjection = text ~ correct ~ questionId ~ order <>(insertApply _, insertUnapply _) returning id

  def forQuestion(id: Long) = session(forQuestionQuery.list(id))

  val forQuestionQuery = for {
    id <- Parameters[Long]
    ans <- Answer if ans.questionId === id
  } yield ans

  def empty(questionId: Long) = Answer(None, "", false, questionId, -1)

  def orderFor: Column[Long] = questionId
}


