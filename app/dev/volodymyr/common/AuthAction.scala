package dev.volodymyr.common

import dev.volodymyr.user.dto.CurrentUser
import dev.volodymyr.user.services.UserService

import play.api.http.HeaderNames
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class CurrentUserRequest[A](currentUser: CurrentUser, request: Request[A])
  extends WrappedRequest[A](request)

class AuthAction @Inject() (bodyParser: BodyParsers.Default,
                            userService: UserService)
                           (implicit ec: ExecutionContext)
  extends ActionBuilder[CurrentUserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  private val tokenHeaderRegex = """Bearer (.+?)""".r

  override def invokeBlock[A](request: Request[A],
                              block: CurrentUserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request)
      .map { token =>
        userService.extractUserFromToken(token) match {
          case Success(user) => block(CurrentUserRequest(user, request))
          case Failure(_) => Future.successful(Results.Unauthorized)
        }
      }
      .getOrElse(Future.successful(Results.Unauthorized))

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers
      .get(HeaderNames.AUTHORIZATION)
      .collect {
        case tokenHeaderRegex(token) => token
      }
}
