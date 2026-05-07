package app.avoor.planbot.data.mock

import android.net.Uri
import app.avoor.planbot.api.models.FeedbackReason
import app.avoor.planbot.api.models.PlancoinTransaction
import app.avoor.planbot.api.models.Streak
import app.avoor.planbot.api.models.Task
import app.avoor.planbot.api.models.User
import app.avoor.planbot.api.models.UserApi
import app.avoor.planbot.api.models.UserWithoutTokens
import app.avoor.planbot.api.models.simplify
import app.avoor.planbot.api.responses.SignupEndpointResponse
import app.avoor.planbot.api.responses.VerifyEndpointResponse
import app.avoor.planbot.data.api.PlanbotApiRepository
import kotlinx.coroutines.flow.Flow

class DemoPlanbotApiRepository: PlanbotApiRepository {
    private val demoTasks = mutableListOf(
        Task(
            1,
            "Complete demo task",
            "This is a demo task",
            false,
            "2025-01-01T10:00:00",
            null,
            "2025-01-10T23:59:59",
            2
        )
    )

    private val demoTransactions = mutableListOf(
        PlancoinTransaction("1", 50, "Demo reward", null),
        PlancoinTransaction("2", 50, "Streak bonus", null)
    )

    // Authentication
    override suspend fun login(email: String, password: String): UserApi {
        return DEMO_USER
    }

    override suspend fun obtainAccessToken(): String {
        return "DEMOasUserOneRT"
    }

    override suspend fun loginUsingAccessToken(): User {
        return DEMO_USER.simplify()
    }

    override suspend fun signup(
        email: String,
        password: String,
        name: String
    ): SignupEndpointResponse {
        return SignupEndpointResponse("Success", "SignupEmailResponseXT", "SignupEmailResponseRT")
    }

    override suspend fun verifyEmail(token: String): VerifyEndpointResponse {
        return VerifyEndpointResponse("Success", DEMO_USER.removeTokens())
    }

    override suspend fun fetchUser(): UserWithoutTokens {
        return DEMO_USER.simplify().removeTokens()
    }

    override suspend fun uploadProfilePicture(uri: Uri): VerifyEndpointResponse {
        return VerifyEndpointResponse("Success", DEMO_USER.removeTokens())
    }

    override suspend fun sendFeedback(reason: FeedbackReason, body: String?) {

    }

    override suspend fun resendVerifyEmail() {

    }

    override suspend fun deleteAccount() {

    }

    // Task Management
    override suspend fun getTasks(): List<Task> {
        return demoTasks
    }

    override suspend fun createTask(
        title: String,
        description: String?,
        priority: Int?,
        dueDate: String?
    ): Task {
        val task = Task(
            demoTasks.size + 1,
            title,
            description,
            false,
            "2025-01-06T10:00:00",
            null,
            dueDate,
            priority ?: 2
        )
        demoTasks.add(task)
        return task
    }

    override suspend fun updateTask(
        taskId: Int,
        title: String?,
        description: String?,
        priority: Int?,
        isCompleted: Boolean?,
        dueDate: String?
    ) {
        val taskIndex = demoTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = demoTasks[taskIndex]
            demoTasks[taskIndex] = task.copy(
                title = title ?: task.title,
                description = description ?: task.description,
                priority = priority ?: task.priority,
                isCompleted = isCompleted ?: task.isCompleted,
                dueDate = dueDate ?: task.dueDate,
                completedAt = if (isCompleted == true) "2025-01-06T10:00:00" else task.completedAt
            )
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        demoTasks.removeIf { it.id == taskId }
    }

    // Plancoins
    override suspend fun addPlancoins(amount: Int, reason: String?): Int {
        val transaction = PlancoinTransaction(
            update_id = "test_${demoTransactions.size + 1}",
            amount = amount,
            reason = reason,
            createdAt = null
        )
        demoTransactions.add(transaction)
        return 100 + amount
    }

    override suspend fun getPlancoinHistory(): List<PlancoinTransaction> {
        return demoTransactions
    }

    override fun getStreakFlow(): Flow<Streak?> {
        TODO("Not yet implemented")
    }

    override suspend fun getStreak(): Streak {
        return Streak(5, 10, 0)
    }

    override suspend fun fetchStreakFromServer(): Streak? {
        TODO("Not yet implemented")
    }

    override suspend fun hasUpdatedStreakToday(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun sendStreakUpdate(): Streak {
        TODO("Not yet implemented")
    }

    override suspend fun syncPendingStreakUpdates(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun syncPendingPlancoinTransactions(): Pair<Int, Int?> {
        return Pair(0, null)
    }

    override suspend fun useStreakFreeze(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun checkAndUpdateStreakStatus() {
        TODO("Not yet implemented")
    }

    override suspend fun isVersionApproved(): Boolean {
        return true
    }
}
