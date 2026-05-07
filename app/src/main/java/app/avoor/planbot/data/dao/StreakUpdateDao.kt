package app.avoor.planbot.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.avoor.planbot.api.models.StreakUpdate
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakUpdateDao {
    @Query("SELECT * FROM streakupdate")
    fun getAll(): List<StreakUpdate>

    @Query("SELECT * FROM streakupdate ORDER BY date DESC LIMIT 1")
    fun getLatest(): StreakUpdate?

    @Query("SELECT * FROM streakupdate ORDER BY date DESC LIMIT 1")
    fun getLatestFlow(): Flow<StreakUpdate?>

    @Query("SELECT * FROM streakupdate WHERE update_id IS :id")
    fun getById(id: String): StreakUpdate?

    /**
     * Get all unsynced streak updates, ordered by date (oldest first).
     */
    @Query("SELECT * FROM streakupdate WHERE synced = 0 ORDER BY date ASC")
    fun getUnsynced(): List<StreakUpdate>

    /**
     * Check if there's an update for today (within the given time range).
     * @param startOfDay start of today in epoch milliseconds
     * @param endOfDay end of today in epoch milliseconds
     */
    @Query("SELECT * FROM streakupdate WHERE date >= :startOfDay AND date < :endOfDay LIMIT 1")
    fun getUpdateForDay(startOfDay: Long, endOfDay: Long): StreakUpdate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(update: StreakUpdate)

    @Insert
    fun insertAll(vararg updates: StreakUpdate)

    @Update
    fun update(update: StreakUpdate)

    /**
     * Mark an update as synced.
     */
    @Query("UPDATE streakupdate SET synced = 1 WHERE update_id = :updateId")
    fun markSynced(updateId: String)

    @Delete
    fun delete(update: StreakUpdate)

    @Query("DELETE FROM streakupdate")
    fun deleteAll()
}