package app.avoor.planbot.api.responses

/**
 * A response received when signing up using [PlanbotApiRepository.signup].
 */
data class SignupEndpointResponse (
    /**
     * A message returned from the server.
     */
    val msg: String,
    /**
     * The user's access token. Null if the sign up operation failed.
     */
    val accessToken: String?,
    /**
     * The user's refresh token. Null if the sign up operation failed.
     */
    val refreshToken: String?
)

fun SignupEndpointResponse.toMessageResponse(): MessageResponse {
    return MessageResponse(this.msg)
}