package dev.volodymyr.task.dto

import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class CreateTaskDto(summary: String)

object CreateTaskDto {
  implicit val reads: Reads[CreateTaskDto] =
    (JsPath \ "summary").read[String](
      minLength[String](1)
        .keepAnd(maxLength[String](100))
    ).map(CreateTaskDto(_))
}
