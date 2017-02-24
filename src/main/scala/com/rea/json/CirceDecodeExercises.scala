package com.rea.json

import io.circe._


/**
  * Before we can jump straight into decoding, there are a few piece of the jigsaw we need to understand.
  *
  * 1) Parsing strings into circe's Json object.
  * 2) Cursors, particularly HCursors.
  * 3) DecodeResults and DecodingFailure.
  *
  * Once we have understood these we can understand how to write and wire decoders.
  **/
object CirceDecodeExercises {

  /**
    * Part 1: Parsing strings into argonaut's Json object.
    * ===================================================
    * Remember that encoding was a two stage process:
    *
    * myObject --(encode)--> Json --(write)--> jsonString
    *
    * Similarly when decoding we need to first trasform our
    * jsonString to a Json model, and then into our domain model
    *
    * jsonString --(parse)--> Json --(decode)--> myObject
    *
    * The first bit is trivial using the ParseWrap.parse method:
    *
    * def parse: String \/ Json
    *
    * This method is implicity added to Strings via the StringWrap trait.
    */

  /**
    * Exercise 1.1
    * Write a method that parses a jsonString.  It should return an Either[ParsingFailure,Json], where the left hand side.
    */

  def parseToJson(myJsonString: String): Either[ParsingFailure, Json] = parser.parse(myJsonString)

  /**
    * So how do we get from the Json to a raw value.
    *
    * The Json object provides us with a number of methods:
    * Json.asBoolean: Option[Boolean]   - returns a string if this json represents a string.
    * Json.asNumber: Option[JsonNumber] - guess what!
    * Json.asObject: Option[JsonObject]- this one is a bit different, it returns a JsonObject if this is a JObject.
    */

  /**
    * Exercise 1.2
    * If the parsed value is a json string, return the string value.
    * (Hint, use the Json.string method)
    * Investigate what the method does when the json is not a JString
    */

  def parseToString(myJsonString: String): Option[String] = {
    parser.parse(myJsonString) match {
      case Left(error) => None
      case Right(json) => json.asString
    }
  }

  /**
    * Exercise 1.3
    * If the parsed value is an object, retun the key-value map
    * Hint: you will need the Json.asObject method and the JsonObject.toMap.
    */

  def parseToMap(myJsonObject: String) : Option[Map[String, Json]] = {
    parser.parse(myJsonObject) match {
      case Left(error) => None
      case Right(json) => json.asObject.map(_.toMap)
    }
  }

  /** Exercise 1.4
    * Extract a value from a map
    * Assuming that your json looks like:
    *  {"description" : "a great house",
    *   "agent" : {
    *     "agentName" : "Wonder Agent",
    *     "agentId" : "WA1XXX"
    *   }
    *  }
    *
    *  Return the description as a string
    *
    */

  def parseDescription(propertyJson: String) : Option[String] = {
    for {
      fieldMap <- parseToMap(propertyJson)
      descriptionJson <- fieldMap.get("description")
      description <- descriptionJson.asString
    } yield description
  }


  /**
    * Part 2: ACursors and HCursors
    * ===================================================
    * So, once we have a complex json structure, decoding it to a map and then traversing that map is painful.
    * Is there a better way?
    * We can navigate around Json structures using Cursors
    * Cursors - hold a pointer to Json, and allow us to navigate the pointer around:
    *
    * We can create a cursor from any Json object using the "hcursor" method.
    * And we can read the Json object that the cursor is currently pointing at using the "focus" method.
    *
    * An HCursor : contains the naviation "history"
    * An ACursor: is an "answer cursor", indicating it can be in a failed state as the result of a failed navigation.
    *
    * You can still navigate on a failed cursor, but the result will always be a failed cursor.
    *
    *
    *
    * Navigation:
    * There are many methods to move a cursor but perhaps the most common is:
      * def downField(k: String): ACursor   ** Move the cursor down to a JSON object at the given field
    *
    * We are also going to use
    * def field (k: String): ACursor  ** Move the cursor to the given sibling field in a JSON object
    *
    *  Assuming that your json looks like:
    *  {"description" : "a great house",
    *   "agent" : {
    *     "agentName" : "Wonder Agent",
    *     "agentId" : "WA1XXX"
    *   }
    *  }
    *
    *
    *
    */

  /**
    * Exercise 2.1
    *
    * Write "fetchDescription" that takes the above Json and returns the Json description.
    * For now return an Option[String] to handle the case where it doesn't exist.
    */

  def fetchDescription(propertyJson: Json): Option[Json] = propertyJson.hcursor.downField("description").focus

  /** Exercise 2.2
    * Lets do the same, for agentName.

    */

  def fetchAgentName(propertyJson: Json): Either[String, Json] = {
    val agentNameCursor: ACursor = propertyJson.hcursor downField  "agent" downField "agentName"
    agentNameCursor.focus match {
      case None => Left(s"Failed getting agent name : ${agentNameCursor}")
      case Some(agentName) => Right(agentName)
    }
  }

  /**
    * Look again at exercise 2.2
    * How many ways could this fail?  How will we express this?
    * Fire up the ArgonautDecode.sc console to explore how the ACursor expresses this, look at the cursor history, and the either field values
    * various failure scenarios.
    * What happens if you want to return a String version of the agentName?
    */








  /**
    * Part 3: DecodeResults
    * =====================
    * Luckily argonaut has thought about how to represent failures in decoding.
    *
    * Rather than using a raw disjunction(\/) to represent success/failure
    * Argonaut uses a "DecodeResult[A]" class.
    * This holds a disjunction (String, CursorHistory)\/ A
    *
    * We create a successful result:
    *   DecodeResult.ok[A](value : A)
    * And a failure:
    *   DecodeResult.fail[A](s : scala.Predef.String, h : argonaut.CursorHistory)
    *
    *
    *
    */

}
