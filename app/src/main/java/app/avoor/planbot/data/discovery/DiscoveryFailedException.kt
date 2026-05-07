package app.avoor.planbot.data.discovery

/**
 * Raised when a discovery operation has failed.
 */
class DiscoveryFailedException(
    val statusCode: Int
): Exception()