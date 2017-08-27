package com.rea.json

import com.rea.json.CirceDecodeExercises._
import io.circe.Json
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification


class CirceDecodeExercisesSpec extends Specification {

  "CirceDecodeExercises" should {
    "Exercise 1.1 " should {
      "parse a valid json string to Json" in {
        parseToJson(""""yup, json string"""") match {
          case Left(error) => ko(s"Error parsing json: $error")
          case Right(json) => json must beEqualTo(Json.fromString("yup, json string"))
        }
      }

      "report a parsing error" in {
        parseToJson("""im not json""") match {
          case Left(error) => ok
          case Right(json) => ko("should have failed parsing invalid json")
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
              "key1" -> Json.fromString("value1"),
              "key2" -> Json.fromString("value2")
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
            | "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":true}
            |}
          """.stripMargin

        parseDescription(json) match {
          case None => ko("No, it didn't work")
          case Some(description) => description must beEqualTo("a great house")
        }
      }

      "return none when the json doesn't contain a description field" in {
        val json = """ {"other_field": 234} """
        parseDescription(json) match {
          case None => ok
          case Some(description) => ko("No, it didn't work")
        }
      }
    }


    "Exercise 2.1" should {
      "extract the description" in {
        val json =
          """
            |{"description" : "a great house",
            | "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":true}
            |}
          """.stripMargin
        parseToJson(json).map(fetchDescription) match {
          case Left(error) => ko(s"failed with: $error")
          case Right(None) => ko("No description value returned")
          case Right(Some(description)) => description must beEqualTo(Json.fromString("a great house"))
        }
      }
    }

    "Exercise 2.2" should {
      "extract the agent surname" in {
        val json =
          """
            |{"description" : "a great house",
            | "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":true}
            |}
          """.stripMargin
        parseToJson(json).flatMap(fetchAgentSurname) match {
          case Left(error) => ko(s"failed with: $error")
          case Right(agentName) => agentName must beEqualTo(Json.fromString("Jones"))
        }
      }
    }

    def withAgentJson[A]: (Json => MatchResult[A]) => MatchResult[Any] = {
      withJson[A](
        """
          | {"surname":"Jones","firstNames":["james","henry"],"principal":true}
        """.stripMargin
      )
    }

    "Exercise 3.1" should {

      "return a DecoderFalure when passed a failed cursor" in {
        withPropertyJson { json =>
          val failedCursor = json.hcursor.downField("Not a field")
          cursorResult(failedCursor).left.map(_.message) must beLeft("oopsie")
          cursorResult(failedCursor).left.map(_.history) must beLeft(failedCursor.history)
        }
      }
      "return a Right('Woo hoo') when passed a successful cursor" in {
        withPropertyJson { json =>
          val successCursor = json.hcursor.downField("description")
          cursorResult(successCursor) must beRight("Woo hoo")
        }
      }
    }

    "Exercise 4.1" should {

      "extract the agent surname" in {
        withPropertyJson { json =>
          fetchAgentSurname2(json) must beRight("Jones")
        }
      }
    }

    "Exercise 4.2" should {
      "extract the agent" in {
        withAgentJson { json =>
          decodeAgent(json.hcursor) must beRight(Agent("Jones", List("james", "henry"), true))
        }
      }
    }

    def withPropertyJson[A]: (Json => MatchResult[A]) => MatchResult[Any] = {
      withJson[A](
        """
          |{"description" : "a great house",
          | "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":true}
          |}
        """.stripMargin
      )
    }

    "Exercise 4.3 decodes property json" in {
      withPropertyJson { json =>
        decodeProperty(json.hcursor) must beRight(
          Property("a great house",
            Agent("Jones", List("james", "henry"), true)
          ))
      }
    }

    "Exercise 4.4 decodes agent with typed Surname" in {
      withAgentJson { json =>
        decodeAgent2(json.hcursor) must beRight(
          Agent2(Surname("Jones"), List("james", "henry"), true)
        )
      }
    }

    "Exercise 4.5" should {
      "pass when agent is a principal" in {
        withAgentJson { json =>
          decodePrincipalAgent(json) must beRight(
            PrincipalAgent("Jones")
          )
        }
      }

      "fail when agent is not a principal" in {
        withJson(
          """
            |{"description" : "a great house",
            | "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":false}
            |}
          """.stripMargin) { json =>
          decodePrincipalAgent(json) must beLeft
        }
      }
    }

    "Exercise 4.6 decodes property with agent json" in {
      withPropertyJson { json =>
        decodePropertyWithAgencyJson(json) must beRight(
          PropertyWithAgentJson(
            "a great house",
            """{"surname":"Jones","firstNames":["james","henry"],"principal":true}"""
          ))
      }
    }
  }


  def withJson[A](jsonString: String)(f: Json => MatchResult[A]): MatchResult[Any] = {

    parseToJson(jsonString) match {
      case Left(error) => ko(s"failed with: $error")
      case Right(json) => f(json)
    }
  }

}
