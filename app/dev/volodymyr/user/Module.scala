package dev.volodymyr.user

import com.google.inject.AbstractModule
import dev.volodymyr.user.repositories.{InMemoryUserRepository, SlickUserRepository, UserRepository}
import dev.volodymyr.user.services.{UserService, UserServiceImpl}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Startup]).asEagerSingleton()
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
//    bind(classOf[UserRepository]).to(classOf[InMemoryUserRepository])
    bind(classOf[UserRepository]).to(classOf[SlickUserRepository])
  }
}
