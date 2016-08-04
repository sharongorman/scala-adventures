package com.rea.json

import argonaut._, Argonaut._

object ArgonautExercises {
  /**
    * Exercise 1
    * Argonaut models JSON as a sealed trait  @argonaut.Json.
    * There are five cases of the Json trait :
    * JNull,JBool(b: Boolean),JNumber(n: JsonNumber),JString(s: String),JArray(a: JsonArray),JObject(o: JsonObject)
    * We don't instantiate these directly but instead use constructor methods : jString, jBoolean, etc...
    *
    * Once we have a Json instance, we can print it out to a string using the Json.nospaces Json.spaces2 or Json.pretty methods
    * Encode a simple json string, using the jString method
    *
    */

  def writeJsonString(value: String): String = ???

  /** Exercise 2
    * Encode a simple boolean  Hint: use jBool method.
    */
  def writeJsonBoolean(value: Boolean): String = ???

  /** Exercise 3
    * Encode an array of strings
    * This time use the jArray method, which takes a JsonArray ... which is just a type alias for List[Json]
    * Hint: use the methods explored above to convert the List[String].
    */
  def writeJsonArray(values: List[String]): String = ???

  /** Exercise 4
    * Encode our first object
    * We can use the @argonaut.Json.apply(fields: (JsonField, Json)*) method, which takes a
    * vararg of tuples (JsonField, Json).
    * JsonField is just a type alias for String.
    * We are after: '{"surname":"Jones","firstNames":["james","henry"],"principle":true}' NB: ignore agentId for now.
    */
  case class Agent(surname: String, firstNames: List[String], principal: Boolean, agentId: Option[String] = None)

  def writeAgent(agent: Agent): String = ???

  /** Introducing Codecs
    * This is getting a bit tedious.  Wouldn't it be nice if it could work out how to encode the field
    * values based on the type of the value?
    * Codecs to the rescue!
    * We can construct a tuple of (JsonField, Json) by using the := method.
    * This is a method added on String by aronaut, that uses an implicit encoder on the passed object, to give you back the tuple.
    *  e.g.
    */
  val myField = "myField" := "some string"  // use your IDE to explore implicit parameters and the := method in this variable definition.

  /** Exercise 5
    * Rewrite agent encoding using the codecs style
    */
  def writeAgent2(agent: Agent): String = ???

  /**
    * But I have a field that is an Option ... I only want to write it if it exists!
    * Exercise 6
    * Option 1: we could use normal scala to create our list of fields, only adding it if it exists.
    *
    */


  /**
    * Exercise 7
    * Option 2: we could use Argonauts "cons" format to conditionally add fields.
    * The ->: method on Json is a right associative method that takes a tuple (JsonField, Json)
    * We start with a jEmptyObject and add fields to it.
    */

//  val myObject : Json = ???


  /** We need a couple of other argonaut methods in our tool kit to add optional fields
    * First the :=? field constructor:
    */
//    val myMaybeObject: Option[String] = None
  //val myMaybeField: Option[(String, Json)] = "option" :=? None

  /** And now the ->?: field appender on @argonaut.Json
    * It will only append a option of tuple if the tuple exists.
    */
//  val myOtherObject: Json = ???


  /**
    * Write our own encoders....
    */


}
