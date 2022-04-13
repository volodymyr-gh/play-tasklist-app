package dev.volodymyr.user.dto

import play.api.libs.json.{Json, OFormat}

case class LoginDto(email: String, password: String)

object LoginDto {
  implicit val format: OFormat[LoginDto] = Json.format[LoginDto]
}
