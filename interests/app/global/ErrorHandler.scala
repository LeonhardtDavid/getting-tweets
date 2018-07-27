package global

import javax.inject.{ Inject, Provider, Singleton }
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }
import play.api.routing.Router
import play.api.{ Configuration, Environment, Logger, OptionalSourceMapper }

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject() (
    env: Environment,
    config: Configuration, sourceMapper: OptionalSourceMapper, router: Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful {
      Logger.error(s"Client error with code $statusCode, message $message for  ${request.method} ${request.uri}")
      BadRequest(Json.obj("error" -> message))
    }

  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] =
    Future.successful {
      Logger.error("Internal server error", ex)
      InternalServerError
    }

  override def onNotFound(request: RequestHeader, message: String): Future[Result] =
    Future.successful {
      Logger.warn(s"Not found ${request.method} ${request.uri}")
      NotFound
    }

  override def onBadRequest(request: RequestHeader, message: String): Future[Result] =
    Future.successful {
      Logger.warn(s"BadRequest ${request.method} ${request.uri}")
      BadRequest
    }

}
