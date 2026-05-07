package app.avoor.planbot.api.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

/**
 * Represents the user's streak state.
 * There should only be one row in this table (singleton).
 */
@Entity
data class Streak(
    /**
     * Always 1 - this is a singleton table.
     */
    @PrimaryKey val id: Int = 1,
    /**
     * The current streak count (number of consecutive days).
     */
    @ColumnInfo(name = "current_streak") val currentStreak: Int = 0,
    /**
     * The number of streak freezes available.
     * A streak freeze prevents the streak from resetting for one day.
     */
    @ColumnInfo(name = "freeze_count") val freezeCount: Int = 0,
    /**
     * The date of the last successful streak update.
     */
    @ColumnInfo(name = "last_update_date") val lastUpdateDate: Instant? = null,
    /**
     * Whether a streak freeze was used today.
     */
    @ColumnInfo(name = "freeze_used_today") val freezeUsedToday: Boolean = false,
    /**
     * The longest streak ever achieved.
     */
    @ColumnInfo(name = "longest_streak") val longestStreak: Int = 0
)
