package io.github.leonhardtdavid.gettingtweetsagent.services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import io.github.leonhardtdavid.gettingtweetsagent.conf.ApplicationConf
import io.github.leonhardtdavid.gettingtweetsagent.models.Interest
import org.slf4j.LoggerFactory
import play.api.libs.json.JsValue
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.JsonBodyReadables._

import scala.concurrent.{ ExecutionContext, Future }

class InterestService(ws: StandaloneAhcWSClient, config: ApplicationConf)(implicit ec: ExecutionContext) {

  private val logger = LoggerFactory.getLogger("application")

  def findInterestsToAdd: Future[List[Interest]] = {
    val after = LocalDateTime.now().minusSeconds(this.config.services.interests.gap.getSeconds)
    val afterString = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(after)

    logger.debug(s"find interests added after $afterString")

    this.ws.url(this.config.services.interests.url)
      .addQueryStringParameters("after" -> afterString)
      .get()
      .map(res => (res.body[JsValue] \ "items").as[List[Interest]])
  }

}
