package dev.volodymyr.user.repositories

import dev.volodymyr.user.entities.User

import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class InMemoryUserRepository extends UserRepository {
  private val users = List[User](
    User(
      email = "john@example.com",
      originalPassword = "qwerty123"
    ),
    User(
      email = "jane@example.com",
      originalPassword = "qwerty123"
    )
  )

  def findByEmail(email: String): Future[Option[User]] = {
    Future.successful(users.find(_.email == email))
  }
}
