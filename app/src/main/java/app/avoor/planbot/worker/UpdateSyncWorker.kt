package app.avoor.planbot.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.domain.PlancoinController
import app.avoor.planbot.domain.UserManager
import java.util.concurrent.TimeUnit

/**
 * Worker that syncs pending updates to the server.
 * Scheduled to run when the device has network connectivity.
 */
class UpdateSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "UpdateSyncWorker"
        private const val WORK_NAME = "update_sync"
        private const val WORK_NAME_PERIODIC = "update_sync_periodic"

        /**
         * Enqueue a one-time sync when network becomes available.
         */
        fun enqueueOneTime(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<UpdateSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueue(request)

            Log.d(TAG, "Enqueued one-time streak sync")
        }

        /**
         * Schedule periodic sync to run every 15 minutes when network is available.
         * This ensures offline updates are synced even if the app is closed.
         */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<UpdateSyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME_PERIODIC,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )

            Log.d(TAG, "Scheduled periodic update sync")
        }

        /**
         * Cancel all scheduled sync work.
         */
        fun cancelAll(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_PERIODIC)
        }
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting update sync")

        return try {
            val application = applicationContext as AvoorApplication
            val repository = application.container.planbotApiRepository
            val plancoinController = application.container.plancoinController

            syncStreakUpdates(repository)
            syncPlancoinTransactions(repository, plancoinController)

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync streaks", e)
            // Retry with exponential backoff
            Result.retry()
        }
    }

    private suspend fun syncStreakUpdates(repository: PlanbotApiRepository) {
        // Sync pending updates
        val syncedCount = repository.syncPendingStreakUpdates()
        Log.d(TAG, "Synced $syncedCount streak updates")

        // Also check and update streak status
        repository.checkAndUpdateStreakStatus()

        // Fetch latest from server to reconcile
        repository.fetchStreakFromServer()
    }

    private suspend fun syncPlancoinTransactions(
        repository: PlanbotApiRepository,
        plancoinController: PlancoinController
    ) {
        // Sync pending updates
        val (syncedCount, serverBalance) = repository.syncPendingPlancoinTransactions()
        Log.d(TAG, "Synced $syncedCount plancoin transactions")

        // Update the plancoin controller with the server balance
        if (serverBalance != null) {
            plancoinController.updateServerBalance(serverBalance)
        }
    }
}
