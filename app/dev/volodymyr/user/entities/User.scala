package dev.volodymyr.user.entities

import com.github.t3hnar.bcrypt._

import java.util.UUID

case class User(uuid: UUID, email: String, password: String) {
  def isPasswordEqualTo(other: String): Boolean = {
    other.isBcryptedBounded(password)
  }
}

object User {
  def apply(email: String, originalPassword: String): User = {
    apply(
      uuid = UUID.randomUUID(),
      email = email,
      password = hashPassword(originalPassword)
    )
  }

  private def hashPassword(password: String): String =
    password.bcryptBounded(10)
}
