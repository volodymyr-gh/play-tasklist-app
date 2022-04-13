package dev.volodymyr.user.controllers

import dev.volodymyr.user.dto.LoginDto
import dev.volodymyr.user.exceptions.NotAuthenticatedException
import dev.volodymyr.user.services.UserService

import play.api.libs.json.JsValue
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class UserController @Inject() (val controllerComponents: ControllerComponents,
                                userService: UserService)
                               (implicit ec: ExecutionContext)
  extends BaseController {

  def login(): Action[JsValue] = Action.async(parse.json) { implicit req: Request[JsValue] =>
    def onValidationSuccess(loginDto: LoginDto): Future[Result] =
      userService.login(loginDto)
        .map { token => Ok(token) }
        .recover {
          case _: NotAuthenticatedException => Unauthorized
          case _ => InternalServerError
        }

    req.body.validate[LoginDto].fold(
      _ => Future.successful(BadRequest),
      onValidationSuccess
    )
  }
}
