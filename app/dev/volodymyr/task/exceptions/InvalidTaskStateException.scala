package dev.volodymyr.task.exceptions

class InvalidTaskStateException(msg: String) extends Exception(msg) {
  def this() = {
    this("Invalid task state")
  }
}
