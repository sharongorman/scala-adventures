package com.rea.error

import org.specs2.mutable.Specification
import ErrorExercises._


class ErrorExercisesSpec extends Specification {

  "ErrorExercisesSpec" should {
    "Exercise 1" should {
      "return an error" in {
        findAgent(3) must beLeft
      }

      "return success" in {
        findAgent(1) must beRight(agent1)
      }
    }

    "Exercise 2" should {
      "return an error" in {
        findAgentAnswer(3) must beLeft
      }

      "return success" in {
        findAgentAnswer(1) must beRight(s"The agent is ${agent1.name}")
      }
    }


    "Exercise 3" should {
      "return an error when the property doesn't exist" in {
        findPropertyAgent(10) must beLeft(AppError("property 10 not found"))
      }

      "return an error when the agent doesn't exist" in {
        findPropertyAgent(17) must beLeft(AppError("agent 0 not found"))
      }

      "find the correct agent" in {
        findPropertyAgent(12) must beRight(agent1)
      }
    }

    "Exercise 4" should {
      "return agents" in {
        findAgents(Vector(1,2)) must beEqualTo(Vector(Right(agent1), Right(agent2)))
      }

      "return an error if agent not found " in {
        findAgents(Vector(0,2)) must beEqualTo(Vector(Left(AppError("agent 0 not found")), Right(agent2)))
      }

    }

    "Exercise 5" should {
      val agent1 = Agent(1, "Arabella Gilliums")
      val agent2 = Agent(2, "Gurmley Warblett")

      "return successful Vector[Agent]" in {
        sequenceAgents(Vector(Right(agent1), Right(agent2))) must beEqualTo(Right(Vector(agent1, agent2)))
      }

      "return an error if agent not found " in {
        val error = AppError("Oh my.")
        sequenceAgents(Vector(Right(agent1), Left(error))) must beEqualTo(Left(error))
      }
    }


    "Exercise 6" should {
      "return agents" in {
        findAllAgents(Vector(1,2)) must beRight(Vector(agent1, agent2))
      }

      "return an error if agent not found " in {
        findAllAgents(Vector(0,2)) must beLeft(AppError("agent 0 not found"))
      }
    }

    "Exercise 7" should {
      "return unit if there are not missing agents" in {
        checkAllAgents(Vector(1,2)) must beRight(())
      }

      "return an error if agent not found " in {
        checkAllAgents(Vector(0,2)) must beLeft(AppError("agent 0 not found"))
      }
    }

    "Exercise 8" should {
      "return unit if there are not missing agents" in {
        findAllAgentsNames(Vector(1,2)) must beRight(Vector(agent1.name, agent2.name))
      }

      "return an error if agent not found " in {
        findAllAgentsNames(Vector(0,2)) must beLeft(AppError("agent 0 not found"))
      }
    }

    "Exercise 9" should {
      "return agents" in {
        findSomeAgents(Vector(1, 2)) must beEqualTo(Nil, Vector(agent1, agent2))
      }

      "return an error if agent not found " in {
        findSomeAgents(Vector(0, 2)) must beEqualTo(Vector(AppError("agent 0 not found")), Vector(agent2))
      }
    }

    "Exercise 10" should {
      "return a suggestion string " in {
        suggestAProperty(12, 1) must beRight(s"Hey ${agent1.name} how about selling A great house")
      }

      "return an error if agent not found " in {
        suggestAProperty(12, 0) must beLeft(AppError("agent 0 not found"))
      }

      "return an error if property not found " in {
        suggestAProperty(0, 1) must beLeft(AppError("property 0 not found"))
      }
    }

    "Exercise 11" should {
      "return a suggestion string " in {
        suggestAProperty2(12, 1) must beRight(s"Hey ${agent1.name} how about selling A great house")
      }

      "return an error if agent not found " in {
        suggestAProperty2(12, 0) must beLeft(AppError("agent 0 not found"))
      }

      "return an error if property not found " in {
        suggestAProperty2(0, 1) must beLeft(AppError("property 0 not found"))
      }
    }

    "Exercise 10" should {
      "return a suggestion string " in {
        suggestAProperty2(12, 1) must beRight(s"Hey ${agent1.name} how about selling A great house")
      }

      "return an error if agent not found " in {
        suggestAProperty2(12, 0) must beLeft(AppError("agent 0 not found"))
      }

      "return an error if property not found " in {
        suggestAProperty2(0, 1) must beLeft(AppError("property 0 not found"))
      }
    }

  }

  val agent1 = Agent(1 , "Hocking Stuart")
  val agent2 = Agent(2, "Ellis Jones")
}
