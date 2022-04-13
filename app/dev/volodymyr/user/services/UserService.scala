package dev.volodymyr.user.services

import dev.volodymyr.user.dto.{CurrentUser, LoginDto}

import scala.concurrent.Future
import scala.util.Try

trait UserService {
  def login(loginDto: LoginDto): Future[String]
  def extractUserFromToken(token: String): Try[CurrentUser]
}
