package controllers

import db.{SchoolEntityTable, StudentExam}
import play.api.data.Form
import play.api.templates.Html
import play.api.data.Forms._
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 07.04.13
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
object StudentExamController extends EntityController[StudentExam] {
  val formName: String = "studnetExam"

  val table: SchoolEntityTable[StudentExam] = StudentExam

  def form(v: StudentExam): Form[StudentExam] = Form(
    mapping(
      "name" -> nonEmptyText,
      "login" -> nonEmptyText,
      "password" -> nonEmptyText
    ) {
      (name, login, pass) => v.copy(name = name, login = login, password = pass)
    } {
      q => Some((q.name, q.login, q.password))
    }
  )

  def htmlView(v: StudentExam)(implicit req: Utils.Req): Html = views.html.studentExam.view(v)

  def editHtml(id: Any)(v: Form[StudentExam])(implicit req: Utils.Req): Html = views.html.studentExam.edit(formName, id)(v)

  def newStudent(examId: String) =
    newAction(StudentExam.empty(examId.toLong), routes.ExamsController.examView(examId).url + "#studnets")

}
