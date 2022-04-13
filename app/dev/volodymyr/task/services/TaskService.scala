package dev.volodymyr.task.services

import dev.volodymyr.task.dto.CreateTaskDto
import dev.volodymyr.task.entities.Task

import java.util.UUID
import scala.concurrent.Future

trait TaskService {
  def getByUserId(userId: UUID): Future[Seq[Task]]
  def create(createTaskDto: CreateTaskDto, createdBy: UUID): Future[Task]
  def complete(id: UUID, userId: UUID): Future[Task]
}
