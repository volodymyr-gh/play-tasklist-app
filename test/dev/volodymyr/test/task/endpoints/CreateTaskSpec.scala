package dev.volodymyr.test.task.endpoints

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws.WSClient

class CreateTaskSpec extends PlaySpec with GuiceOneServerPerSuite {
  private def wsClient: WSClient = app.injector.instanceOf[WSClient]

  private def url: String = s"http://localhost:$port/tasks"

  "POST request to /tasks endpoint" should {
    "return status 201 and newly created task in the body" in {
      val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NTI1ODA3NDEsInV1aWQiOiJkNTBlMTQyNS02OTM0LTQ0YzYtOWRmNi00OWJiODFkYTFiNDkiLCJlbWFpbCI6ImpvaG5AZXhhbXBsZS5jb20ifQ.9vmnbPKfINWQbC6Ow7UVV3ncwQ0M34yQMm_hWjfKaRQ"

      val req = wsClient.url(url)
        .withHttpHeaders(("Authorization", s"Bearer $token"))
        .post(Json.obj("summary" -> "do something"))

      val res = await(req)
      res.status mustBe CREATED
    }
  }
}
