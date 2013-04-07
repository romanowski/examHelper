package utils

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 07.04.13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
object Randoms {


  def string(len: Int) = System.nanoTime().toString.take(len)
}
