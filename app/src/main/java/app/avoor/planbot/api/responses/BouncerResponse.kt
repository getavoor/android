package app.avoor.planbot.api.responses

/**
 * A response from the bouncer service.
 */
data class BouncerResponse(
    /**
     * Is this version allowed?
     */
    val allowed: Boolean
)