package dev.volodymyr.user.services

import dev.volodymyr.user.dto.{CurrentUser, LoginDto}
import dev.volodymyr.user.entities.User
import dev.volodymyr.user.exceptions.NotAuthenticatedException
import dev.volodymyr.user.repositories.UserRepository

import io.circe.parser.decode
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import play.api.Configuration
import play.api.libs.json.Json

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class UserServiceImpl @Inject() (userRepository: UserRepository,
                                 config: Configuration)
                                (implicit ec: ExecutionContext)
  extends UserService {

  def extractUserFromToken(token: String): Try[CurrentUser] = {
    def onDecoded(payload: io.circe.Json): Try[CurrentUser] =
      decode[CurrentUser](payload.toString) match {
        case Right(user) => Success(user)
        case Left(ex) => Failure(ex)
      }

    val secret = config.get[String]("app.auth.token.secret")
    val decoded = JwtCirce.decodeJson(token, secret, Seq(JwtAlgorithm.HS256))

    decoded match {
      case Success(value) => onDecoded(value)
      case Failure(ex) => Failure(ex)
    }
  }

  def login(loginDto: LoginDto): Future[String] =
    userRepository.findByEmail(loginDto.email).flatMap {
      case None => Future.failed(new NotAuthenticatedException)
      case Some(user) =>
        if (user.isPasswordEqualTo(loginDto.password))
          Future.successful(createToken(user))
        else
          Future.failed(new NotAuthenticatedException)
    }

  private def createToken(user: User): String = {
    val content = Json.stringify(Json.obj(
      "uuid" -> user.uuid.toString,
      "email" -> user.email
    ))

    val ttl = config.get[Int]("app.auth.token.TTLSeconds")

    val expiration = Instant.now
      .plusSeconds(ttl)
      .getEpochSecond

    val secret = config.get[String]("app.auth.token.secret")

    JwtCirce.encode(
      claim = JwtClaim(
        content = content,
        expiration = Some(expiration)
      ),
      key = secret,
      algorithm = JwtAlgorithm.HS256
    )
  }
}
