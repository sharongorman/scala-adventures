package com.rea.json

import com.rea.json.CirceEncodingExercises._
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification

class CirceEncodingExercisesSpec extends Specification with JsonMatchers {

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
      writeAgent(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principal":true}""")
    }

    "Exercise 5 encodes an Agent object" in {
      val agent = Agent("Jones", List("james", "henry"), true)
      writeAgent2(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principal":true}""")
    }

    "Exercise 6" should {
      "encode an Agent object with no agent id" in {
        val agent = Agent("Jones", List("james", "henry"), true)
        writeAgent3(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principal":true}""")
      }
      "encode an Agent object with agent id" in {
        val agent = Agent("Jones", List("james", "henry"), true, Some("FXDNI"))
        writeAgent3(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principal":true,"agentid":"FXDNI"}""")
      }
    }

    "Exercise 8" should {
      "encode an Agent object with no agent id" in {
        val agent = Agent("Jones", List("james", "henry"), true)
        writeAgent5(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principal":true}""")
      }
      "encode an Agent object with agent id" in {
        val agent = Agent("Jones", List("james", "henry"), true, Some("FXDNI"))
        writeAgent5(agent) must beEqualTo("""{"surname":"Jones","firstNames":["james","henry"],"principal":true,"agentid":"FXDNI"}""")
      }
    }

    "Exercise 9 encodes a property" should {
      val agent = Agent("Jones", List("james", "henry"), true)
      val property = Property("a great house", agent)
      val propertyJson = writeProperty(property)

      "with description" in {
        propertyJson must /("description" -> "a great house")
      }

      "with agent" in {
        "with surname" in {
          propertyJson must /("agent") / ("surname" -> agent.surname)
        }
        "with principal" in {
          propertyJson must /("agent") / ("principal" -> agent.principal)
        }
        "with firstNames" in {
          propertyJson must (/("agent") / ("firstNames")).andHave(exactly(agent.firstNames: _*))
        }

      }
    }



    "Exercise 10 encodes a property" should {
      val agent = Agent("Jones", List("james", "henry"), true)
      val property = Property("a great house", agent)
      val propertyJson = writePropertyWithEncoder(property)

      "with description" in {
        propertyJson must /("description" -> "a great house")
      }

      "with agent" in {
        "with surname" in {
          propertyJson must /("agent") / ("surname" -> agent.surname)
        }
        "with principal" in {
          propertyJson must /("agent") / ("principal" -> agent.principal)
        }
        "with firstNames" in {
          propertyJson must (/("agent") / ("firstNames")).andHave(exactly(agent.firstNames: _*))
        }

      }


    }

  }
}
