package app.avoor.planbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.api.models.PlancoinReward
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.dao.PlancoinRewardDao
import app.avoor.planbot.domain.PlancoinController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PlancoinViewModel(
    private val repository: PlanbotApiRepository,
    private val rewardDao: PlancoinRewardDao,
    private val plancoinController: PlancoinController
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlancoinState())
    val uiState: StateFlow<PlancoinState> = _uiState.asStateFlow()

    init {
        observePlancoins()
        loadRewards()
    }

    private fun loadRewards() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.update {
                    it.copy(
                        rewards = rewardDao.getAll()
                    )
                }
            }
        }
    }

    private fun observePlancoins() {
        viewModelScope.launch {
            plancoinController.balance.collect { bal ->
                _uiState.update {
                    it.copy(
                        plancoins = bal,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun showError(error: String) {
        _uiState.update { it.copy(error = error) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun buy(reward: PlancoinReward) {
        viewModelScope.launch {
            // buy the reward
            val result = plancoinController.buyReward(reward)
            // if we couldn't buy the reward, show an error
            if (!result) {
                showError("Not enough plancoins.")
            }
        }
    }

    fun showExplainer() {
        _uiState.update { it.copy(showExplainer = true) }
    }

    fun hideExplainer() {
        _uiState.update { it.copy(showExplainer = false) }
    }

    fun showAddReward() {
        _uiState.update { it.copy(showAddReward = true) }
    }

    fun hideAddReward() {
        _uiState.update { it.copy(showAddReward = false) }
    }

    fun createReward(name: String, cost: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // create and add a reward
                val reward = PlancoinReward(
                    UUID.randomUUID().toString(),
                    name,
                    cost,
                    -1
                )
                rewardDao.insert(reward)
            }
            // reload the list
            loadRewards()
            // hide the reward screen
            hideAddReward()
        }
    }

    fun deleteReward(reward: PlancoinReward) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                rewardDao.delete(reward)
            }
            // reload the list
            loadRewards()
            // show a message
            showError("Reward deleted")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                PlancoinViewModel(
                    repository = appCtr.planbotApiRepository,
                    rewardDao = appCtr.database.plancoinRewardDao(),
                    plancoinController = appCtr.plancoinController
                )
            }
        }
    }
}