package app.avoor.planbot.api.responses

/**
 * A basic server response with a message.
 */
data class MessageResponse(
    /**
     * A message returned from the server.
     */
    val msg: String
)