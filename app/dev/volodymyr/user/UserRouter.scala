package dev.volodymyr.user

import dev.volodymyr.user.controllers.UserController

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import javax.inject.Inject

class UserRouter @Inject() (userController: UserController) extends SimpleRouter {
  override def routes: Routes = {
    case POST(p"/login") => userController.login()
  }
}
