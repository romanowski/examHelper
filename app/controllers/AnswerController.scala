package controllers

import play.api.data.Form
import play.api.data.Forms._
import db.Answer


object AnswerController extends EntityController[Answer] {

  val formName = "ans_form"


  override val table = Answer

  def form(ans: Answer): Form[Answer] = Form(mapping(
    "text" -> nonEmptyText,
    "correct" -> boolean
  ) {
    case (text, correct) =>
      ans.copy(text = text, correct = correct)
  } {
    ans => Some((ans.text, ans.correct))
  })

  override def htmlView(v: Answer)(implicit req: Utils.Req) = views.html.answer.view(v)

  override def editHtml(id: Any)(v: Form[Answer])(implicit req: Utils.Req) = views.html.answer.edit(formName)(id)(v)

  def newAnswer(examId: String, questionId: String) =
    newAction(Answer.empty(questionId.toLong), routes.ExamsController.examView(examId).url + "#questions")

}
