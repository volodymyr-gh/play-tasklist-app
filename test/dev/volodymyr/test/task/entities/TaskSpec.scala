package dev.volodymyr.test.task.entities

import dev.volodymyr.task.entities.{Completed, InProgress, Task}
import dev.volodymyr.task.exceptions.InvalidTaskStateException
import org.scalatestplus.play._
import org.scalatest.TryValues._

import java.util.UUID

class TaskSpec extends PlaySpec {
  "Task uuid" should {
    "be populated automatically upon creation if not provided" in {
      val task = Task(
        summary = "do something",
        status = InProgress,
        createdBy = UUID.randomUUID()
      )

      task.uuid mustBe a [UUID]
    }
  }

  "Task completion" should {
    "return new task with updated status" in {
      val task = Task(
        summary = "do something",
        status = InProgress,
        createdBy = UUID.randomUUID()
      )

      val result = task.complete()
      result.success.value mustBe a [Task]

      val completedTask = result.success.value
      completedTask.uuid mustBe task.uuid
      completedTask.status mustBe Completed
    }

    "return an error if already completed" in {
      val task = Task(
        summary = "do something",
        status = Completed,
        createdBy = UUID.randomUUID()
      )

      val result = task.complete()
      result.failure.exception mustBe an [InvalidTaskStateException]
    }
  }
}
