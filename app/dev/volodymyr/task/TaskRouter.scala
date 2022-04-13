package dev.volodymyr.task

import dev.volodymyr.task.controllers.TaskController

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import java.util.UUID
import javax.inject.Inject

class TaskRouter @Inject() (taskController: TaskController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/") => taskController.list()
    case POST(p"/") => taskController.create()
    case POST(p"/$id/complete") => taskController.complete(UUID.fromString(id))
  }
}

