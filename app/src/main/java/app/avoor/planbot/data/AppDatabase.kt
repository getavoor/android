package app.avoor.planbot.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.avoor.planbot.api.models.PlancoinReward
import app.avoor.planbot.api.models.PlancoinTransaction
import app.avoor.planbot.api.models.Streak
import app.avoor.planbot.api.models.StreakUpdate
import app.avoor.planbot.data.dao.PlancoinDao
import app.avoor.planbot.data.dao.PlancoinRewardDao
import app.avoor.planbot.data.dao.StreakDao
import app.avoor.planbot.data.dao.StreakUpdateDao

@Database(
    entities = [
        StreakUpdate::class,
        Streak::class,
        PlancoinTransaction::class,
        PlancoinReward::class
    ], version = 5
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun streakUpdateDao(): StreakUpdateDao
    abstract fun streakDao(): StreakDao
    abstract fun plancoinDao(): PlancoinDao
    abstract fun plancoinRewardDao(): PlancoinRewardDao
}