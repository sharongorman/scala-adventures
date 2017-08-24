package com.rea.json

import com.rea.json.CirceEncodingExercises.Agent
import io.circe._



/**
  * Before we can jump straight into decoding, there are a few piece of the jigsaw we need to understand.
  *
  * 1) Parsing strings into circe's Json object.
  * 2) Cursors, particularly HCursors.
  * 3) DecodeResults and DecodingFailure.
  * 4) Decoders
  *
  * Once we have understood these we can understand how to write and wire decoders.
  **/
object CirceDecodeExercises {

  /**
    * Part 1: Parsing strings into circe's Json object.
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
    * The first bit is trivial using the io.circe.parser.parse method:
    *
    * def parse: parse(input: String): Either[ParsingFailure, Json]
    *
    */

  /**
    * Exercise 1.1
    * Write a method that parses a jsonString.  It should return an Either[ParsingFailure,Json]
    *
    */

  def parseToJson(myJsonString: String): Either[ParsingFailure, Json] = parser.parse(myJsonString)

  /**
    * Thats the first stage of : jsonString --(parse)--> Json --(decode)--> myObject
    *
    * So how do we get from the Json to our model objects?.
    *
    * The Json object provides us with a number of methods to deal with the base cases:
    * Json.asBoolean: Option[Boolean]   - returns a string if this json represents a string.
    * Json.asNumber: Option[JsonNumber] - guess what!
    * Json.asObject: Option[JsonObject]- this one is a bit different, it returns a JsonObject if this is a JObject.
    */

  /**
    * Exercise 1.2
    * If the parsed value is a json string, return the string value.
    * (Hint, use the Json.asString method)
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
    * Building on the parseToMap method we just wrote,
    * build on this to read the description as a string.
    *
    *
    * Assuming that your json structure looks like:
    *
    *  {"description" : "a great house",
    *   "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":true}
    *  }
    *
    *  Return the property description as a string
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
    * That's the end of part 1.
    * If all you are doing is pulling a couple of strings out of a json, that may well be sufficient.
    * But is does have a number of drawbacks:
    * 1. It is pretty verbose
    * 2. If it doesn't work, yuo will just get a None, no further indication of what went wrong.
    *
    * Luckily, we can improve on this.  But first we are going to need to take a detour through Cursors.
    */


  /**
    * Part 2: ACursors and HCursors
    * ===================================================
    * We have seen that once we have a complex json structure,
    * decoding it to a map and then traversing that map has some drawbacks.
    *
    * Is there a better way?
    * The first step to understanding Circe's solution to this problem, is to understand Cursors.
    * Cursors allow us to navigate around the json structure in a composable, immutable manner.
    * (They are implemented using a functional programming technique called zippers ... which we don't need
    * to understand for our purposes).
    * We are going to use a simple mental model of a cursor:
    * Cursors - hold a pointer to Json, and allow us to navigate the pointer around.
    *
    * We can create a cursor from any Json object using the "hcursor" method.
    * And we can read the Json object that the cursor is currently pointing at using the "focus" method.
    *
    * A quick not on "HCursors" vs "ACursors
    * =======================================
    * HCursor stands for "History Cursor"
    *    - which is a cursor that remembers it's navigation history, and is always pointing at a valid point in the json
    * ACursor stands for "Answer Cursor"
    *    - which is a cursor that might or might not be pointing at a valid location in the json.
    *    (e.g. if we tried to navigate to somewhere that doesn't exist).
    *
    * The methods we will use on cursors will mostly abstract this away from us, so we don't need to worry too much.
    *
    *
    *
    * Cursor Navigation:
    * ==================
    * There are many methods to move a cursor but lets start with a simple one:
    * def downField(k: String): ACursor   ** Move the cursor down to a JSON object at the given field
    *
    * We are also going to use
    * def field (k: String): ACursor  ** Move the cursor to the given sibling field in a JSON object
    *
    *  Assuming that your json looks like:
    *  {"description" : "a great house",
    *   "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":true}
    *  }
    *
    *  Hint: remember the ACursor.focus method?  def focus: Option[Json]
    *
    */

  /**
    * Exercise 2.1
    *
    * Write "fetchDescription" that takes the above Json and returns the Json description.
    * For now return an Option[Json] to handle the case where it doesn't exist.
    */

  def fetchDescription(propertyJson: Json): Option[Json] = propertyJson.hcursor.downField("description").focus


  /** Exercise 2.2
    * Lets do the same, for agent surname.
    * But this time we want to be a bit nicer to our consumer, and return a helpful
    * error message in a string if we fail.
    */

  def fetchAgentSurname(propertyJson: Json): Either[String, Json] = {
    val agentSurnameCursor: ACursor = propertyJson.hcursor downField  "agent" downField "surname"
    agentSurnameCursor.focus match {
      case None => Left(s"Failed getting agent name : ${agentSurnameCursor}")
      case Some(surname) => Right(surname)
    }
  }

  /**
    * Look again at exercise 2.2
    * How many ways could this fail?  How will we express this?
    * Fire up the CirceDecode.sc worksheet to explore how the ACursor expresses this, look at the cursor history, and the either field values
    * various failure scenarios.
    * What happens if you want to return a String version of the surname?
    */








  /**
    * Part 3: DecodeResults
    * =====================
    * Luckily circe has thought about how to represent failures in decoding.
    *
    * It is not dis-similar to how we modelled errors in the Error Excercises. (It is a little more complex, but same idea)
    * Decoding results are represented as the type Decoder.Result[A]
    * But this is just a type alias for an Either:
    * type Result[A] = Either[DecodingFailure, A]
    *
    * And that DecodingFailure is an extension of Exception.
    *
    * So we can create our own decoding results:
    *
    * val success:Decoder.Result[String] = Right("my string")
    * val failure:Decoder.Result[String] = Left(DecodingFailure("somehthing went wrong", Nil))
    *
    */

  /**
    * Exercise 3.1
    *
    * Create a method cursorResult:
    * def cursorResult(cursor: ACursor): Decoder.Result[String]
    * For now we are not going to worry about the json in the cursor, just whether it is in a failed state.
    *
    * If the cursor is failed, return a DecodingFailure with the message "oopsie", and the cursor history populated.
    * Otherwise return a success message "Woo hoo"
    * Explore the Cursor and DecodingFailure methods to see how to include the history.
    */

    def cursorResult(cursor: ACursor): Decoder.Result[String] = {
      if (cursor.failed) Left(DecodingFailure("oopsie", cursor.history))
      else Right("Woo hoo")
    }





  /**
    * Part 4.  Pulling it all together into decoders.
    * ===============================================
    *  Similar to it's Encoder trait, circe provides a Decoder trait, that wraps a single abstract method
    *     def apply(c: HCursor): Decoder.Result[A]
    *
    *
    *  Circe provides Decoders for standard types such as String (take a look in it's Decoder object, for methods marked with  `@group Decoding`
    *  Our import io.circe._ has brought these into scope for us.
    *
    *  Cursors have a method that we can uses these implicit decoders:
    *     def as[A](implicit d: Decoder[A]): Decoder.Result[A]
    *
    *  So decoding a simple string becomes:
    *
    *  def stringFromJson(curson: Hcursor): Decoder.Result[String] = cursor.as[String]
    *
    */


  /** Exercise 4.1
    * Rewrite fetchAgentSurname once more, but this time, lets use a decoder.
    * Once we navigate to the correct field, we can use the `as` method.
    *
    * When you have it working, see if you can use the cursor get method, to make your code simpler.
    *
    * ACursor.get[A](field: String) simply does a "downField" to the named field, and then calls "as" for the
    * given type
    */

//Solution using as:
  def fetchAgentSurname2(propertyJson: Json): Decoder.Result[String] = {
    propertyJson.hcursor.downField("agent").downField("surname").as[String]
  }

//Solution combining downField and as to get:
//  def fetchAgentSurname2(propertyJson: Json): Decoder.Result[String] = {
//     propertyJson.hcursor downField  "agent" get[String] "surname"
//  }

  /** Exercise 4.2 Fetching multiple values
    * What if we want to fetch multiple values from the same cursor.
    * For instance, lets say we want to fetch the Agent name and id, and wrap them up in our Agent case class?
    *
    * Note, since cursors are immutable, we can reuse the same cursor as the starting point for multiple values... Nice.
    *
    * Since a Decoder.Result[A] is simply a type alias for an Either, we can use a for comprehension.
    * If we have a Json object representing a sandwich:
    *
    *
    * def decodeSandwich(cursor: ACursor): Decoder.Result[Sandwich] = for {
    *   bread <- cursor.get[String]("bread")
    *   filling <- cursor.get[String]("filling")
    * } yield Sandwich(bread,filling)
    *
    *
    * Try it now to implement decodeAgent.
    *
    * Remember our agent json looks like:
    * {"surname":"Jones","firstNames":["james","henry"],"principal":true}
    *
    */


  case class Agent(surname: String, firstNames: List[String], principal: Boolean, agentId: Option[String] = None)


  def decodeAgent(cursor: ACursor): Decoder.Result[Agent] = for {
    surname <- cursor.get[String]("surname")
    firstNames <- cursor.get[List[String]]("firstNames")
    principal <- cursor.get[Boolean]("principal")
  } yield Agent(surname, firstNames, principal)


  /**
    * Exercise 4.3 - Creating our own decoders
    *
    * So far we have used the built in decoders.
    * But what if we want to create our own decoder, e.g. for Agent, and
    * then use it to decode the agent for a property?
    *
    * Property json:
    *   { "description" : "a great house",
    *     "agent" : {"surname":"Jones","firstNames":["james","henry"],"principal":true}
    *   }
    *
    * Remember a Decoder is simply a trait with an abstract method:
    * def apply(c: HCursor): Decoder.Result[A]
    *
    * Play with different ways to create your decoder from the decodeAgent method .... how simple
    * can you make it?
    *
    */

  case class Property(description: String, agent: Agent)


  implicit val agentDecoder: Decoder[Agent] = decodeAgent
  //Notice we only have to give the method?  Scala is clever enough to wrap it in the trait!

  def decodeProperty(cursor: HCursor): Decoder.Result[Property] = for {
    description <- cursor.get[String]("description")
    agent <- cursor.get[Agent]("agent")
  } yield Property(description, agent)

  /**
    * Exercise 4.4 - Adapting decoders
    *
    * Sometimes we want to do further processing with the decoded value.
    * Here we remodel the agent's surname to a Surname class.
    *
    * Rather than writing a decoder from scratch, as we have done before,
    * use the Decoder.map method to adapt the string decoder (Decoder.decodeString), and create
    * a decoder for Surname
    *
    */

  case class Surname(value: String)

  case class Agent2(surname: Surname, firstNames: List[String], principal: Boolean, agentId: Option[String] = None)


  def decodeAgent2(cursor: HCursor): Decoder.Result[Agent2] = {
    implicit val surnameDecoder: Decoder[Surname] = Decoder.decodeString.map(Surname.apply)

    for {
      surname <- cursor.get[Surname]("surname")
      firstNames <- cursor.get[List[String]]("firstNames")
      principal <- cursor.get[Boolean]("principal")
    } yield Agent2(surname, firstNames, principal)
  }


  /**
    * Exercise 4.5 - Adapting decoders with errors
    * We are suddently very exclusive ... we are only expecting
    * Agents that are principles.
    *
    * Adapt our agentDecoder to fail if the agent isn't a principal,
    * or return a new "PrincipalAgent" class if it is
    * Hint: use the Decoder.emap method
    * Notice how this method maps our failure message into a DecodingFailure
    */

    case class PrincipalAgent(surname: String)

    def decodePrincipalAgent(agentJson: Json): Decoder.Result[PrincipalAgent] = {
      val principalsOnlyDecoder: Decoder[PrincipalAgent] = agentDecoder.emap { agent =>
        if (agent.principal) Right(PrincipalAgent(agent.surname))
        else Left("Not a principal")
      }

      principalsOnlyDecoder.decodeJson(agentJson)
    }





  /**
    * Excercise 4.6 - Returning json strings from decoders
    *
    * Finally, here is something we have had to do a bit of in the buy pipeline.
    *
    * Assume that you want to decode the property json, but this time the
    * property has a string field that holds the agent json string.  i.e.
    * we don't want to decode the agent at this point, we just want to pass on it's json
    */


  case class PropertyWithAgentJson(description: String, agentJson: String)


  def decodePropertyWithAgencyJson(propertyJson: Json): Decoder.Result[PropertyWithAgentJson] = {
    val cursor = propertyJson.hcursor

    for {
      description <- cursor.get[String]("description")
      agencyJson <- cursor.get[Json]("agent").map(_.noSpaces)
    } yield  PropertyWithAgentJson(description, agencyJson)
  }
}
