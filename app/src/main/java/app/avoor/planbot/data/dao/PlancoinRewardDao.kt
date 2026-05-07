package app.avoor.planbot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.avoor.planbot.api.models.PlancoinReward

@Dao
interface PlancoinRewardDao {
    @Query("SELECT * FROM plancoinreward")
    fun getAll(): List<PlancoinReward>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reward: PlancoinReward)

    @Query("SELECT * FROM plancoinreward WHERE id IS :id")
    fun getById(id: String): PlancoinReward?
}