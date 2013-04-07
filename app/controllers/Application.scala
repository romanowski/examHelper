package controllers

import _root_.db.Teacher
import play.api.mvc._
import views.html
import play.api.data._
import play.api.data.Forms._


object Application extends Controller {

  // ######### Auth section
  private def username(request: RequestHeader) = {
    println(request.session.data)
    request.session.get("userId").map(_.toLong)
  }

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = {
    println(request)
    Results.Redirect(routes.Application.login)
  }

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => Long => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) {
    teacherId =>
      Action(request => f(teacherId)(request))
  }

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated[T](bodyParser: BodyParser[T])(f: => Long => Request[T] => Result) = Security.Authenticated(username, onUnauthorized) {
    teacherId =>
      Action(bodyParser)(request => f(teacherId)(request))
  }


  // ######### index
  def index = IsAuthenticated {
    id => request =>
      Teacher.byId(id).map(t => Ok(views.html.index(t))).getOrElse(logoutRedirect)
  }

  // ######## login
  val passwordField = "password"
  val BadCredentialError = FormError(passwordField, "Nie poprawny email lub hasło", Nil)

  val loginForm = Form(
    mapping(
      "email" -> email,
      passwordField -> text
    )(Teacher.authForForm) {
      case (email, _) =>
        println(email)
        Some(email -> "")
    }
  )

  /**
   * Handle login form submission.
   */
  def login = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(html.login(formWithErrors))
        },
        user => {
          user._2.map(id => Redirect(routes.Application.index).withSession("userId" -> id.toString))
            .getOrElse(BadRequest(html.login(loginForm.fill(user).withGlobalError("Nie poprawny email lub hasło", Nil))))
        })
  }

  def logout = Action {
    Redirect(routes.Application.login).withNewSession
  }

  def logoutRedirect = Redirect(routes.Application.login).withNewSession


  // ######## newExam


}