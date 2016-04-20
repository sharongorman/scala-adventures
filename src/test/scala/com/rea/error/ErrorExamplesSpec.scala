package com.rea.error

import org.specs2.mutable.Specification
import ErrorExamples._
import scalaz._, Scalaz._
import org.specs2.matcher.DisjunctionMatchers._

class ErrorExamplesSpec extends Specification {

  "ErrorExamplesSpec" should {
    "myLookup" should {
      "return an error" in {
        myLookup(3) must be_-\/
      }

      "return success" in {
        myLookup(1) must be_\/-("one")
      }
    }

  }
}
