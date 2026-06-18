package app.avoor.planbot.domain

import app.avoor.planbot.api.models.Streak
import app.avoor.planbot.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

fun AppDatabase.getStreakFlow(): Flow<Streak?> {
    return this.streakDao().getFlow()
}
suspend fun AppDatabase.getStreak(): Streak? = withContext(Dispatchers.IO) {
    this@getStreak.streakDao().get()
}