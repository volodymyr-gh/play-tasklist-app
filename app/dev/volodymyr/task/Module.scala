package dev.volodymyr.task

import com.google.inject.AbstractModule
import dev.volodymyr.task.repositories.{InMemoryTaskRepository, SlickTaskRepository, TaskRepository}
import dev.volodymyr.task.services.{TaskService, TaskServiceImpl}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[TaskService]).to(classOf[TaskServiceImpl])
//    bind(classOf[TaskRepository]).to(classOf[InMemoryTaskRepository])
    bind(classOf[TaskRepository]).to(classOf[SlickTaskRepository])
  }
}
