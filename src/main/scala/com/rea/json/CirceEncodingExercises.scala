package com.rea.json

import io.circe.{Printer, Encoder, Json}
import io.circe.syntax._


object CirceEncodingExercises {
  /**
    * Exercise 1
    * Circe models JSON as a sealed trait  @circe.Json.
    * There are six cases of the Json trait :
    * JNull,JBool(b: Boolean),JNumber(n: JsonNumber),JString(s: String),JArray(a: JsonArray),JObject(o: JsonObject)
    * We don't instantiate these directly but instead use constructor methods on the Json object: 
    *   Json.fromString(value), Json.fromBoolean(value) etc...
    *
    * Once we have a Json instance, we can print it out to a string using the Json.noSpaces Json.spaces2 
    * For more control on the print format use a configured printer with the Json.pretty method
    *
    * Encode a simple json string, using the Json.fromString method
    *
    */

  def writeJsonString(value: String): String = ???

  /** Exercise 2
    * Encode a simple boolean  Hint: use Json.fromBoolean method.
    */
  def writeJsonBoolean(value: Boolean): String = ???

  /** Exercise 3
    * Encode an array of strings
    * This time use the Json.fromValues method, which takes a Iterable[Json]  (a List[_] is an Iterable[_]!)
    * Hint: use the methods explored above to convert the List[String] to List[Json].
    */
  def writeJsonArray(values: List[String]): String = ???

  /** Exercise 4
    * Encode our first object
    * We can use the @circe.Json.obj(fields: (String, Json)*) method, which takes a
    * vararg of tuples (String, Json).
    * We are after: '{"surname":"Jones","firstNames":["james","henry"],"principal":true}' NB: ignore agentId for now.
    */
  case class Agent(surname: String, firstNames: List[String], principal: Boolean, agentId: Option[String] = None)

  def writeAgent(agent: Agent): String = ???

  /** Introducing Encoders
    * This is getting a bit tedious.  Wouldn't it be nice if it could work out how to encode the field
    * values based on the type of the value?
    * Encoders to the rescue!
    * Circe provides us the asJson method on any class A for which there is an Encoder[A] in implicit scope.
    * Gosh ... that was a mouthful!
    * In plain english "If it knows how to encode the type, it will put an asJson method on the type.  We tell it how to
    *    encode the type by providing an Encoder"
    * Functional foo: If you want to understand the mechanics of how this works, it is using the "typeclass" pattern ...
    * Luckily for us, Circe already provides Encoder instances for lots of types (have a look in it's Encoder object)
    *  e.g.
    */
  val myField = "myField" -> "some string".asJson  // use your IDE to explore implicit parameters in this method call.

  /** Exercise 5
    * Rewrite agent encoding using the Encoder style
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

  def writeAgent3(agent: Agent): String = {

    val mandatoryFields: List[(String, Json)] = List(
      "surname" -> agent.surname.asJson,
      "firstNames" -> agent.firstNames.asJson,
      "principal" -> agent.principal.asJson
    )

    val optionalFields: List[(String, Json)] = ???

    Json.obj(
      ??? : _*
    ).noSpaces

  }



  /**
    * Option 2
    * Use a custom Printer, in place of no spaces to strip out optional values when you are converting from Json to a string
    *  val printer = Printer.noSpaces.copy(dropNullKeys = true)
    *
    *
    * Exercise 8
    *
    *
    * Rewrite agent encoding the above pretty formatter (Use the Json.pretty(p: Printer)) method.
    *
    **/

  val printer = Printer.noSpaces.copy(dropNullKeys = true)

  def writeAgent5(agent: Agent): String = ???


  /**
    * Exercise 9 - Writing custom encoders
    *
    * Using the asJson method to find the build in encoders automatically tidied
    * up our code nicely.   But what if there isn't a build in encoder for our type?
    *
    * Consider a property that contains an agent.
    * We want to write the method
    * def writeProperty(property: Property): String
    *
    */

  case class Property(description: String, agent: Agent)

  /**
    * Write out an encoder that just uses the syntax from Exercise 5,
    * and a simple encode Agent method.
    */



  def writeProperty(property: Property): String = {
    def encodeAgent(agent: Agent): Json = ???

    Json.obj(
      "description" -> property.description.asJson,
      "agent" -> ???
    ).noSpaces
  }

  /**
    * Exercise 10
    *
    * But how do we replace that "-> encodeAgent(...)" so that it implicitly finds it.
    * Look again at the asJson method definition
    * def asJson(implicit encoder: Encoder[A]): Json
    *
    * so we need an Encoder[Agent]
    * Encoder is simply a trait, that has a single abstract method:
    * def apply(a: A): Json
    *
    * and there is a convenience method on the Encoder object : def instance[A](f: A => Json): Encoder[A]
    *
    * So all we need to create an encoder is a method with type Agent => Json
    * Oh hang on ... that is what we have in the "encodeAgent" method above!
    *
    *
    * val encoder: Encoder[Agent] = f: Agent => Json
    *
    * Try again, creating an Encoder instance for Agent instead
    */

  def writePropertyWithEncoder(property: Property): String = {
    def encodeAgent(agent: Agent): Json = ???

    /**
      * Note: since scala 2.11, a Single Abstract Method trait instance can be automatically created from a function
      * that matches the abstract method's signature.  So the below line can be further simplified to:
      * implicit def AgentEncoder: Encoder[Agent] = encodeAgent
      */
    implicit def AgentEncoder: Encoder[Agent] = ???

    Json.obj(
      "description" -> property.description.asJson,
      "agent" -> ???
    ).noSpaces
  }


}
