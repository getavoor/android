package app.avoor.planbot.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// database v5 adds limited stock to plancoin rewards.

val MIGRATION_FROM_4_TO_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE `PlancoinReward`" +
                "ADD `stock` INTEGER NOT NULL"
        )
    }
}