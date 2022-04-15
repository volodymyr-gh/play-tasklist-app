package dev.volodymyr.testUtils

import akka.util.Timeout
import com.github.t3hnar.bcrypt._
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.test.DefaultAwaitTimeout
import slick.jdbc.JdbcProfile

import java.util.UUID
import scala.concurrent.{Await, ExecutionContext}

trait TestDBProvider
  extends HasDatabaseConfigProvider[JdbcProfile]
    with BeforeAndAfterEach
    with DefaultAwaitTimeout {

  this: Suite with GuiceOneServerPerSuite =>

  import profile.api._

  override protected lazy val dbConfigProvider: DatabaseConfigProvider =
    app.injector.instanceOf[DatabaseConfigProvider]

  private lazy val dbApi =
    app.injector.instanceOf[DBApi].database("default")

  implicit lazy val ec: ExecutionContext =
    app.injector.instanceOf[ExecutionContext]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    Evolutions.applyEvolutions(dbApi)
  }

  override protected def afterEach(): Unit = {
    Evolutions.cleanupEvolutions(dbApi)
    super.afterEach()
  }

  protected def createFixtureUser(uuid: UUID = UUID.randomUUID(),
                                  email: String = "default_user@example.com",
                                  password: String = "default_password"): FixtureUser = {

    val hashedPassword = password.bcryptBounded(10)
    val action = sqlu"""INSERT INTO public."USER" ("UUID", "EMAIL", "PASSWORD")
                        VALUES (${uuid.toString}::uuid, $email, $hashedPassword)"""
    runDBAction(action)
    FixtureUser(uuid, email, password, hashedPassword)
  }

  private def runDBAction[T](action: DBIO[T])(implicit timeout: Timeout): T =
    Await.result(db.run(action), timeout.duration)
}
