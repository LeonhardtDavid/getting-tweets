package persistance

import java.sql.Timestamp
import java.time.LocalDateTime

import javax.inject.Inject
import models.Interest
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

class InterestRepository @Inject() (val dbConfigProvider: DatabaseConfigProvider)(
    implicit
    ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val table = TableQuery[Interests]

  private def findByIdQuery(id: Long): Query[Interests, Interest, Seq] = this.table.filter(_.id === id)

  def list(from: Long, limit: Int, after: Option[LocalDateTime], user: String): Future[List[Interest]] =
    db.run {
      val query = this.table.filter(_.user === user)
      (limit, after) match {
        case (_, Some(date)) => query.filter(_.creationDate > date).result
        case (l, _) if l > 0 => query.drop(from).take(limit).result
        case _               => query.result
      }
    } map (_.toList)

  def findById(id: Long): Future[Option[Interest]] =
    db.run(this.findByIdQuery(id).result.headOption)

  def save(e: Interest): Future[Interest] = db.run {
    this.table.returning(this.table.map(_.id)).into((item, id) => item.copy(id = Some(id))) += e
  }

  def delete(id: Long): Future[Unit] = db.run(this.findByIdQuery(id).delete).map(_ => ())

  implicit def date2dateTime: BaseColumnType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, Timestamp](
      dateTime => Timestamp.valueOf(dateTime),
      date => date.toLocalDateTime)

  // $COVERAGE-OFF$
  class Interests(tag: Tag) extends Table[Interest](tag, None, "Interest") {

    // scalastyle:off public.methods.have.type

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def value = column[String]("value")
    def user = column[String]("user")
    def creationDate = column[LocalDateTime]("creationDate")

    def * = (id.?, value, user.?, creationDate.?) <> ((Interest.apply _).tupled, Interest.unapply) // scalastyle:ignore

    // scalastyle:off public.methods.have.type

  }
  // $COVERAGE-ON$

}
