package controllers

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import db.SchoolEntity
import play.api.mvc._

object Utils {
  val datetimeFormat = DateTimeFormat.forPattern("HH:mm dd-MM-yyyy")
  val dateFormat = DateTimeFormat.forPattern("dd-MM-yyyy")
  val timeFormat = DateTimeFormat.forPattern("HH:mm")


  def printDate(date: DateTime) = datetimeFormat.print(date)

  def parseDate(time: String, date: String): DateTime = {
    datetimeFormat.parseDateTime(time + " " + date).withSecondOfMinute(0)
  }

  def getTime(date: DateTime) = timeFormat.print(date)

  def getDate(date: DateTime) = dateFormat.print(date)

  def isValid(format: DateTimeFormatter)(text: String): Boolean = try {
    format.parseDateTime(text)
    true
  } catch {
    case _: Throwable => false
  }

  def entityEdiParam(entity: SchoolEntity[_]) = (entity.getClass.getSimpleName + "_edit_id")

  private def path(path: String, params: Map[String, Seq[String]]) =
    "%s?%s".format(path, (params).map {
      case (name, params) => "%s=%s".format(name, params.mkString(""))
    }.mkString("&"))

  def editPath(formName: String, value: Any)(implicit req: Request[AnyContent]) = {
    path(req.path, req.queryString + (formName -> Seq(value.toString)))
  }

  def removeEditPath(name: String)(implicit req: Request[AnyContent]): String = {
    path(req.path, req.queryString.filter(_._1 != name))

  }

  def extractEdit(formName: String, value: String)(implicit req: Request[AnyContent]): Option[String] = {
    req.getQueryString(formName)
      .orElse(req.body.asFormUrlEncoded.flatMap(_.get(formName).flatMap(_.headOption)))
      .filter(value ==)
  }

  type Req = Request[AnyContent]
}
