package app.avoor.planbot.api.responses

import app.avoor.planbot.api.models.UserWithoutTokens

/**
 * A response received when verifying an email or uploading a profile picture.
 */
data class VerifyEndpointResponse (
    /**
     * A message returned from the server.
     */
    val msg: String,
    /**
     * The user object. Null if the operation failed.
     *
     * Note that this user doesn't contain access or refresh tokens.
     */
    val user: UserWithoutTokens?
)

fun VerifyEndpointResponse.toMessageResponse(): MessageResponse {
    return MessageResponse(this.msg)
}

