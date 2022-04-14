package dev.volodymyr.task.entities

import dev.volodymyr.task.exceptions.InvalidTaskStateException
import play.api.libs.json.{Json, OWrites}

import java.util.UUID
import scala.util.{Failure, Success, Try}

sealed trait TaskStatus
case object InProgress extends TaskStatus
case object Completed extends TaskStatus

case class Task(uuid: UUID = UUID.randomUUID(),
                summary: String,
                status: TaskStatus,
                createdBy: UUID) {

  def complete(): Try[Task] = status match {
    case InProgress => Success(copy(status = Completed))
    case _ => Failure(new InvalidTaskStateException)
  }
}

object Task {
  implicit val writes: OWrites[Task] = (task: Task) => Json.obj(
    "uuid" -> task.uuid.toString,
    "summary" -> task.summary,
    "status" -> task.status.toString,
    "createdBy" -> task.createdBy.toString
  )
}
