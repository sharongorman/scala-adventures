import io.circe.Json
import io.circe.syntax._


val propertyJson = Json.obj(
  "description" -> "a great house".asJson,
  "agent" -> Json.obj(
    "agentName" -> "Wonder Agent".asJson,
    "agentId" -> "XA1XXX".asJson
  )
)

//Navigate to agent
val agentCursor = (propertyJson.hcursor downField  "agent").focus

