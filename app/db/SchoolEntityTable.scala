package db

import play.api.db.DB
import scala.slick.driver.PostgresDriver.simple._
import play.api.Play.current
import slick.driver.{BasicQueryTemplate, BasicInvokerComponent}
import Database.threadLocalSession

trait SchoolEntityTable[T <: SchoolEntity[T]] {
  self: Table[T] =>


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
    v.id.map(update(v)).getOrElse(saveNew(v))
  }

  def update(v: T)(id: Long): T = {
    byIdQuery(id).mutate(f => f.row = v)
    v
  }

  def saveNew(v: T): T = v.withId(insertProjection.insert(v))

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

trait OrderedSchoolEntity[T] {
  val order: Int

  def withOrder(order: Int): T

  def orderedFor: Long
}

trait OrderedSchoolEntityTable[T <: SchoolEntity[T] with OrderedSchoolEntity[T]] extends SchoolEntityTable[T] {
  self: Table[T] =>

  def orderFor: Column[Long]


  def nextPosQuery(forId: Long) = for {
    v <- self if v.orderFor === forId
  } yield v.id

  def nextPos(forId: Long) = Query(nextPosQuery(forId).length).first()

  override def saveNew(v: T): T = super.saveNew(v.withOrder(nextPos(v.orderedFor)))
}


object SchoolEntityTable {
  lazy val database = Database.forDataSource(DB.getDataSource())

}