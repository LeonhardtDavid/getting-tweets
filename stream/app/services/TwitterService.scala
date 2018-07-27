package services

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.http.clients.streaming.TwitterStream
import javax.inject.Inject
import models.TwitterUser
import persistance.TweetRepository
import play.api.Logger

import scala.concurrent.{ ExecutionContext, Future }

class TwitterService @Inject() (tweetRepository: TweetRepository)(implicit ec: ExecutionContext) {

  private val client = TwitterStreamingClient()

  private var x: Future[TwitterStream] = Future.failed(new RuntimeException)

  private def createStream(interests: List[String]) = {
    val (hashTags, users) = interests.partition(_.startsWith("#"))

    this.client.filterStatuses(tracks = interests) {
      case tweet: Tweet =>
        val text = tweet.text.toLowerCase
        val (userName, image) =
          tweet.user.map(u => (u.screen_name, u.profile_image_url_https.default)).getOrElse(("unknown", ""))
        val user = TwitterUser(userName, image)
        val interest = hashTags
          .find(ht => text.contains(ht.toLowerCase))
          .orElse(users.find(_ == s"@$userName"))
          .getOrElse("AAAA")

        Logger.debug("user " + tweet.user.map(u => u.name + " / " + u.screen_name))

        this.tweetRepository.save(new models.Tweet(interest, user, tweet.text))
    }
  }

  def findAndSave(interests: List[String]): Unit = {
    this.x = for {
      _ <- this.x.map(_.close()).recover { case _ => }
      twitterStream <- this.createStream(interests)
    } yield {
      twitterStream
    }
  }

}
