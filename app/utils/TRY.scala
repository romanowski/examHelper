package utils

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 22.03.13
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
object TRY {

  def apply[T](f: => T): Option[T] = try {
    Option(f)
  } catch {
    case _: Throwable => None
  }

}
