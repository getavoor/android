package app.avoor.planbot.api.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A reward one gets by paying plancoins.
 */
@Entity
data class PlancoinReward(
    /**
     * The ID of this reward.
     */
    @PrimaryKey val id: String,
    /**
     * The title of this reward.
     */
    val title: String,
    /**
     * The cost (in plancoins) of this reward.
     */
    val cost: Int,
    /**
     * How many of this reward are available, or -1 for unlimited.
     */
    val stock: Int
)
