package com.rea.error

import cats.Apply
import cats.implicits._

object ErrorExercises {

  type AgentId = Int
  type PropertyId = Int

  /**
    * Lets define a class to hold our errors
    */
  case class AppError(message: String, throwable: Option[Throwable] = None)

  /**
    * And lets define a parameterised type "ErrorOr".
    * We are going to use this to represent any return type, that could be an error.
    *
    * By convention, errors are on the left, and our desired values are on the right.
    *
    * We can create ErrorOrs with the Left(errorOnTheLeft) and Right(valueOnTheRight) constructors.
   **/
  type ErrorOr[A] = Either[AppError, A]


  /** Now lets define our model:
    *  - A property has an id and an agent id.
    *  - An agent has an agent id.
    */
  case class Property(id: PropertyId, description: String, agentId: AgentId)
  case class Agent(id: AgentId, name: String)


  /**
    * Exercise 1: 
    * 
    * Implement findAgent.  
    *
    * It should find the agentId in agents, and return it wrapped in an ErrorOr.
    * If the agentId is not there, it should return an AppError, wrapped in an ErrorOr.
    * For simplicity, our "agents repository" is simply a map of AgentId -> Agent.
    *
    * HINT: Investigate the Map.get method.
    */

  val agents = Map(
    1 -> Agent(1 , "Hocking Stuart"), 
    2 -> Agent(2, "Ellis Jones")
  )

  def findAgent(agentId: AgentId): ErrorOr[Agent] = agents.get(agentId) match {
    case None => Left(AppError(s"agent $agentId not found"))
    case Some(agent) => Right(agent)
  }

  /**
    * Exercise 2:
    *
    * Lets look at what happens if we want to use the result of this lookup.
    * For now lets pop it in a string s"The agent is ${agent.name}"
    *
    * HINT: ErrorOr[A] has a map method, that lets us manipulate the results on the right: 
    *  def map[B](g: A => B): ErrorOr[B]
    */

  def findAgentAnswer(agentId: AgentId) : ErrorOr[String] =
    findAgent(agentId).map(agent => s"The agent is ${agent.name}")

  /**
    * Exercise 3:
    *
    * Lets look at what happens if we want to use the result of one lookup in another lookup
    * So lets look up the property by id, and then use the resulting property's agent id to find the agent.
    */


  /**
    * To do this we will need to implement findProperty (just the same as findAgent above).
    */

  val properties = Map(
    12 -> Property(12, "A great house", 1),
    15 -> Property(15, "A wonderful pad", 2),
    17 -> Property(17, "A dream house from a non existant agent", 0)
  )

  def findProperty(propertyId: PropertyId) : ErrorOr[Property] = properties.get(propertyId) match {
    case None => Left(AppError(s"property $propertyId not found"))
    case Some(property) => Right(property)
  }

  /**
    * Then we can find the agent from the property id.
    *
    * HINT: Because ErrorOr is a monad, it has a flatMap method:
    * def flatMap[B](g: A => ErrorOr[B]): ErrorOr[B]
    */
  def findPropertyAgent(propertyId: PropertyId) : ErrorOr[Agent] = 
    findProperty(propertyId).flatMap(p => findAgent(p.agentId))

  /**
    * Exercise 4:
    *
    * Lets look up a list of agent ids.
    * We want to get either a Vector[ErrorOr[String]]
    */
  def findAgents(agentIds: Vector[AgentId]): Vector[ErrorOr[Agent]] =
    agentIds.map(findAgent)
    // Same thing as agentIds.map(id => findAgent(id))

  /**
    * Exercise 5:
    *
    * Having obtained them, it would be tiresome to handle each possibly 
    * failed agent individually. Either succeed with the full Vector[Agent] 
    * on the right, or fail with an AppError on the left. 
    *
    * HINT: investigate the "sequence" method. This lets us apply the outer action
    * across the whole structure. The signature is something like:
    *
    * def sequence[F[_]]: F[Vector[A]]
    */
  def sequenceAgents(agentIds: Vector[ErrorOr[Agent]]): ErrorOr[Vector[Agent]] =
    agentIds.sequence[ErrorOr, Agent]

  /**
    * Exercise 6:
    *
    * Lets look up a list of agent ids.
    * This time we either want a Vector of agents if they are all successful, or the
    * first error message.
    *
    * HINT: investigate the "traverse" method. This lets us perform an action on each element of the vector, 
    * distributing the action over the whole traversable structure.
    *
    * Where F an action for which Applicative[F] is defined, the signature is something like:
    * def traverse[F[_]](f: A => F[B]): F[Vector[B]]
    */

  def findAllAgents(agentIds: Vector[AgentId]): ErrorOr[Vector[Agent]] =
    agentIds.traverse[ErrorOr, Agent](findAgent)

  /** Exercise 7:
    *
    * Let check if a list of agent ids exist.
    * This time we either want an error if one doesn't exist or a unit (i.e they exist)
    *
    * HINT: There is a version of traverse called "traverseU_" that returns F[Unit]
    * so we don't have to wastefully collect all the answers. The underscore is a Haskell/scalaz/cats naming convention,
    * indicating that the results are thrown away.
    */

  def checkAllAgents(agentIds: Vector[AgentId]): ErrorOr[Unit] = 
    agentIds.traverse_[ErrorOr, Agent](findAgent)

  /** Exercise 8:
    *
    * Let look up a list of agent ids.
    * This time we either want an error if one doesn't exist or a list of the agents' names.
    */
  def findAllAgentsNames(agentIds: Vector[AgentId]): ErrorOr[Vector[String]] =
    agentIds.traverse[ErrorOr, String](agentId => findAgent(agentId).map(_.name))

  /**
    * Exercise 9:
    *
    * Lets lookup a list of agents again.
    * This time the result should be a Vector of all the errors, and a list of all the successfully found values.
    *
    * HINT: Consider cats method ".separate", which among other things, can squeeze an F[Xor[A,B]] into an (F[A], F[B]).
    */

  def findSomeAgents(agentIds: Vector[AgentId]): (Vector[AppError], Vector[Agent]) = 
    findAgents(agentIds).separate

  /**
    * Exercise 10:
    *
    * Lets lookup an agency and a property, and create the string "Hey ${agent.name} how about selling ${property.description}"
    * We should get the first error if they fail.
    *
    * HINT: use a for comprehension.
    */
  def suggestAProperty(propertyId: PropertyId, agentId: AgentId): ErrorOr[String] =
    for {
      property <- findProperty(propertyId)
      agent <- findAgent(agentId)
    } yield s"Hey ${agent.name} how about selling ${property.description}"

  /**
    * Exercise 11:
    *
    * Applicative is a concept that allows us to lift a function of any arity to operating on arguments inside some F.
    *            A --->  F[A]
    *       A => B --->  F[A] => F[B]
    *   (A,B) => C ---> (F[A], F[B]) => F[C]
    * (A,B,C) => D ---> (F[A], F[B], F[C]) => F[D]
    *              ...
    *
    * If arguments (ie A, B, C, etc) don't depend on each other, then Applicatives are a weaker and more general 
    * alternative to monads.
    *
    * We can use it here by instantiating Apply[F], and calling applyN for the number of arguments we have.
    *
    * ie
    * def makeSandwich(b: Bacon, l: Lettuce, t: Tomato): Sandwich = ...
    *
    * val optionalSandwich: Option[Sandwich] = 
    *   Apply[Option].apply3(optionalBacon, optionalLettuce, optionalTomato)(makeSandwich)
    *
    * Try rewriting the suggestAProperty method above using an Apply[ErrorOr].applyXXX variant.
    */

  def suggestAProperty2(propertyId: Int, agentId: Int): ErrorOr[String] = {
    def suggestString(agent: Agent, property: Property) = s"Hey ${agent.name} how about selling ${property.description}"

    Apply[ErrorOr].map2(findAgent(agentId), findProperty(propertyId))(suggestString)
  }

}
