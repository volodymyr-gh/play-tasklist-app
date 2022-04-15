package dev.volodymyr.testUtils

import java.util.UUID

case class FixtureUser(uuid: UUID,
                       email: String,
                       originalPassword: String,
                       hashedPassword: String)
