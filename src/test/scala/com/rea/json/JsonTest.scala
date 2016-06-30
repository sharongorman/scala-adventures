package com.rea.json

import org.specs2.matcher._
import org.specs2.mutable.Specification

class JsonTest extends Specification with JsonMatchers with JsonFieldMatchers  {
  "SuggestionRequestEncoder" should {
    val myJson =
      """{
        | "property" : {
        |   "name": "The House",
        |   "number": "Some Place"
        |   },
        |
        | "size": 0
        |
        |}
      """.stripMargin
    "checks field exists" in {
      myJson must haveField("property")
    }
    "check fields" in {
      myJson must haveFields("property", "size")
    }
    "checks child exists in parent" in {
      myJson must /("property").withField("name")
    }
    "checks children exists in parent" in {
      myJson must /("property").withFields("name", "number")
    }
    "check exactly fields" in {
      myJson must /("property").withExactlyFields("name", "number")
    }
  }
}
