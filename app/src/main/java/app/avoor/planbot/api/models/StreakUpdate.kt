package app.avoor.planbot.api.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

/**
 * An update to a streak - represents a single day's check-in.
 * Stored locally and synced to the server.
 */
@Entity
data class StreakUpdate(
    /**
     * The ID of this update.
     */
    @PrimaryKey val update_id: String,
    /**
     * The date the update was done on.
     */
    val date: Instant,
    /**
     * The current streak at the time of the update.
     */
    @ColumnInfo(name = "current_streak") val currentStreak: Int,
    /**
     * Whether this update has been synced to the server.
     */
    @ColumnInfo(name = "synced", defaultValue = "0") val synced: Boolean = false
)
