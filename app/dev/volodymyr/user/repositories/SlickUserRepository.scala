package dev.volodymyr.user.repositories

import dev.volodymyr.user.entities.User

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlickUserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                    (implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with UserRepository {

  import profile.api._

  private type UserRow = (UUID, String, String)

  private class UsersTable(tag: Tag) extends Table[UserRow](tag, "USER") {
    def uuid: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)
    def email: Rep[String] = column[String]("EMAIL", O.Unique)
    def password: Rep[String] = column[String]("PASSWORD")

    def * : ProvenShape[UserRow] = (uuid, email, password)
  }

  private val Users = TableQuery[UsersTable]

  def findByEmail(email: String): Future[Option[User]] = db.run {
    Users.filter(_.email === email)
      .result
      .headOption
      .map {
        case Some((uuid, email, password)) => Some(User(uuid, email, password))
        case _ => None
      }
  }
}
