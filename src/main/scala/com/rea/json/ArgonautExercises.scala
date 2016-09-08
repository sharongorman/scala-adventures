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
    * We are after: '{"surname":"Jones","firstNames":["james","henry"],"principal":true}' NB: ignore agentId for now.
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
    *
    * Option 1: we could use normal scala to create our list of fields, only adding it if it exists.
    *
    *
    * Exercise 6
    *
    *
    * Rewrite agent encoding to also encode the agentid if it exists.
    * For now, create a List of tuples, only adding the agentid tuple to the list if it is required.
    *
    */

  def writeAgent3(agent: Agent): String = ???

  /**
    *
    * Option 2: There are a few new concepts that we can introduce to conditionally add fields in a slightly nicer DSL.
    * We will need a few new argonaut concepts to do this.
    *
    * 1. Cons method of creating objects
    * ==================================
    * The ->: method on Json is a right associative method that takes a tuple (JsonField, Json) and adds it to
    * the jObject, much like in list a :: myList adds an item to a list.
    * We start with a jEmptyObject and add fields to it.
    *
    * e.g. val myObject : Json = ("key1" := "hello") ->: ("key2" := "world") ->: jEmptyObject  // {"key2":"world","key1":"hello"}
    *
    * NOTE: it appends rather than prepends the key!
    *
    *
    * 2. Creating optional tuples
    * ============================
    * The :=? method is implicitly added to String by Argonaut, it gives us an optional tuple.  (I see it can also be written: :?=  )
    * def :=?[A: EncodeJson](a: Option[A]):Option[(JsonField, Json)]
    * val notAnOption: Option[String] = None
    * val myMaybeField = "option" :=? notAnOption // None
    * val myMaybeField2 = "option" :=? Some("value") //
    *
    *
    * 3. Optionally adding a tuple to an object
    * =========================================
    * And now the ->?: field appender on @argonaut.Json
    * It will only append a option of tuple if the tuple exists.
    * def ->?:(o: Option[(JsonField, Json)]): Json
    *
    * (("field1" :=? notAnOption) ->?: jEmptyObject) // {}
    *
    * (("field1" :=? Some("my value")) ->?: jEmptyObject).nospaces //{"field1":"my value"}
    *
    *
    *
    * Exercise 7
    *
    *
    * Rewrite agent encoding using the :=? and ->?: methods
    *
    **/

  def writeAgent4(agent: Agent): String = ???


  /**
    * Option 3
    * Use a custom formater, in place of no spaces to strip out optional values when you are converting from Json to a string
    *  val formatter = PrettyParams.nospace.copy(dropNullKeys = true)
    *
    *
    * Exercise 8
    *
    *
    * Rewrite agent encoding the above pretty formatter (Use the Json.pretty(p: PrettyParams)) method.
    *
    **/

  val formatter = PrettyParams.nospace.copy(dropNullKeys = true)

  def writeAgent5(agent: Agent): String = ???


  /**
    * Exercise 9 - Writing custom encoders
    *
    * Using the := method to find the build in encoders automatically tidied
    * up our code nicely.   But what if there isn't a build in encoder for our type?
    *
    * Consider a property that contains an agent.
    * We want to write
    * def writeProperty(property:
    *
    */

  case class Property(description: String, agent: Agent)

  /**
    * Write out an encoder that just uses the syntax from Exercise 4,
    * and a simple encode Agent method.
    */



  def writeProperty(property: Property): String = {
    def encodeAgent(agent: Agent): Json = ???
    Json(
      "description" := property.description,
      "agent" -> ???
    ).nospaces
  }

  /**
    * Exercise 10
    *
    * But how do we replace that "-> encodeAgent(...)" so that it implicitly finds it.
    * Look again at the := method definition
    * def :=[A: EncodeJson](a: A)
    *
    * so we need an EncodeJson[Agent]
    * EncodeJson is simply a trait, that has a single abstract method:
    * def encode(a: A): Json
    *
    * Argonaut provides a convenience constructor in the EncodeJson object:
    * def apply[A](f: A => Json): EncodeJson[A]
    * So we can create and instance by calling EncodeJson[Agent](f: Agent => Json)
    *
    * Try again, creating an EncodeJson instance for Agent instread
    */

  def writePropertyWithEncoder(property: Property): String = {
    implicit def AgentEncoder: EncodeJson[Agent] = EncodeJson(???)
    Json(
      "description" := property.description,
      "agent" := property.agent
    ).nospaces
  }


}
