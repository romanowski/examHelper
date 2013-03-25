import db._
import play.api.db.DB
import play.api.GlobalSettings
import utils.TRY

// Use H2Driver to connect to an H2 database

import scala.slick.driver.PostgresDriver.simple._

// Use the implicit threadLocalSession

import Database.threadLocalSession

import play.api.Application
import play.api.Play.current


object Global extends GlobalSettings {


  override def onStart(app: Application) {
    lazy val database = Database.forDataSource(DB.getDataSource())

    database.withSession {
      val tables = List(Answer, Question, Exam, Teacher)

      val data = tables.flatMap(t => TRY(t -> t.backup))

      tables.foreach {
        el => try {
          el.ddl.drop
        } catch {
          case e => e.printStackTrace
        }
      }
      tables.reverse.foreach(_.ddl.create)

      data.reverse.foreach {
        case (t, data) => t.restore(data)
      }
      initAdmin()
    }
  }

  def initAdmin() {
    Teacher.admin
  }

}