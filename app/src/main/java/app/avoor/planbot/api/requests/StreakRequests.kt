package app.avoor.planbot.api.requests

import com.google.gson.annotations.SerializedName

/**
 * Request body for sending a streak update to the server.
 */
data class StreakUpdateBody(
    @SerializedName("update_id") val updateId: String,
    @SerializedName("date") val date: String // ISO 8601 format
)

/**
 * Request body for using a streak freeze.
 */
data class UseStreakFreezeBody(
    @SerializedName("date") val date: String // ISO 8601 format
)
