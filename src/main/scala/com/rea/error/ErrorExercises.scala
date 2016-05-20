package com.rea.error
import scalaz._, Scalaz._

object ErrorExercises {

  /**
    * Lets define a class to hold our errors
    */
  case class AppError(message: String, throwable: Option[Throwable] = None)

  /**
    * And lets define a paramaterized type.
    * This will be the return type of any errors
   **/
  type ErrorOr[A] = AppError \/ A


  /** Now lets define our model
    *  A property has an id and an agent id
    *  An agent has an agent id
    */
  case class Property(id: Int, description: String, agentId: Int)
  case class Agent(id: Int, name: String)


  /**
    * Exercise 1 : implement findAgent.  It should find the agentId
    * in agents, and return it wrapped in an ErrorOr.
    * If the agentId is not there, it should return an AppError, wrapped in an ErrorOr
    */

  val agents = Map(1 -> Agent(1 , "Hocking Stuart"), 2 -> Agent(2, "Ellis Jones"))

  def findAgent(agentId: Int): ErrorOr[Agent] = agents.get(agentId) match {
    case None => -\/(AppError(s"agent $agentId not found"))
    case Some(agent) => \/-(agent)
  }

  /**
    * Exercise 2 :
    * Lets look at what happens if we want to use the result of this lookup.
    * For now lets pop it in a string s"The agent is ${agent.name}"
    */

  def findAgentAnswer(agentId: Int) : ErrorOr[String] = findAgent(agentId).map(agent => s"The agent is ${agent.name}")
  /**
    * Exercise 3 :
    * Lets look at what happens if we want to use the result of one lookup in another lookup
    * So lets look up the property by id, and then use the resulting propertie's agent id to find the agent.
    */


  /**
    * To do this we will need to implement findProperty (just the same as findAgent above).
    */

  val properties = Map(
    12 -> Property(12, "A great house", 1),
    15 -> Property(15, "A wonderful pad", 2),
    17 -> Property(17, "A dream house from a non existant agent", 0)
  )

  def findProperty(propertyId: Int) : ErrorOr[Property] = properties.get(propertyId) match {
    case None => -\/(AppError(s"property $propertyId not found"))
    case Some(property) => \/-(property)
  }

  /**
    * Then we can find the agent from the property id.
    */
  def findPropertyAgent(propertyId: Int) : ErrorOr[Agent] = findProperty(propertyId).flatMap{ property =>
    findAgent(property.agentId)
  }




  /**
    * Exercise 4:
    * Lets look up a list of agent ids.
    * We want to get either a Vector[ErrorOr[String]]
    */
  def findAgents(agentIds: Vector[Int]): Vector[ErrorOr[Agent]] =
    agentIds.map(agentId => findAgent(agentId))
  // With syntactic sugar, this could also be written:
      //agentIds.map(findAgent(_))
  // or even:
      //agentIds.map(findAgent)


  /**
    * Exercise 5:
    * Lets look up a list of agent ids.
    * This time we either want a Vector of agents if they are all successful, or the
    * first error message.
    * Hint: investigate the "sequence" method.  This basically unwrapps and re-wraps a traversable (e.g. Vector) and an applicative (our ErrorOr).
    */

  def findAllAgents(agentIds: Vector[Int]): ErrorOr[Vector[Agent]] =
     findAgents(agentIds).sequence[ErrorOr, Agent]

  /** Exercise 6:
    * Let check if a list of agent ids exist
    * This time we either want an error if one doesn't exist or a unit (i.e they exist)
    */

  def checkAllAgents(agentIds: Vector[Int]): ErrorOr[Unit] = findAgents(agentIds).sequence_[ErrorOr, Agent]

  /** Exercise 7:
    * Let look up a list of agent ids.
    * This time we either want an error if one doesn't exist or a list of the agents names
    * Hint: take a look at the "traverse" method, it is like doing a sequence, but for each element, it first applies a function that transfroms the element.
    */

  def findAllAgentsNames(agentIds: Vector[Int]): ErrorOr[Vector[String]] = {
    findAgents(agentIds).traverse[ErrorOr, String](maybeAgent => maybeAgent.map(_.name))
  }

  /**
    * Exercise 8:
    * Lets lookup a list of agents again.
    * This time the result should be a Vector of all the errors, and a list of all the successfully found values.
    */

  def findSomeAgents(agentIds: Vector[Int]): (Vector[AppError], Vector[Agent]) = findAgents(agentIds).separate

  /**
    * Exercise 9:
    * Lets lookup an agency and a property, and create the string "Hey ${agent.name} how about selling ${property.description}"
    * We should get the first error if they fail.
    * Hint, use a for comprehension
    */
  def suggestAProperty(propertyId: Int, agentId: Int): ErrorOr[String] = {
    for {
      property <- findProperty(propertyId)
      agent <- findAgent(agentId)
    } yield s"Hey ${agent.name} how about selling ${property.description}"
  }

  /**
    * Exercise 10:
    * Try the above using apply2 method: Apply[F[_]].apply2[A, B, C](fa: => F[A], fb: => F[B])(f: (A, B) => C): F[C]
    */

  def suggesAProperty2(propertyId: Int, agentId: Int): ErrorOr[String] = {
    def suggestString(agent: Agent, property: Property) = s"Hey ${agent.name} how about selling ${property.description}"

    Apply[ErrorOr].apply2(findAgent(agentId), findProperty(propertyId))(suggestString)
  }


}
