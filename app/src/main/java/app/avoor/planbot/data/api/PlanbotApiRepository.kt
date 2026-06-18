package app.avoor.planbot.data.api

import android.net.Uri
import app.avoor.planbot.api.models.FeedbackReason
import app.avoor.planbot.api.models.PlancoinTransaction
import app.avoor.planbot.api.models.Streak
import kotlinx.coroutines.flow.Flow
import app.avoor.planbot.api.models.Task
import app.avoor.planbot.api.models.User
import app.avoor.planbot.api.models.UserApi
import app.avoor.planbot.api.models.UserWithoutTokens
import app.avoor.planbot.api.responses.SignupEndpointResponse
import app.avoor.planbot.api.responses.VerifyEndpointResponse

/**
 * Repository for interfacing with the Planbot API.
 */
interface PlanbotApiRepository {
    // Authentication
    /**
     * Sign in.
     *
     * This function doesn't require any authentication.
     *
     * @param email the user's email.
     * @param password the user's password.
     *
     * @return the user object.
     */
    suspend fun login(email: String, password: String): UserApi

    /**
     * Obtain an access token.
     *
     * This function requires a refresh token.
     *
     * @returns an access token.
     */
    suspend fun obtainAccessToken(): String

    /**
     * Sign in using an access token.
     *
     * This function requires an access token.
     *
     * @return the user object.
     */
    suspend fun loginUsingAccessToken(): User

    /**
     * Sign up.
     *
     * This function doesn't require any authentication.
     *
     * @param email the user's email.
     * @param password the user's password.
     * @param name the user's name.
     *
     * @return a signup response.
     */
    suspend fun signup(email: String, password: String, name: String): SignupEndpointResponse

    /**
     * Verify a user's email using a token sent to it.
     *
     * This function requires an access token.
     *
     * @return a verification response.
     */
    suspend fun verifyEmail(token: String): VerifyEndpointResponse

    /**
     * Load user data.
     *
     * NOTE: this always loads data from the server. It's preferred to use the
     * cached data from UserManager.
     *
     * @return the current user's account.
     */
    suspend fun fetchUser(): UserWithoutTokens

    /**
     * Upload a profile picture.
     *
     * This function requires an access token.
     *
     * @param uri the URI of the new profile picture.
     *
     * @return a verification response.
     */
    suspend fun uploadProfilePicture(uri: Uri): VerifyEndpointResponse

    /**
     * Sends feedback.
     *
     * This function requires an access token.
     *
     * @param reason the ID of the reason for deleting the account.
     * @param body extra text, optional.
     */
    suspend fun sendFeedback(reason: FeedbackReason, body: String?)

    /**
     * Resend the account verification email.
     *
     * This function requires an access token of an unverified account.
     */
    suspend fun resendVerifyEmail()

    /**
     * Deletes the current user.
     *
     * This function requires an access token.
     */
    suspend fun deleteAccount()

    // Task Management
    // TODO remove? tasks are calendar events in the final app, but we might need this
    // TODO in the future
    /**
     * Get all tasks.
     *
     * This function requires an access token.
     *
     * @return the list of tasks.
     */
    suspend fun getTasks(): List<Task>

    /**
     * Create a task.
     *
     * This function requires an access token.
     *
     * @param title the task title.
     * @param description the task description.
     * @param priority the task priority (1=low, 2=medium, 3=high).
     * @param dueDate the task due date.
     *
     * @return the created task.
     */
    suspend fun createTask(
        title: String,
        description: String? = null,
        priority: Int? = null,
        dueDate: String? = null
    ): Task

    /**
     * Update a task.
     *
     * This function requires an access token.
     *
     * @param taskId the task ID.
     * @param title the task title.
     * @param description the task description.
     * @param priority the task priority (1=low, 2=medium, 3=high).
     * @param isCompleted whether the task is completed.
     * @param dueDate the task due date.
     */
    suspend fun updateTask(
        taskId: Int,
        title: String? = null,
        description: String? = null,
        priority: Int? = null,
        isCompleted: Boolean? = null,
        dueDate: String? = null
    )

    /**
     * Delete a task.
     *
     * This function requires an access token.
     *
     * @param taskId the task ID.
     */
    suspend fun deleteTask(taskId: Int)

    // Plancoins
    /**
     * Add plancoins.
     *
     * This function requires an access token.
     *
     * @param amount the amount of plancoins to add.
     * @param reason the reason for adding plancoins.
     *
     * @return the new plancoin balance.
     */
    suspend fun addPlancoins(amount: Int, reason: String? = null): Int

    /**
     * Get plancoin history.
     *
     * This function requires an access token.
     *
     * @return the list of plancoin transactions.
     */
    suspend fun getPlancoinHistory(): List<PlancoinTransaction>

    // Streak Management

    /**
     * Get the current streak state as a Flow for real-time updates.
     */
    @Deprecated(
        "Moved to the domain layer; obtain a copy of `AppDatabase` and use the extension method `AppDatabase.getStreakFlow()` from `.domain` instead"
    )
    fun getStreakFlow(): Flow<Streak?>

    /**
     * Get the current streak state.
     */
    @Deprecated(
        "Moved to the domain layer; obtain a copy of `AppDatabase` and use the extension method `AppDatabase.getStreakFlow()` from `.domain` instead"
    )
    suspend fun getStreak(): Streak?

    /**
     * Fetch the streak state from the server and update local cache.
     */
    suspend fun fetchStreakFromServer(): Streak?

    /**
     * Check if a streak update has been sent today.
     */
    suspend fun hasUpdatedStreakToday(): Boolean

    /**
     * Send a streak update. Creates local record and attempts server sync.
     * If sync fails, the update is queued for later.
     */
    suspend fun sendStreakUpdate(): Streak

    /**
     * Sync all pending streak updates to the server.
     * @return number of successfully synced updates
     */
    suspend fun syncPendingStreakUpdates(): Int

    /**
     * Sync all pending plancoin transactions to the server.
     * @return Pair of (number of successfully synced updates, server balance after sync or null if sync failed)
     */
    suspend fun syncPendingPlancoinTransactions(): Pair<Int, Int?>

    /**
     * Use a streak freeze to prevent streak reset.
     * @return true if freeze was successfully used
     */
    suspend fun useStreakFreeze(): Boolean

    /**
     * Check if streak should be reset (no update for more than 1 day)
     * and apply freeze if available.
     */
    suspend fun checkAndUpdateStreakStatus()

    /**
     * Check if the bouncer allows usage of this version of Planbot.
     */
    suspend fun isVersionApproved(): Boolean
}

