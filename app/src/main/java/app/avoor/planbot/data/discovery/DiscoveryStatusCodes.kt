package app.avoor.planbot.data.discovery

/**
 * Error codes for sending as responses to invalid data sent over socket.io.
 *
 * Loosely match HTTP error codes.
 */
class DiscoveryStatusCodes {
    companion object {
        val UNAUTHORIZED = 403
        val INVALID_DATA = 400
        val UNKNOWN_COMMAND = 404
        val OK = 200
    }
}