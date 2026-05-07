package app.avoor.planbot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.avoor.planbot.api.models.PlancoinTransaction
import app.avoor.planbot.api.models.StreakUpdate
import kotlinx.coroutines.flow.Flow

@Dao
interface PlancoinDao {
    @Query("SELECT * FROM plancoins")
    fun getAll(): List<PlancoinTransaction>

    @Query("SELECT * FROM plancoins ORDER BY created_at DESC LIMIT 1")
    fun getLatest(): PlancoinTransaction?

    @Query("SELECT * FROM plancoins ORDER BY created_at DESC LIMIT 1")
    fun getLatestFlow(): Flow<PlancoinTransaction?>

    @Query("SELECT * FROM plancoins WHERE update_id IS :id")
    fun getById(id: String): PlancoinTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trxn: PlancoinTransaction)

    /**
     * Get all unsynced plancoin updates, ordered by date (oldest first).
     */
    @Query("SELECT * FROM plancoins WHERE synced = 0 ORDER BY created_at ASC")
    fun getUnsynced(): List<PlancoinTransaction>

    /**
     * Mark an update as synced.
     */
    @Query("UPDATE plancoins SET synced = 1 WHERE update_id = :updateId")
    fun markSynced(updateId: String)

    /**
     * Get the sum of all unsynced plancoin transaction amounts.
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM plancoins WHERE synced = 0")
    fun getUnsyncedSum(): Int

    /**
     * Get the sum of all unsynced plancoin transaction amounts as a Flow.
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM plancoins WHERE synced = 0")
    fun getUnsyncedSumFlow(): Flow<Int>
}