package tech.cataspect.socketx

/**
 * Thrown when a socket has timed out waiting for an acknowledgement.
 */
class AckTimeoutException(
    message: String? = null
): Exception()