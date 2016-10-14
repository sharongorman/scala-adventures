package com.rea.json

import com.rea.json.ArgonautDecodeExercises._
import argonaut._, Argonaut._
import org.specs2.mutable.Specification

import scalaz.{\/-, -\/}

class ArgonautDecodeExercisesSpec extends Specification {

  "ArgonautDecodeExcercises" should {
    "Exercise 1.1 " should {
      "parse a valid json string to Json" in {
        parseToJson(""""yup, json string"""") match {
          case -\/(error) => ko(s"Error parsing json: $error")
          case \/-(json) => json must beEqualTo(jString("yup, json string"))
        }

        "report a parsing error" in {
          parseToJson("""im not json""") match {
            case -\/(error) => ok
            case \/-(json) => ko("should have failed parsing invalid json")
          }
        }
      }
    }

    "Exercise 1.2" should {
      "parse a valid json string to a String object" in {
        parseToString(""""yup, json string"""") match {
          case None => ko(s"Error parsing json")
          case Some(stringValue) => stringValue must beEqualTo("yup, json string")
        }
      }
      "return None when the json is invalid" in {
        parseToString("""im not json""") match {
          case None => ok
          case Some(stringValue) => ko("should have failed parsing invalid json")
        }
      }
      "return None when the json is not a json string" in {
        parseToString("""80""") match {
          case None => ok
          case Some(stringValue) => ko("should have failed converting to a string, a number is not a string")
        }
      }
    }

    "Exercise 1.3" should {
      "parse a valid json object into a key value map" in {
        val json = """
                     |{
                     |"key1": "value1",
                     |"key2": "value2"
                     |}
                   """.stripMargin
        parseToMap(json) match {
          case None => ko("failed to parse json")
          case Some(result) => result must beEqualTo(
            Map(
              "key1" -> jString("value1"),
              "key2" -> jString("value2")
            )
          )
        }
      }
      "return None when the json is not valid" in {
        val json = """im not json"""
        parseToMap(json) match {
          case None => ok
          case Some(json) => ko("Whoops this should have failed")
        }
      }
      "return None when the json is not an object" in {
        val json = """"yup, json string""""
        parseToMap(json) match {
          case None => ok
          case Some(json) => ko("Whoops this should have failed")
        }
      }
    }

    "Exercise 1.4" should {
      "extract the description" in {
        val json =
          """
            |{"description" : "a great house",
            | "agent" : {
            |   "agentName" : "Wonder Agent",
            |   "agentId" : "WA1XXX"
            | }
            |}
          """.stripMargin
        parseDescription(json) match {
          case None => ko("No, it didn't work")
          case Some(description) => description must beEqualTo("a great house")
        }
      }
    }

    "Exercise 1.5" should {
      "extract the description" in {
        val json =
          """
            |{"description" : "a great house",
            | "agent" : {
            |   "agentName" : "Wonder Agent",
            |   "agentId" : "WA1XXX"
            | }
            |}
          """.stripMargin
        parseAgentId(json) match {
          case None => ko("No, it didn't work")
          case Some(agentId) => agentId must beEqualTo("WA1XXX")
        }
      }
    }

    "Exercise 2.1" should {
      "extract the description" in {
        val json =
          """
            |{"description" : "a great house",
            | "agent" : {
            |   "agentName" : "Wonder Agent",
            |   "agentId" : "WA1XXX"
            | }
            |}
          """.stripMargin
        json.parse.map(fetchDescription) match {
          case -\/(error) => ko(s"failed with: $error")
          case \/-(None) => ko("No description value returned")
          case \/-(Some(description)) => description must beEqualTo(jString("a great house"))
        }
      }
    }

    "Exercise 2.2" should {
      "extract the agent name" in {
        val json =
          """
            |{"description" : "a great house",
            | "agent" : {
            |   "agentName" : "Wonder Agent",
            |   "agentId" : "WA1XXX"
            | }
            |}
          """.stripMargin
        json.parse.flatMap(fetchAgentName) match {
          case -\/(error) => ko(s"failed with: $error")
          case \/-(agentName) => agentName must beEqualTo(jString("Wonder Agent"))
        }
      }
    }
  }


}
