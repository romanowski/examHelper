package controllers

import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import db.Exam
import Application._
import play.api.templates.Html

object ExamsController extends EntityController[Exam] {


  override val formName: String = "exam_form"
  override val table = Exam

  override def form(exam: Exam): Form[Exam] = Form(mapping(
    "title" -> nonEmptyText,
    "desc" -> text,
    "startDate" -> text.verifying("nie poprawny format daty: dd-mm-yyyy", Utils.isValid(Utils.dateFormat) _),
    "startTime" -> text.verifying("nie poprawny format godziny: mm:hh", Utils.isValid(Utils.timeFormat) _),
    "duration" -> number
  ) {
    case (title, desc, startDate, startTime, duration) =>
      exam.copy(title = title,
        desc = desc,
        date = Utils.parseDate(startTime, startDate),
        duration = duration)
  }(exam =>
    Some(exam.title, exam.desc, Utils.getDate(exam.date), Utils.getTime(exam.date), exam.duration))
  ).fill(exam)

  override def htmlView(v: Exam)(implicit req: Utils.Req): Html = views.html.exam.view(v)

  override def editHtml(id: Any)(v: Form[Exam])(implicit req: Utils.Req): Html = views.html.exam.edit(formName, id)(v)


  def newExam = newAction(Exam.empty _, routes.Application.index.url)

  //## view exam
  def examView(id: String) = {
    IsAuthenticated {
      teacherID => implicit request =>
        Exam.byId(id.toLong).map(
          exam => Ok(views.html.exam.fullView(exam))
        ).getOrElse(Redirect(routes.Application.login))
    }
  }
}
