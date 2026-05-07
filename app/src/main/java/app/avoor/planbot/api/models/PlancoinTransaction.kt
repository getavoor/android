package app.avoor.planbot.api.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

/**
 * A plancoin transaction.
 */
@Entity("plancoins")
data class PlancoinTransaction(
    /**
     * The ID of this update.
     */
    @PrimaryKey val update_id: String,
    /**
     * The amount of plancoins.
     */
    val amount: Int,
    /**
     * The reason for this transaction.
     */
    val reason: String?,
    /**
     * When was this transaction created?
     */
    @ColumnInfo(name = "created_at") val createdAt: Instant?,

    val synced: Boolean = true
)
