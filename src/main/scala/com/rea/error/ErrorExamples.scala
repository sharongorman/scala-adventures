package com.rea.error
import scalaz._, Scalaz._

object ErrorExamples {

  /**
    * Lets define a class to hold our errors
    */
  case class AppError(message: String, throwable: Option[Throwable] = None)

  /**
    * And lets define a paramaterized type.
    * This will be the return type of any errors
   **/
  type ErrorOr[A] = AppError \/ A


  /**
    * Exercise 1 : implement myLookup.  It should find the key
    * in myMap, and return it wrapped in an ErrorOr.
    * If the key is not there, it should return an AppError, wrapped in an ErrorOr
    */
  val myMap = Map(0 -> "zero", 1 -> "one")

  def myLookup(key: Int): ErrorOr[String] = ???
}
