package app.avoor.planbot.data.discovery

import org.json.JSONObject

/**
 * A group discovery session.
 */
data class GroupDiscoverySession(
    /**
     * The group ID.
     */
    val id: String,
    /**
     * The members of this group.
     *
     * Members participate in discovery (i.e. the server takes their taste preferences
     * into account).
     */
    val members: List<DiscoveryUser>,
    /**
     * The guests of this group.
     *
     * Guests don't participate in discovery and need to be manually approved.
     */
    val guests: List<DiscoveryUser>
)

fun JSONObject.parseDiscoveryGroup(): GroupDiscoverySession {
    return GroupDiscoverySession(
        this["id"] as String,
        this["members"] as List<DiscoveryUser>,
        this["guests"] as List<DiscoveryUser>
    )
}