package app.avoor.planbot.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// database v4 adds plancoin rewards.
// it requires no changes to schemas defined in v3.

val MIGRATION_FROM_3_TO_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE `PlancoinReward` (" +
            "`id` TEXT NOT NULL, `title` TEXT NOT NULL, `cost` INTEGER NOT NULL," +
            "PRIMARY KEY(`id`))"
        )
    }
}