package app.avoor.planbot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.avoor.planbot.api.models.Streak
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    /**
     * Get the current streak state.
     * There should only be one row (id=1).
     */
    @Query("SELECT * FROM streak WHERE id = 1")
    fun get(): Streak?

    /**
     * Get the current streak state as a Flow for real-time updates.
     */
    @Query("SELECT * FROM streak WHERE id = 1")
    fun getFlow(): Flow<Streak?>

    /**
     * Insert or replace the streak state.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(streak: Streak)

    /**
     * Update the streak state.
     */
    @Update
    fun update(streak: Streak)

    /**
     * Increment the current streak by 1.
     */
    @Query("UPDATE streak SET current_streak = current_streak + 1, longest_streak = MAX(longest_streak, current_streak + 1) WHERE id = 1")
    fun incrementStreak()

    /**
     * Reset the streak to 0.
     */
    @Query("UPDATE streak SET current_streak = 0 WHERE id = 1")
    fun resetStreak()

    /**
     * Use a streak freeze (decrement freeze count, mark freeze used today).
     */
    @Query("UPDATE streak SET freeze_count = freeze_count - 1, freeze_used_today = 1 WHERE id = 1 AND freeze_count > 0")
    fun useFreeze(): Int

    /**
     * Add a streak freeze.
     */
    @Query("UPDATE streak SET freeze_count = freeze_count + :count WHERE id = 1")
    fun addFreezes(count: Int)

    /**
     * Reset the freeze_used_today flag (called at start of new day).
     */
    @Query("UPDATE streak SET freeze_used_today = 0 WHERE id = 1")
    fun resetFreezeUsedToday()

    /**
     * Delete all streak data.
     */
    @Query("DELETE FROM streak")
    fun deleteAll()
}
