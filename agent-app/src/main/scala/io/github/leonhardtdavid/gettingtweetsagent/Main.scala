package io.github.leonhardtdavid.gettingtweetsagent

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.github.leonhardtdavid.gettingtweetsagent.conf.ApplicationConf
import io.github.leonhardtdavid.gettingtweetsagent.services.{ InterestService, StreamService }
import org.slf4j.LoggerFactory
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext }

object Main {

  private val logger = LoggerFactory.getLogger("application")

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val ec: ExecutionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val config = ConfigFactory.load()
    val conf = new ApplicationConf(config)
    val ws = StandaloneAhcWSClient()
    val interestService = new InterestService(ws, conf)
    val streamService = new StreamService(ws, conf)

    val future = for {
      interests <- interestService.findInterestsToAdd
      added <- streamService.add(interests) if added
      ok <- streamService.run
    } yield ok

    future onComplete { maybeResult =>
      logger.info(s"Finished for: $maybeResult")
      ws.close()
      system.terminate()
      Await.result(system.whenTerminated, Duration.Inf)
    }
  }

}
