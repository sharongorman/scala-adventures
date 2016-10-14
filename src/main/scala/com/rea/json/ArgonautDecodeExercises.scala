package com.rea.json

import argonaut._, Argonaut._

import scalaz.{\/-, -\/, \/}

/**
  * Before we can jump straight into decoding, there are a few piece of the jigsaw we need to understand.
  *
  * 1) Parsing strings into argonaut's Json object.
  * 2) Cursors, particularly HCursors.
  * 3) DecodeResult type.
  *
  * Once we have understood these we can understand how to write and wire decoders.
  **/
object ArgonautDecodeExercises {

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
    * Write a method that parses a jsonString.  It should return a scalaz disjunction String \/ Json.
    */

  def parseToJson(myJsonString: String): String \/ Json = myJsonString.parse

  /**
    * So how do we get from the Json to a raw value.
    *
    * The json object provides us with a number of methods:
    * json.string : Option[String]   - returns a string if this json represents a string.
    * json.number : Option[Number]   - guess what!
    * json.obj : Option[JsonObject] - this one is a bit different, it returns a JsonObject if this is a JObject.
    */

  /**
    * Exercise 1.2
    * If the parsed value is a json string, return the string value.
    * (Hint, use the Json.string method)
    * Investigate what the method does when the json is not a JString
    */

  def parseToString(myJsonString: String): Option[String] = {
    myJsonString.parse match {
      case -\/(error) => None
      case \/-(json) => json.string
    }
  }

  /**
    * Exercise 1.3
    * If the parsed value is an object, retun the key-value map
    * Hint: you will need the json.obj method and the JsonObject.toMap.
    */

  def parseToMap(myJsonObject: String) : Option[Map[JsonField, Json]] = {
    myJsonObject.parse match {
      case -\/(error) => None
      case \/-(json) => json.obj.map(_.toMap)
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
      description <- descriptionJson.string
    } yield description
  }

  /** Exercise 1.5
    * Extracting a single value from a Json object.
    * Take a look at the "-||" method of Json
    * def -||(fs: List[JsonField]): Option[Json]
    *
    * It allows us to navigate through a list of keys, and return the Json object found there.
    * Use this to write parseAgentId
    */

  def parseAgentId(propertyJson: String) : Option[String] = {
    propertyJson.parse match {
      case -\/(error) => None
      case \/-(json) => {
        (json -|| ("agent" :: "agentId" :: Nil)).flatMap(_.string)
      }
    }
  }


  /**
    * Part 2: Cursors and HCursors
    * ===================================================
    * So, once we have a complex json structure, decoding it to a map and then traversing that map is painful.
    * Is there a better way?
    * We can navigate around Json structures using Cursors
    * Cursors - hold a pointer to Json, and allow us to navigate the pointer around:
    *
    * We can create a cursor from any Json object using the "cursor" method.
    * And we can read the Json object that the cursor is currently pointing at using the "focus" method.
    *
    * It doesn't take long in argonaut to come accross a couple of different variations on cursors.  HCursors and ACursors.
    * These both have similar navigation and focus methods available.
    *
    * HCursors - are HistoryCursors that maintain their navigation history (useful for error or logging puposes).  In general
    *            we use these rather than bare cursors.  The navigation methods on these return an ACursor.
    *            Get an hcursor using Json.hcursor method.
    * ACursors - I think means "AnswerCursor" , representing a result of a cursor naviation
    *            they are a case class holding an HCursor\/HCursor , where the "left" represents a cursor that has failed
    *            a navigation, while a right indicates it was successful.  Use the "any" method to get back an hcursor.
    *
    *
    *

    *
    * Navigation:
    * There are many methods to move a cursor but perhaps the most common is:
    * def --\(q: JsonField): Option[Cursor]   ** Move the cursor down to a JSON object at the given field
    *
    * We are also going to use
    * def -- (q: JsonField): Option[Cursor]  ** Move the cursor to the given sibling field in a JSON object
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

  def fetchDescription(propertyJson: Json): Option[Json] = (propertyJson.cursor --\"description").map(_.focus)

  /** Exercise 2.2
    * Lets do the same, for agentName.

    */

  def fetchAgentName(propertyJson: Json): String \/ Json = {
    val agentNameCursor: ACursor = propertyJson.hcursor --\ "agent" --\ "agentName"
    agentNameCursor.focus match {
      case None => -\/(s"Failed getting agent name : ${agentNameCursor}")
      case Some(agentName) => \/-(agentName)
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
