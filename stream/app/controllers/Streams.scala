package controllers

import javax.inject.{ Inject, Singleton }
import models.Request
import persistance.TweetRepository
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{ BaseController, ControllerComponents }
import services.TwitterService

import scala.collection.mutable
import scala.concurrent.ExecutionContext

@Singleton
class Streams @Inject() (
    val controllerComponents: ControllerComponents,
    tweetRepository: TweetRepository, twitterService: TwitterService)(
    implicit
    ec: ExecutionContext) extends BaseController {

  // scalastyle:off public.methods.have.type

  private val interests = mutable.ListBuffer.empty[String]

  def create = Action(parse.json) { req =>
    req.body.validate[Request].fold(
      errors => BadRequest(JsError.toJson(errors)),
      request => {
        this.interests.clear()
        this.interests.appendAll(request.interests)

        Logger.debug(s"created interests ${this.interests}")

        Accepted
      })
  }

  def add = Action(parse.json) { req =>
    req.body.validate[Request].fold(
      errors => BadRequest(JsError.toJson(errors)),
      request => {
        this.interests.appendAll(request.interests)

        Logger.debug(s"updated interests after add ${this.interests}")

        Accepted
      })
  }

  def delete = Action(parse.json) { req =>
    req.body.validate[Request].fold(
      errors => BadRequest(JsError.toJson(errors)),
      request => {
        val filteredInterests = this.interests.filterNot(request.interests.contains)
        this.interests.clear()
        this.interests.appendAll(filteredInterests)

        Logger.debug(s"updated interests after delete ${this.interests}")

        Accepted
      })
  }

  def run = Action {
    this.twitterService.findAndSave(this.interests.toList)
    Accepted
  }

  def find(interests: List[String]) = Action {
    Logger.debug(s"Interests to find $interests")
    Ok.chunked(this.tweetRepository.find(interests).map(Json.toJson(_)))
  }

}
