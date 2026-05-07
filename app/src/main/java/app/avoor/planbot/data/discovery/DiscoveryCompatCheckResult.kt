package app.avoor.planbot.data.discovery

/**
 * The result of a compatibility check.
 */
data class DiscoveryCompatCheckResult(
    /**
     * Is this client compatible with the server?
     */
    val compatible: Boolean,
    /**
     * The server's protocol version.
     */
    val protoVer: Int
)