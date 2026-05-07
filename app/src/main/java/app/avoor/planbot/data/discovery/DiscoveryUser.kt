package app.avoor.planbot.data.discovery

import org.json.JSONObject

/**
 * A user returned by the discovery session.
 *
 * Only used in group related commands to protect users' privacy;
 * other parts of the API use [UserApi] instead.
 */
data class DiscoveryUser(
    /**
     * The user's ID.
     */
    val id: Int,
    /**
     * The user's name.
     */
    val name: String,
    /**
     * The URL of the user's profile picture, if any.
     */
    val photoUrl: String?
)

fun JSONObject.parseDiscoveryUser(): DiscoveryUser {
    return DiscoveryUser(
        this["id"] as Int,
        this["name"] as String,
        // pass the photo url as null if it wasn't sent or was sent as null
        if (this.has("photoUrl") && !this.isNull("photoUrl"))
            this["photoUrl"] as String
        else null
    )
}