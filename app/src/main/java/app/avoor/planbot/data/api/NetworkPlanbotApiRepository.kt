package app.avoor.planbot.data.api

import android.net.Uri
import android.util.Log
import app.avoor.planbot.api.PlanbotApiService
import app.avoor.planbot.api.models.FeedbackReason
import app.avoor.planbot.api.models.PlancoinTransaction
import app.avoor.planbot.api.models.Streak
import app.avoor.planbot.api.models.StreakUpdate
import app.avoor.planbot.api.models.Task
import app.avoor.planbot.api.models.User
import app.avoor.planbot.api.models.UserApi
import app.avoor.planbot.api.models.UserWithoutTokens
import app.avoor.planbot.api.models.toUser
import app.avoor.planbot.api.requests.AddPlancoinsBody
import app.avoor.planbot.api.requests.CreateTaskBody
import app.avoor.planbot.api.requests.EmailPasswordBody
import app.avoor.planbot.api.requests.EmailPasswordNameBody
import app.avoor.planbot.api.requests.FeedbackBody
import app.avoor.planbot.api.requests.StreakUpdateBody
import app.avoor.planbot.api.requests.TokenBody
import app.avoor.planbot.api.requests.UpdateTaskBody
import app.avoor.planbot.api.requests.UseStreakFreezeBody
import app.avoor.planbot.api.responses.SignupEndpointResponse
import app.avoor.planbot.api.responses.VerifyEndpointResponse
import app.avoor.planbot.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Instant

class NetworkPlanbotApiRepository(
    private val planbotApiService: PlanbotApiService,
    private val filesDir: File,
    private val db: AppDatabase
): PlanbotApiRepository {
    // Authentication
    override suspend fun login(email: String, password: String): UserApi
        = planbotApiService.login(EmailPasswordBody(email, password))

    override suspend fun obtainAccessToken(): String
        = planbotApiService.refreshToken().accessToken

    override suspend fun loginUsingAccessToken(): User
        = planbotApiService.retrieveCurrentUser().toUser()

    override suspend fun fetchUser(): UserWithoutTokens
        = planbotApiService.retrieveCurrentUser()

    override suspend fun signup(
        email: String,
        password: String,
        name: String
    ): SignupEndpointResponse
        = planbotApiService.signup(EmailPasswordNameBody(email, password, name))

    override suspend fun verifyEmail(token: String): VerifyEndpointResponse
        = planbotApiService.confirm(TokenBody(token))

    override suspend fun uploadProfilePicture(uri: Uri): VerifyEndpointResponse {
        // Get the file
        val file = File(filesDir, "out.jpg")
        Log.d(TAG, file.path)

        // create RequestBody instance from file
        val requestFile: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        // MultipartBody.Part is used to send the file and its name
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)
        Log.d(TAG, "sending")

        // Upload the picture
        return planbotApiService.uploadPicture(body)
    }

    override suspend fun sendFeedback(reason: FeedbackReason, body: String?) {
        planbotApiService.sendFeedback(FeedbackBody(reason.id, body))
    }

    override suspend fun resendVerifyEmail() {
        planbotApiService.resendVerifyEmail()
    }

    override suspend fun deleteAccount() {
        planbotApiService.deleteMe()
    }

    // Task Management
    override suspend fun getTasks(): List<Task>
        = planbotApiService.getTasks().tasks

    override suspend fun createTask(
        title: String,
        description: String?,
        priority: Int?,
        dueDate: String?
    ): Task = planbotApiService.createTask(
        CreateTaskBody(title, description, priority, dueDate)
    ).task

    override suspend fun updateTask(
        taskId: Int,
        title: String?,
        description: String?,
        priority: Int?,
        isCompleted: Boolean?,
        dueDate: String?
    ) {
        planbotApiService.updateTask(
            taskId,
            UpdateTaskBody(title, description, priority, isCompleted, dueDate)
        )
    }

    override suspend fun deleteTask(taskId: Int) {
        planbotApiService.deleteTask(taskId)
    }

    // Plancoins
    override suspend fun addPlancoins(amount: Int, reason: String?): Int {
        return planbotApiService.addPlancoins(
            AddPlancoinsBody(amount, reason)
        ).plancoins
    }

    override suspend fun getPlancoinHistory(): List<PlancoinTransaction>
        = planbotApiService.getPlancoinHistory().transactions

    // Streak Management

    companion object {
        private const val TAG = "avr#npar"
        private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
    }

    // TODO these functions have nothing to do with the API, maybe move into the domain layer?
    override fun getStreakFlow(): Flow<Streak?> {
        return db.streakDao().getFlow()
    }
    override suspend fun getStreak(): Streak? = withContext(Dispatchers.IO) {
        db.streakDao().get()
    }

    override suspend fun fetchStreakFromServer(): Streak? = withContext(Dispatchers.IO) {
        try {
            val response = planbotApiService.getStreak()
            val lastUpdateDate = response.lastUpdateDate?.let {
                Instant.parse(it)
            }
            val streak = Streak(
                id = 1,
                currentStreak = response.currentStreak,
                freezeCount = response.freezeCount,
                longestStreak = response.longestStreak,
                lastUpdateDate = lastUpdateDate,
                freezeUsedToday = response.freezeUsedToday
            )
            db.streakDao().upsert(streak)
            streak
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch streak from server", e)
            null
        }
    }

    override suspend fun hasUpdatedStreakToday(): Boolean = withContext(Dispatchers.IO) {
        val now = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(tz).date

        // Calculate start and end of today in epoch millis
        val startOfDay = kotlinx.datetime.LocalDateTime(today.year, today.month, today.day, 0, 0, 0)
            .toInstant(tz).toEpochMilliseconds()
        val endOfDay = startOfDay + MILLIS_IN_DAY

        db.streakUpdateDao().getUpdateForDay(startOfDay, endOfDay) != null
    }

    override suspend fun sendStreakUpdate(): Streak = withContext(Dispatchers.IO) {
        // get the start of today
        val tz = TimeZone.currentSystemDefault()
        val now = Clock.System.todayIn(tz).atStartOfDayIn(tz)

        val updateId = UUID.randomUUID().toString()

        // Get current streak or create initial one
        var streak = db.streakDao().get() ?: Streak(id = 1)

        // Check if we should reset streak (more than 1 day since last update)
        streak = checkStreakReset(streak, now)

        // Increment streak
        val newStreakCount = streak.currentStreak + 1
        val newLongestStreak = maxOf(streak.longestStreak, newStreakCount)

        // Create local update record
        val update = StreakUpdate(
            update_id = updateId,
            date = now,
            currentStreak = newStreakCount,
            synced = false
        )
        db.streakUpdateDao().insert(update)

        // Update local streak state
        val updatedStreak = streak.copy(
            currentStreak = newStreakCount,
            longestStreak = newLongestStreak,
            lastUpdateDate = now,
            freezeUsedToday = false
        )
        db.streakDao().upsert(updatedStreak)

        // Try to sync to server
        try {
            val dateStr = now.toString()
            val response = planbotApiService.postStreakUpdate(StreakUpdateBody(updateId, dateStr))
            db.streakUpdateDao().markSynced(updateId)

            // Update with server response
            val serverStreak = updatedStreak.copy(
                currentStreak = response.currentStreak,
                freezeCount = response.freezeCount,
                longestStreak = response.longestStreak
            )
            db.streakDao().upsert(serverStreak)
            serverStreak
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync streak update to server, will retry later", e)
            updatedStreak
        }
    }

    private fun checkStreakReset(streak: Streak, now: Instant): Streak {
        val lastUpdate = streak.lastUpdateDate ?: return streak
        val millisSinceLastUpdate = now.toEpochMilliseconds() - lastUpdate.toEpochMilliseconds()

        // If more than 2 days have passed (skipped one full day), streak should reset
        // Note: We use 2 days because if you update on day 1 and day 3, you skipped day 2
        if (millisSinceLastUpdate > 2 * MILLIS_IN_DAY) {
            // Check if we have a freeze available
            if (streak.freezeCount > 0 && !streak.freezeUsedToday) {
                // Use freeze
                val updatedStreak = streak.copy(
                    freezeCount = streak.freezeCount - 1,
                    freezeUsedToday = true
                )
                db.streakDao().upsert(updatedStreak)
                return updatedStreak
            } else {
                // Reset streak
                val resetStreak = streak.copy(currentStreak = 0)
                db.streakDao().upsert(resetStreak)
                return resetStreak
            }
        }
        return streak
    }

    override suspend fun syncPendingStreakUpdates(): Int = withContext(Dispatchers.IO) {
        val unsyncedUpdates = db.streakUpdateDao().getUnsynced()
        var syncedCount = 0

        for (update in unsyncedUpdates) {
            try {
                val dateStr = update.date.toString()
                planbotApiService.postStreakUpdate(StreakUpdateBody(update.update_id, dateStr))
                db.streakUpdateDao().markSynced(update.update_id)
                syncedCount++
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync update ${update.update_id}", e)
                // Stop on first failure to maintain order
                break
            }
        }
        syncedCount
    }

    override suspend fun syncPendingPlancoinTransactions(): Pair<Int, Int?> = withContext(Dispatchers.IO) {
        val unsyncedUpdates = db.plancoinDao().getUnsynced()
        var syncedCount = 0
        var serverBalance: Int? = null

        for (update in unsyncedUpdates) {
            try {
                val response = planbotApiService.addPlancoins(
                    AddPlancoinsBody(update.amount, update.reason)
                )
                db.plancoinDao().markSynced(update.update_id)
                syncedCount++
                // Keep track of the latest server balance
                serverBalance = response.plancoins
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync plancoin transaction ${update.update_id}", e)
                // Stop on first failure to maintain order
                break
            }
        }
        Pair(syncedCount, serverBalance)
    }

    override suspend fun useStreakFreeze(): Boolean = withContext(Dispatchers.IO) {
        val streak = db.streakDao().get() ?: return@withContext false

        if (streak.freezeCount <= 0) {
            return@withContext false
        }

        // Use freeze locally
        val affectedRows = db.streakDao().useFreeze()
        if (affectedRows == 0) {
            return@withContext false
        }

        // Try to sync with server
        try {
            val now = Clock.System.now()
            planbotApiService.useStreakFreeze(UseStreakFreezeBody(now.toString()))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync freeze usage to server", e)
            // Local change already applied, will reconcile on next fetch
        }

        true
    }

    override suspend fun checkAndUpdateStreakStatus() = withContext(Dispatchers.IO) {
        val streak = db.streakDao().get() ?: return@withContext
        val now = Clock.System.now()

        // Check if we need to reset freeze_used_today (new day)
        val lastUpdate = streak.lastUpdateDate
        if (lastUpdate != null) {
            val tz = TimeZone.currentSystemDefault()
            val lastUpdateDay = lastUpdate.toLocalDateTime(tz).date
            val today = now.toLocalDateTime(tz).date

            if (lastUpdateDay != today && streak.freezeUsedToday) {
                db.streakDao().resetFreezeUsedToday()
            }
        }

        // Check and apply streak reset logic
        checkStreakReset(streak, now)
    }

    override suspend fun isVersionApproved(): Boolean {
        try {
            val response = planbotApiService.getBouncerResponse()
            return response.allowed
        } catch (e: HttpException) {
            // in case the request fails, fall back to approving the version
            return true
        }
    }
}
