package dev.volodymyr.user.exceptions

class NotAuthenticatedException(msg: String) extends Exception(msg: String) {
  def this() = {
    this("Not authenticated")
  }
}
