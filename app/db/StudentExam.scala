package db

import slick.lifted.ColumnBase
import scala.slick.driver.PostgresDriver.simple._
import slick.driver.BasicInvokerComponent
import utils.Randoms


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 12.03.13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
case class StudentExam(override val id: Option[Long], name: String, login: String, password: String, examID: Long) extends SchoolEntity[StudentExam] {
  def withId(id: Long): StudentExam = copy(id = Some(id))
}

import Database.threadLocalSession


object StudentExam extends Table[StudentExam]("userExams") with SchoolEntityTable[StudentExam] {

  def name = column[String]("name")

  def login = column[String]("login")

  def password = column[String]("password")

  def examId = column[Long]("examId")


  def exam = foreignKey("exam", examId, Exam)(_.id)

  def * : ColumnBase[StudentExam] = id.? ~ name ~ login ~ password ~ examId <>(apply _, unapply _)

  def insertApply(name: String, login: String, password: String, examID: Long): StudentExam =
    StudentExam(None, name, login, password, examID)

  def insertUnApply(se: StudentExam) = Some((se.name, se.login, se.password, se.examID))

  def insertProjection: BasicInvokerComponent#KeysInsertInvoker[StudentExam, Long] =
    name ~ login ~ password ~ examId <>(insertApply _, insertUnApply _) returning id

  def empty(examId: Long): StudentExam = StudentExam(None, "", Randoms.string(5), Randoms.string(5), examId)


  val forExamQuery = for {
    examId <- Parameters[Long]
    se <- StudentExam if se.examId === examId
  } yield se

  def forExam(examId: Long) = session {
    forExamQuery.list(examId)
  }
}
