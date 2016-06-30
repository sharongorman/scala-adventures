package com.rea.json




import org.specs2.matcher._
import MatchersImplicits._


trait JsonFieldMatchers extends JsonMatchers {
  def haveField(fieldName: String): Matcher[String] = /(matchByName(fieldName))


  def haveFields(fields: String*): JsonMatcher = have(withFieldsMatcher(fields: _*))


  implicit class JsonSelectorMatcherOps2(s: JsonSelectorMatcher) {
    def withFields(fields: String*): JsonFinalMatcher = {
      s.andHave(withFieldsMatcher(fields: _*))
    }

    def withExactlyFields(fields: String*): JsonFinalMatcher = {
      s.andHave(withExactlyFieldsMatcher(fields: _*))
    }

    def withField(field: String): JsonSelectorMatcher = {
      s / (matchByName(field))
    }
  }

  private def matchByName(field: String): JsonSelector = field -> AlwaysMatcher[String]()

  private def withFieldsMatcher(fields: String*): Matcher[JsonType] = {
    def withField(field: String): Matcher[JsonType] = //(field).andHave(always)
      (actual: JsonType) => actual match {
      case JsonMap(map) => (map.contains(field), s"$map does not contain $field")
      case other => (false, s"not a json map: $other")
    }
    val always: Matcher[JsonType] = AlwaysMatcher()
    fields.foldLeft(always)((matcher: Matcher[JsonType], value: String) => matcher and withField(value))
  }




  def someMatcher: Matcher[String] = {(_:String) =>(true, "asdf")}

  private def withExactlyFieldsMatcher(fields: String*): Matcher[JsonType] = {
    val exactFieldCount: Matcher[JsonType] = new SizedMatcher[JsonType](fields.length, "field count")
    withFieldsMatcher(fields: _*) and exactFieldCount
  }

}


