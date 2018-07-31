package io.github.leonhardtdavid.gettingtweetsagent.services

import io.github.leonhardtdavid.gettingtweetsagent.conf.ApplicationConf
import io.github.leonhardtdavid.gettingtweetsagent.models.{ Interest, Request }
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ ExecutionContext, Future }

class StreamService(ws: StandaloneAhcWSClient, config: ApplicationConf)(implicit ec: ExecutionContext) {

  private val logger = LoggerFactory.getLogger("application")

  def add(interests: List[Interest]): Future[Boolean] = {
    val json = Json.toJson(Request(interests.map(_.value)))

    logger.debug(s"json=$json")

    this.ws.url(this.config.services.stream.addUrl)
      .post(json)
      .map(_.status == 202)
  }

  def run: Future[Boolean] = {
    logger.debug("Running update on stream")

    this.ws.url(this.config.services.stream.runUrl)
      .get()
      .map(_.status == 202)
  }

}
