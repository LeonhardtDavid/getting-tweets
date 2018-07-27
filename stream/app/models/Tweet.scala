package models

import play.api.libs.json.{ Json, OFormat }
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json._

case class Tweet(_id: BSONObjectID, interest: String, user: TwitterUser, text: String) {
  def this(interest: String, user: TwitterUser, text: String) = this(BSONObjectID.generate(), interest, user, text)
}

case class TwitterUser(name: String, profileImage: String)

object Tweet {
  implicit val userFormat: OFormat[TwitterUser] = Json.format[TwitterUser]
  implicit val format: OFormat[Tweet] = Json.format[Tweet]
}
