package controllers

import controllers.Application._
import play.api.mvc.BodyParsers.parse
import db.{SchoolEntity, OrderedSchoolEntity, OrderedSchoolEntityTable}

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 07.04.13
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
trait OrderedEntityController[T <: SchoolEntity[T] with OrderedSchoolEntity[T]] {

  def items(id: Long): Seq[T]

  val meta: OrderedSchoolEntityTable[T]

  def orderChange(id: String) = IsAuthenticated(parse.json) {
    tId => implicit request =>
      val order = (request.body \ "order").as[List[String]].map(_.toLong)
      val items = this.items(id.toLong).map(v => v.id_! -> v).toMap
      order.zipWithIndex.foreach {
        case (id, pos) =>
          items.get(id).foreach {
            q => meta.save(q.withOrder(pos))
          }
      }
      Ok
  }

}
