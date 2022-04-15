package dev.volodymyr.testUtils

import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION

import java.time.Instant

trait AuthenticationProvider {
  this: GuiceOneServerPerSuite =>

  private def config: Configuration = app.injector.instanceOf[Configuration]

  protected def buildAuthHeaders(user: FixtureUser): Seq[(String, String)] = {
    val token = buildAuthToken(user)
    Seq((AUTHORIZATION, s"Bearer $token"))
  }

  private def buildAuthToken(user: FixtureUser): String = {
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
