package dev.volodymyr.task.repositories

import dev.volodymyr.task.entities.{Completed, InProgress, Task, TaskStatus}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlickTaskRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                    (implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with TaskRepository {

  import profile.api._

  private class TasksTable(tag: Tag) extends Table[Task](tag, "TASK") {
    def uuid: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)
    def summary: Rep[String] = column[String]("SUMMARY")
    def status: Rep[TaskStatus] = column[TaskStatus]("STATUS")
    def createdBy: Rep[UUID] = column[UUID]("CREATED_BY")

    def * : ProvenShape[Task] = (uuid, summary, status, createdBy) <> ((Task.apply _).tupled, Task.unapply)

    implicit val statusColumnType: BaseColumnType[TaskStatus] =
      // I wonder if there is a better way to do this
      MappedColumnType.base[TaskStatus, String](
        {
          case InProgress => "InProgress"
          case Completed => "Completed"
        },
        {
          case "InProgress" => InProgress
          case "Completed" => Completed
        }
      )
  }

  private val Tasks = TableQuery[TasksTable]

  def findByUserId(userId: UUID): Future[Seq[Task]] = db.run {
    Tasks
      .filter(_.createdBy === userId)
      .result
  }

  def findByIdAndUserId(uuid: UUID, userId: UUID): Future[Option[Task]] = db.run {
    Tasks
      .filter(_.uuid === uuid)
      .filter(_.createdBy === userId)
      .result
      .headOption
  }

  def insert(task: Task): Future[Task] = db.run {
    (Tasks returning Tasks) += task
  }

  def update(task: Task, updatedTask: Task): Future[Task] = db.run {
    Tasks
      .filter(_.uuid === task.uuid)
      .update(updatedTask)
      .map(_ => updatedTask)
  }
}
