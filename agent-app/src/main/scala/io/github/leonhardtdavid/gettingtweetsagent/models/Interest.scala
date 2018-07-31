package io.github.leonhardtdavid.gettingtweetsagent.models

import java.time.LocalDateTime

import play.api.libs.json.{ Json, OFormat }

case class Interest(id: Option[Long], value: String, user: Option[String], creationDate: Option[LocalDateTime])

object Interest {
  implicit val format: OFormat[Interest] = Json.format[Interest]
}
