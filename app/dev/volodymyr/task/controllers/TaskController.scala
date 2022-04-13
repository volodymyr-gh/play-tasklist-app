package dev.volodymyr.task.controllers

import dev.volodymyr.common.{AuthAction, CurrentUserRequest}
import dev.volodymyr.task.dto.CreateTaskDto
import dev.volodymyr.task.services.TaskService

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaskController @Inject() (val controllerComponents: ControllerComponents,
                                authAction: AuthAction,
                                taskService: TaskService)
                               (implicit ec: ExecutionContext)
  extends BaseController {

  def list(): Action[AnyContent] = authAction.async { implicit req: CurrentUserRequest[AnyContent] =>
    taskService.getByUserId(req.currentUser.uuid)
      .map(tasks => Ok(Json.toJson(tasks)))
  }

  def create(): Action[JsValue] = authAction.async(parse.json) { implicit req: CurrentUserRequest[JsValue] =>
    def onValidationSuccess(taskDto: CreateTaskDto): Future[Result] =
      taskService.create(taskDto, req.currentUser.uuid)
        .map(task => Created(Json.toJson(task)))

    req.body.validate[CreateTaskDto].fold(
      _ => Future.successful(BadRequest),
      onValidationSuccess
    )
  }

  def complete(id: UUID): Action[AnyContent] = authAction.async { implicit req: CurrentUserRequest[AnyContent] =>
    taskService.complete(id, req.currentUser.uuid)
      .map(task => Ok(Json.toJson(task)))
  }
}
