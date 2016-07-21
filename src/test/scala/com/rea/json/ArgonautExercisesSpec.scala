package com.rea.json

import org.specs2.mutable.Specification
import ArgonautExercises._
import argonaut._, Argonaut._

class ArgonautExercisesSpec extends Specification {

  "ArgonautExcercises" should {
    "Exercise 1 prints a string" in {
      writeJsonString("yup, json string") must beEqualTo(""""yup, json string"""")
    }

    "Exercise 2" in {
      "prints true for a boolean true" in {
        writeJsonBoolean(true) must beEqualTo("""true""")
      }
      "prints false for a boolean false" in {
        writeJsonBoolean(false) must beEqualTo("""false""")
      }
    }

    "Exercise 3 encodes an array of string" in {
      writeJsonArray(List("a", "json", "array")) must beEqualTo("""["a","json","array"]""")
    }

    "Exercise 4 encodes an Agent object" in {
      val agent = Agent("Jones", List("james", "henry"), true)
      writeAgent(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principle":true}""")
    }

    "Exercise 5 encodes an Agent object" in {
      val agent = Agent("Jones", List("james", "henry"), true)
      writeAgent2(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principle":true}""")
    }

  }
}
