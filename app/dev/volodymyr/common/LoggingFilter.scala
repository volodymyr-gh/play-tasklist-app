package dev.volodymyr.common

import akka.stream.Materializer
import play.api.Logging
import play.api.mvc.{Filter, RequestHeader, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext)
  extends Filter with Logging  {

  def apply(next: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val start = System.currentTimeMillis()

    next(requestHeader).map { result =>
      val requestTime = System.currentTimeMillis() - start

      logger.info(
        s"${requestHeader.method} ${requestHeader.uri} took $requestTime ms and returned ${result.header.status}"
      )

      result.withHeaders("Request-Time" -> s"${requestTime.toString}ms")
    }
  }
}
