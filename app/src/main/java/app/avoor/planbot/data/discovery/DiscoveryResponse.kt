package app.avoor.planbot.data.discovery

/**
 * Commands to use during discovery.
 *
 * Mostly used when receiving responses from the server, as repositories already provide
 * functions for sending requests with the appropriate command.
 */
enum class DiscoveryCommand(
    /**
     * The API ID of this command.
     */
    val id: String
) {

    /**
     * A user has joined the group.
     */
    JOIN("join"),
    /**
     * A user has been approved and now participates in discovery.
     */
    APPROVE("approve"),
    /**
     * A user has left the group.
     */
    LEAVE("leave"),

    /**
     * The group is being destroyed.
     *
     * Only sent by the server.
     */
    DESTROY("destroy"),

    /**
     * Unknown command.
     *
     * Used when the command can't be parsed.
     */
    UNKNOWN("_unk")
}

/**
 * Response received from the server during a discovery session.
 */
class DiscoveryResponse (
    /**
     * The command.
     */
    val command: DiscoveryCommand,
    /**
     * The error, if any.
     */
    val error: String? = null,
    /**
     * The user.
     *
     * Only sent with [DiscoveryCommand.JOIN], [DiscoveryCommand.APPROVE] and
     * [DiscoveryCommand.LEAVE].
     */
    val user: DiscoveryUser? = null
)