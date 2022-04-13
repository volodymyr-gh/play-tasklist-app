package dev.volodymyr.user.repositories

import dev.volodymyr.user.entities.User

import scala.concurrent.Future

trait UserRepository {
  def findByEmail(email: String): Future[Option[User]]
}
