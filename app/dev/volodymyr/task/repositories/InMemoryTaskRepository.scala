package dev.volodymyr.task.repositories

import dev.volodymyr.task.entities.Task

import java.util.UUID
import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class InMemoryTaskRepository extends TaskRepository {
  private var tasks = List[Task]()

  def findByUserId(userId: UUID): Future[Seq[Task]] =
    Future.successful(tasks)

  def findByIdAndUserId(id: UUID, userId: UUID): Future[Option[Task]] =
    Future.successful(
      tasks.find(t => t.uuid.equals(id) && t.createdBy.equals(userId))
    )

  def insert(task: Task): Future[Task] = {
    tasks = task :: tasks
    Future.successful(task)
  }

  def update(task: Task, updatedTask: Task): Future[Task] = {
    tasks = updatedTask :: tasks.filterNot(_ == task)
    Future.successful(updatedTask)
  }
}
