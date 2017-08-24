
import io.circe._
import sun.util.resources.cldr.mas.CalendarData_mas_KE
"hello"

val myJsonString =
  """
    |{"thing1":1, "thing2":{"inside": "value"}}
  """.stripMargin

val myJson = parser.parse(myJsonString)

def stringValue(cursor: HCursor): Decoder.Result[String] = Right(cursor.value.noSpaces)

case class MyThing(thing1: Int, thing2: String)

implicit val myThingDecoder: Decoder[MyThing] = Decoder {cursor => for {
  thing1 <- cursor.get[Int]("thing1")
  thing2 <- cursor.get[String]("thing2")(c => Right(c.value.noSpaces))
} yield MyThing(thing1, thing2)

}

val myCustomDecoder = Decoder.decodeJson.map(_.noSpaces)
parser.decode(myJsonString)

val success:Decoder.Result[String] = Right("my string")
val failure:Decoder.Result[String] = Left(DecodingFailure("somehthing went wrong", Nil))


Json.fromString("My first json").as[String]

def decodeSandwich(cursor: ACursor): Decoder.Result[Sandwich] = for {
  bread <- cursor.get[String]("bread")
  filling <- cursor.get[String]("filling")
} yield Sandwich(bread, filling)