package app.avoor.planbot.domain

import app.avoor.planbot.api.models.PlancoinReward
import app.avoor.planbot.api.models.PlancoinTransaction
import app.avoor.planbot.data.dao.PlancoinDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.time.Clock

/**
 * Manages plancoins as the source of truth.
 *
 * The balance is computed as: serverBalance + unsyncedLocalTransactions
 *
 * This allows offline-first operation where:
 * - Plancoin rewards are added locally immediately
 * - Syncing happens via UpdateSyncWorker (batched, every 15 min)
 * - Server balance is updated after successful sync
 */
class PlancoinController(
    private val userManager: UserManager,
    private val plancoinDao: PlancoinDao,
    private val scope: CoroutineScope
) {
    companion object {
        private const val PLANCOINS_PER_MINUTE = 1
    }

    /**
     * Balance as reported by the server (updated after login/sync).
     */
    private val _serverBalance = MutableStateFlow(0)

    /**
     * The combined balance: server balance + unsynced local transactions.
     * This is the authoritative balance for UI display.
     */
    val balance: StateFlow<Int> = combine(
        _serverBalance,
        plancoinDao.getUnsyncedSumFlow()
    ) { serverBalance, unsyncedSum ->
        serverBalance + unsyncedSum
    }.stateIn(scope, SharingStarted.Eagerly, 0)

    init {
        // Initialize server balance from cached user
        userManager.getCurrentUser()?.let { user ->
            _serverBalance.value = user.plancoins
        }

        // Listen to login state to update server balance when user logs in
        scope.launch {
            userManager.loginState.collect { state ->
                if (state is LoginState.LoggedIn) {
                    userManager.getCurrentUser()?.let { user ->
                        _serverBalance.value = user.plancoins
                    }
                }
            }
        }

        // Keep UserManager in sync with our balance for UI components
        scope.launch {
            balance.collect { newBalance ->
                userManager.updateLocalUser(plancoins = newBalance)
            }
        }
    }

    /**
     * Add plancoins to the local database.
     * No API call is made - syncing happens via UpdateSyncWorker.
     *
     * @param amount The amount of plancoins to add.
     * @param reason The reason for this transaction.
     * @throws IllegalStateException if the transaction will make the user's balance negative.
     */
    suspend fun addPlancoins(amount: Int, reason: String?) = withContext(Dispatchers.IO) {
        // before inserting, make sure the user will have a positive or zero balance
        if (balance.value + amount < 0) {
            throw IllegalStateException("User does not have enough plancoins for this transaction")
        }

        val transaction = PlancoinTransaction(
            update_id = UUID.randomUUID().toString(),
            amount = amount,
            reason = reason,
            createdAt = Clock.System.now(),
            synced = false
        )
        plancoinDao.insert(transaction)
    }

    /**
     * Add plancoins for completed focus time.
     * Calculates reward based on minutes spent focusing.
     *
     * @param minutes The number of minutes spent focusing.
     */
    suspend fun addSpentTimePlancoins(minutes: Int) {
        val coins = minutes * PLANCOINS_PER_MINUTE
        if (coins > 0) {
            addPlancoins(coins, "focus_session")
        }
    }

    /**
     * Update the server balance after a successful sync.
     * Called by UpdateSyncWorker after syncing transactions.
     *
     * @param balance The new server balance.
     */
    fun updateServerBalance(balance: Int) {
        _serverBalance.value = balance
    }

    /**
     * Try to buy a plancoin reward.
     *
     * @param reward the reward to buy.
     * @return `true` if the operation was successful, `false` if the user
     * does not have enough plancoins.
     */
    suspend fun buyReward(reward: PlancoinReward) : Boolean {
        // check if the user has enough plancoins
        if (balance.value < reward.cost) {
            return false
        }
        // check if the item is out of stock and its stock is limited
        if (reward.stock == 0) {
            return false
        }

        // create a plancoin transaction
        addPlancoins(-reward.cost, "reward")
        return true
    }
}
