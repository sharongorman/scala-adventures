import argonaut._, Argonaut._



val propertyJson = Json(
  "description" := "a great house",
  "agent" := Json(
    "agentName" := "Wonder Agent",
    "agentId" := "XA1XXX"
  )
)

//Navigate to agent
val agentCursor = propertyJson.hcursor --\ "agent"

