package persistance

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import javax.inject.Inject
import models.Tweet
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.akkastream.cursorProducer
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }

class TweetRepository @Inject() (reactiveMongoApi: ReactiveMongoApi)(
    implicit
    ec: ExecutionContext,
    materializer: Materializer) {

  private val futureCollection: Future[JSONCollection] =
    this.reactiveMongoApi.database.map(_.collection[JSONCollection]("tweets"))

  def save(t: Tweet): Future[Tweet] =
    for {
      db <- this.futureCollection
      res <- db.insert(t) if res.ok
    } yield t

  def find(interests: List[String]): Source[Tweet, _] = {
    Source
      .fromFuture(this.futureCollection)
      .flatMapConcat { db =>
        db.find(Json.obj("interest" -> Json.obj("$in" -> interests)))
          .cursor[Tweet]()
          .documentSource()
      }
  }

}
