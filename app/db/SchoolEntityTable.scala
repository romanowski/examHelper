package db

import play.api.db.DB
import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import slick.driver.{BasicQueryTemplate, BasicInvokerComponent}

trait SchoolEntityTable[T <: SchoolEntity[T]] {
  self: Table[T] =>

  import Database.threadLocalSession

  def session[T](f: => T): T = {
    SchoolEntityTable.database.withSession {
      f
    }
  }

  def insertProjection: BasicInvokerComponent#KeysInsertInvoker[T, Long]


  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

  val byIdQuery: BasicQueryTemplate[Long, T] = for {
    id <- Parameters[Long]
    u <- this if u.id === id
  } yield u

  def save(v: T): T = session {
    v.id.map {
      id => byIdQuery(id).mutate(f => f.row = v)
      v
    }.getOrElse {
      v.withId(insertProjection.insert(v))
    }
  }

  def byId(id: Long) = session(byIdQuery.firstOption(id))

  def backup: Seq[SchoolEntity[_]] = Query(this).list()

  def restore(data: Seq[SchoolEntity[_]]) = data.foreach {
    data => *.insert(data.asInstanceOf[T])
  }


}

trait SchoolEntity[T] {
  val id: Option[Long]

  lazy val id_! = id.getOrElse(-1L)

  def withId(id: Long): T
}


object SchoolEntityTable {
  lazy val database = Database.forDataSource(DB.getDataSource())

}