package controllers

import db._
import play.api.data.Form
import play.api.templates.Html
import controllers.Application._
import play.api.mvc.EssentialAction

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 24.03.13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
trait EntityController[T <: SchoolEntity[T]] {

  val formName: String

  val table: SchoolEntityTable[T]

  def form(v: T): Form[T]


  def htmlView(v: T)(implicit req: Utils.Req): Html

  def editHtml(id: Any)(v: Form[T])(implicit req: Utils.Req): Html

  lazy val newPageTitle = "dodaj nowe " + table.getClass


  def createHtml(form: Form[T])(implicit req: Utils.Req): Html = views.html.main(newPageTitle)(editHtml("")(form))

  def view(v: T)(implicit req: Utils.Req) = Utils.extractEdit(formName, v.id_!.toString).map {
    el => form(v).bindFromRequest().fold(
      err => editHtml(v.id_!.toString)(err.fill(v)),
      newVal => {
        table.save(newVal)
        htmlView(newVal)
      }
    )
  }.getOrElse {
    htmlView(v)
  }


  def newAction(empty: T, redirectUrl: String): EssentialAction = newAction(_ => empty, redirectUrl)

  def newAction(empty: Long => T, redirectUrl: String): EssentialAction =
    Application.IsAuthenticated {
      userId => implicit req =>
        form(empty(userId)).bindFromRequest().fold(
          err => BadRequest(createHtml(err)),
          ans => {
            table.save(ans)
            Redirect(redirectUrl)
          })
    }
}
