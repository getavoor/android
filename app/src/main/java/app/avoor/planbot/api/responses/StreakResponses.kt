package app.avoor.planbot.api.responses

import com.google.gson.annotations.SerializedName

/**
 * Response from the streak endpoint.
 */
data class StreakResponse(
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("freeze_count") val freezeCount: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("last_update_date") val lastUpdateDate: String?,
    @SerializedName("freeze_used_today") val freezeUsedToday: Boolean
)

/**
 * Response from posting a streak update.
 */
data class StreakUpdateResponse(
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("freeze_count") val freezeCount: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("streak_extended") val streakExtended: Boolean
)

/**
 * Response from using a streak freeze.
 */
data class StreakFreezeResponse(
    @SerializedName("freeze_count") val freezeCount: Int,
    @SerializedName("freeze_used") val freezeUsed: Boolean,
    @SerializedName("current_streak") val currentStreak: Int
)
