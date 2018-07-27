package models

import java.time.LocalDateTime

import play.api.libs.json.{ Json, JsonValidationError, OFormat }

case class Interest(id: Option[Long], value: String, user: Option[String], creationDate: Option[LocalDateTime])

object Interest {

  private val reads = Json.reads[Interest]
    .filter(JsonValidationError("Value should start with # or @")) { i =>
      i.value.startsWith("@") || i.value.startsWith("#")
    }

  implicit val format: OFormat[Interest] = OFormat(
    reads,
    Json.writes[Interest])

}
