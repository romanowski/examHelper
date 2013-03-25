package controllers

import play.api.data._
import play.api.data.Forms._
import db.Question
import play.api.templates.Html

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 22.03.13
 * Time: 22:20
 * To change this template use File | Settings | File Templates.
 */
object QuestionsController extends EntityController[Question] {


  override val formName: String = "qest_form"
  override val table = Question

  override def form(question: Question) = Form(
    mapping(
      "question" -> nonEmptyText
    ) {
      q => question.copy(question = q)
    } {
      q => Some(q.question)
    }
  )

  override def htmlView(v: Question)(implicit req: Utils.Req): Html = views.html.question.view(v)

  override def editHtml(id: Any)(v: Form[Question])(implicit req: Utils.Req): Html = views.html.question.edit(formName, id)(v)

  def newQuestion(examId: String) = newAction(Question.empty(examId.toLong), routes.ExamsController.examView(examId) + "#questions")
}
