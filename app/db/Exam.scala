package db

import org.joda.time.{Period, DateTime}
import slick.lifted.ColumnBase
import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._
import slick.driver.BasicInvokerComponent

case class Exam(date: DateTime, title: String, desc: String, ownerId: Long, duration: Int, override val id: Option[Long] = None) extends SchoolEntity[Exam] {
  def withId(id: Long): Exam = copy(id = Option(id))

  lazy val isPending = started && date.plus(Period.minutes(duration)).isBefore(new DateTime())

  lazy val started = date.isBefore(new DateTime())

}


import Database.threadLocalSession

object Exam extends Table[Exam]("exams") with SchoolEntityTable[Exam] {

  def insertApply(sqlDate: Timestamp, title: String, desc: String, ownerId: Long, duration: Int) =
    new Exam(new DateTime(sqlDate), title, desc, ownerId, duration, None)

  def insertUnApply(e: Exam) = Some((new Timestamp(e.date.getMillis), e.title, e.desc, e.ownerId, e.duration))


  def sqlApply(sqlDate: Timestamp, title: String, desc: String, ownerId: Long, duration: Int, id: Option[Long]) =
    new Exam(new DateTime(sqlDate), title, desc, ownerId, duration, id)

  def sqlUnApply(e: Exam) = Some((new Timestamp(e.date.getMillis), e.title, e.desc, e.ownerId, e.duration, e.id))


  def date = column[Timestamp]("date")

  def title = column[String]("title")

  def desc = column[String]("desc")

  def ownerId = column[Long]("ownerId")

  def owner = foreignKey("owner", ownerId, Teacher)(_.id)

  def duration = column[Int]("duration")


  def * : ColumnBase[Exam] = date ~ title ~ desc ~ ownerId ~ duration ~ id.? <>(sqlApply _, sqlUnApply _)

  def insertProjection: BasicInvokerComponent#KeysInsertInvoker[Exam, Long] =
    date ~ title ~ desc ~ ownerId ~ duration <>(insertApply _, insertUnApply _) returning id

  def notStarterFor(t: Teacher) = session {
    StartedExamForTeacherQuery.list(t.id_! -> new Timestamp(System.currentTimeMillis()))
  }

  def endedFor(t: Teacher) = session {
    NotStartedExamForTeacherQuery.list(t.id_! -> new Timestamp(System.currentTimeMillis()))
      .filterNot(_.isPending)
  }

  def pendingFor(t: Teacher) = session {
    NotStartedExamForTeacherQuery.list(t.id_! -> new Timestamp(System.currentTimeMillis()))
      .filter(_.isPending)
  }

  /*  def save(e: Exam) = session {
      autoInc.insert(e)
      e
    }*/

  val StartedExamForTeacherQuery = for {
    (teacherID, startDate) <- Parameters[(Long, Timestamp)]
    exam <- Exam if (exam.ownerId === teacherID) && (exam.date > startDate)
  } yield exam
  val NotStartedExamForTeacherQuery = for {
    (teacherID, startDate) <- Parameters[(Long, Timestamp)]
    exam <- Exam if (exam.ownerId === teacherID) && (exam.date < startDate)
  } yield exam

  def empty(teacherId: Long) = Exam(new DateTime(), "", "", teacherId, 0, None)

}
