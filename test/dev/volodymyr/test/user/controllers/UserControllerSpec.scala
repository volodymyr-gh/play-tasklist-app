package dev.volodymyr.test.user.controllers

import dev.volodymyr.user.controllers.UserController
import dev.volodymyr.user.dto.LoginDto
import dev.volodymyr.user.services.UserService
import dev.volodymyr.user.exceptions.InvalidCredentialsException

import org.scalatestplus.play._
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserControllerSpec extends PlaySpec with MockitoSugar {
  "UserController login" should {
    "return a token if the given credentials are valid" in {
      val token = "someToken"
      val userEmail = "somebody@example.com"
      val userPassword = "password"

      val mockUserService = mock[UserService]
      when(mockUserService.login(LoginDto(userEmail, userPassword)))
        .thenReturn(Future.successful(token))

      val controller = new UserController(stubControllerComponents(), mockUserService)

      val req = FakeRequest(POST, "/users/login")
        .withBody(JsObject(Map(
          "email" -> JsString(userEmail),
          "password" -> JsString(userPassword)
        )))

      val res = controller.login().apply(req)

      status(res) mustBe OK
      contentAsString(res) mustBe token
    }

    "return status 400 if the request body is missing password" in {
      val controller = new UserController(stubControllerComponents(), mock[UserService])

      val req = FakeRequest(POST, "/users/login")
        .withBody(JsObject(Map(
          "email" -> JsString("john@example.com")
        )))

      val res = controller.login().apply(req)

      status(res) mustBe BAD_REQUEST
    }

    "return status 401 if the given credentials are invalid" in {
      val userEmail = "somebody@example.com"
      val userPassword = "password"

      val mockUserService = mock[UserService]
      when(mockUserService.login(LoginDto(userEmail, userPassword)))
        .thenReturn(Future.failed(new InvalidCredentialsException))

      val controller = new UserController(stubControllerComponents(), mockUserService)

      val req = FakeRequest(POST, "/users/login")
        .withBody(JsObject(Map(
          "email" -> JsString(userEmail),
          "password" -> JsString(userPassword)
        )))

      val res = controller.login().apply(req)

      status(res) mustBe UNAUTHORIZED
    }
  }
}
