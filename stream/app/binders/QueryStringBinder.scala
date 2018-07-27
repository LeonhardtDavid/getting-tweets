package binders

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import play.api.mvc.QueryStringBindable

import scala.util.{ Failure, Success, Try }

object QueryStringBinder {

  implicit def queryStringLocalDateBindables(
      implicit
      binder: QueryStringBindable[String]): QueryStringBindable[LocalDateTime] = {
    new QueryStringBindable[LocalDateTime] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LocalDateTime]] = {
        for {
          param <- binder.bind(key, params)
        } yield {
          param match {
            case Right(status) =>
              Try(LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(status))) match {
                case Success(date) => Right(date)
                case Failure(_)    => Left("Invalid LocalDate format")
              }
            case _ =>
              Left("Unable to bind a LocalDate")
          }
        }
      }

      override def unbind(key: String, value: LocalDateTime): String =
        binder.unbind(key, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value))

    }
  }

}
