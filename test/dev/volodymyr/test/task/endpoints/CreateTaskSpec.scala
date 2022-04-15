package dev.volodymyr.test.task.endpoints

import dev.volodymyr.testUtils.{AuthenticationProvider, TestDBProvider}

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.test.Helpers._
import play.api.libs.ws.WSClient
import play.api.libs.functional.syntax._

import java.util.UUID

class CreateTaskSpec
  extends PlaySpec
    with GuiceOneServerPerSuite
    with TestDBProvider
    with AuthenticationProvider {

  private lazy val wsClient = app.injector.instanceOf[WSClient]

  private lazy val url = s"http://localhost:$port/tasks"

  private case class CreateTaskResponse(uuid: UUID, summary: String)
  private object CreateTaskResponse {
    implicit val reads: Reads[CreateTaskResponse] =
      ((JsPath \ "uuid").read[UUID] and
      (JsPath \ "summary").read[String])(CreateTaskResponse.apply _)
  }

  "POST request to /tasks endpoint" should {
    "return status 201 and newly created task in the body" in {
      val currentUser = createFixtureUser()
      val taskSummary = "do something"

      val req = wsClient.url(url)
        .withHttpHeaders(buildAuthHeaders(currentUser): _*)
        .post(Json.obj("summary" -> taskSummary))

      val res = await(req)
      res.status mustBe CREATED

      // I wonder if this is how JSON is supposed to be tested
      val parseResult = Json.parse(res.body).validate[CreateTaskResponse]
      parseResult.isSuccess mustBe true
      parseResult.get.summary mustBe taskSummary
    }

    "return status 401 if the request is not authenticated" in {
      val req = wsClient.url(url)
        .post(Json.obj("summary" -> "do something"))

      val res = await(req)
      res.status mustBe UNAUTHORIZED
    }

    "return status 400 if the request body is missing summary" in {
      val currentUser = createFixtureUser()

      val req = wsClient.url(url)
        .withHttpHeaders(buildAuthHeaders(currentUser): _*)
        .post(Json.obj())

      val res = await(req)
      res.status mustBe BAD_REQUEST
    }
  }
}
