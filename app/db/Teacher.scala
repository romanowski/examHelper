package db

import slick.lifted.ColumnBase
import scala.slick.driver.PostgresDriver.simple._
import play.api.db._
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 12.03.13
 * Time: 08:29
 * To change this template use File | Settings | File Templates.
 */
case class Teacher(override val id: Option[Long], name: String, email: String, password: String) extends SchoolEntity[Teacher] {

  def matchPass(pass: String) = pass == password

  override def withId(id: Long): Teacher = copy(id = Option(id))
}

import Database.threadLocalSession

object Teacher extends Table[Teacher]("teachers") with SchoolEntityTable[Teacher] {

  def insertApply(name: String, email: String, password: String): Teacher = Teacher(None, name, email, password)

  def insertUnApply(t: Teacher) = Some(t.name, t.email, t.password)


  lazy val database = Database.forDataSource(DB.getDataSource())


  def name = column[String]("name")

  def email = column[String]("email")

  def password = column[String]("password")

  def * : ColumnBase[Teacher] = id.? ~ name ~ email ~ password <>(apply _, unapply _)

  override def insertProjection = name ~ email ~ password <>(insertApply _, insertUnApply _) returning id


  def authenticate(email: String, pass: String): Option[Teacher] = session {
    byEmailQuery.list(email).headOption.filter(_.matchPass(pass))
  }

  def authForForm(email: String, pass: String): (String, Option[Long]) = authenticate(email, pass).map(th => th.email -> th.id).getOrElse(email -> None)


  def admin: Teacher = Teacher.byEmailQuery("a@a.pl").list().headOption.getOrElse {
    Teacher.insert(
      Teacher.apply(id = Some(1l),
        name = "Ala maKota",
        email = "a@a.pl",
        password = "ala123"))
    Teacher.byEmailQuery("a@a.pl").first()
  }

  val byEmailQuery = for {
    email <- Parameters[String]
    teacher <- Teacher if teacher.email === email
  } yield teacher
}