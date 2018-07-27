package controllers

import java.time.LocalDateTime

import javax.inject.{ Inject, Singleton }
import models.Interest
import persistance.InterestRepository
import play.api.libs.json._
import play.api.mvc.{ BaseController, ControllerComponents }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class Interests @Inject() (
    val controllerComponents: ControllerComponents,
    repository: InterestRepository)(implicit ec: ExecutionContext) extends BaseController {

  // scalastyle:off public.methods.have.type

  private val dummyUser = "dummy"

  def list(from: Long, limit: Int, after: Option[LocalDateTime]) = Action.async {
    this.repository.list(from, limit, after, dummyUser) map { items =>
      Ok(Json.obj("items" -> items))
    }
  }

  def find(id: Long) = Action.async {
    this.repository.findById(id) map {
      case Some(item) => Ok(Json.toJson(item))
      case None       => NotFound
    }
  }

  def save = Action.async(parse.json) { req =>
    req.body.validate[Interest].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      item => this.repository.save(item.copy(user = Some(dummyUser), creationDate = Some(LocalDateTime.now()))) map {
        case Interest(Some(id), _, _, _) => Created.withHeaders("Location" -> routes.Interests.find(id).url)
        case _                           => InternalServerError(Json.obj("error" -> "Something went wrong..."))
      })
  }

  def delete(id: Long) = Action.async {
    this.repository.delete(id) map { _ =>
      Ok(Json.obj("result" -> "deleted"))
    }
  }

}
