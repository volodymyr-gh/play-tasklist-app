package dev.volodymyr.test.task.endpoints

import dev.volodymyr.testUtils.{AuthenticationProvider, TestDBProvider}
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.libs.ws.WSClient
import scala.concurrent.Await
import scala.concurrent.duration.Duration.Inf

class CreateTaskSpec
  extends PlaySpec
    with GuiceOneServerPerSuite
    with TestDBProvider
    with AuthenticationProvider {

  private def wsClient = app.injector.instanceOf[WSClient]

  private def url = s"http://localhost:$port/tasks"

  "POST request to /tasks endpoint" should {
    "return status 201 and newly created task in the body" in {
      val currentUser = Await.result(createFixtureUser(), Inf)

      val req = wsClient.url(url)
        .withHttpHeaders(buildAuthHeaders(currentUser): _*)
        .post(Json.obj("summary" -> "do something"))

      val res = await(req)
      res.status mustBe CREATED
      // TODO check body
    }

    "return status 401 if the request is not authenticated" in {
      val req = wsClient.url(url)
        .post(Json.obj("summary" -> "do something"))

      val res = await(req)
      res.status mustBe UNAUTHORIZED
    }

    "return status 400 if the request body is missing summary" in {
      val currentUser = Await.result(createFixtureUser(), Inf)

      val req = wsClient.url(url)
        .withHttpHeaders(buildAuthHeaders(currentUser): _*)
        .post(Json.obj())

      val res = await(req)
      res.status mustBe BAD_REQUEST
    }
  }
}
