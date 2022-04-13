package dev.volodymyr.user.dto

import io.circe.Decoder

import java.util.UUID

case class CurrentUser(uuid: UUID, email: String)

object CurrentUser {
  implicit val decoder: Decoder[CurrentUser] =
    Decoder.forProduct2("uuid", "email")(CurrentUser.apply)
}
