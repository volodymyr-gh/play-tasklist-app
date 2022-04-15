package dev.volodymyr.task.services

import dev.volodymyr.task.dto.CreateTaskDto
import dev.volodymyr.task.entities.{InProgress, Task}
import dev.volodymyr.task.repositories.TaskRepository

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class TaskServiceImpl @Inject() (taskRepository: TaskRepository)
                                (implicit ec: ExecutionContext)
  extends TaskService {

  def getByUserId(userId: UUID): Future[Seq[Task]] =
    taskRepository.findByUserId(userId)

  def create(createTaskDto: CreateTaskDto, createdBy: UUID): Future[Task] =
    taskRepository.insert(Task(
      summary = createTaskDto.summary,
      status = InProgress,
      createdBy = createdBy
    ))

  def complete(id: UUID, userId: UUID): Future[Task] = {
    def onTaskFound(task: Task): Future[Task] =
      task.complete() match {
        case Success(completedTask) =>
          taskRepository.update(task, completedTask)
        case Failure(ex) => Future.failed(ex)
      }

    taskRepository.findByIdAndUserId(id, userId).flatMap {
      case Some(task) => onTaskFound(task)
      case None => Future.failed(new Exception("Task not found"))
    }
  }
}
