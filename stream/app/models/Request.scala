package models

import play.api.libs.json.{ Json, OFormat }

case class Request(interests: List[String])

object Request {
  implicit val format: OFormat[Request] = Json.format[Request]
}
