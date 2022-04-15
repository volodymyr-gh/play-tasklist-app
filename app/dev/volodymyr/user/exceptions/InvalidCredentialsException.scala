package dev.volodymyr.user.exceptions

class InvalidCredentialsException(msg: String) extends Exception(msg: String) {
  def this() = {
    this("Invalid user credentials")
  }
}
