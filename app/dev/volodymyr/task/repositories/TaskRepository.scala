package dev.volodymyr.task.repositories

import dev.volodymyr.task.entities.Task

import java.util.UUID
import scala.concurrent.Future

trait TaskRepository {
  def findByUserId(userId: UUID): Future[Seq[Task]]
  def findByIdAndUserId(uuid: UUID, userId: UUID): Future[Option[Task]]
  def insert(task: Task): Future[Task]
  def update(task: Task, updatedTask: Task): Future[Task]
}
