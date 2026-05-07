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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class NuxPlancoinViewModel(
    private val rewardDao: PlancoinRewardDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuxPlancoinState())
    val uiState: StateFlow<NuxPlancoinState> = _uiState.asStateFlow()

    fun showAddReward() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                showAddReward = true,
                nextSwitchesScreens = true
            ) }
        }
    }

    fun hideAddReward() {
        _uiState.update { it.copy(showAddReward = false) }
    }

    fun createReward(name: String, cost: Int, afterAdd: () -> Unit) {
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
            // hide the reward screen
            hideAddReward()
            MainScope().launch {
                afterAdd()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                NuxPlancoinViewModel(
                    rewardDao = appCtr.database.plancoinRewardDao()
                )
            }
        }
    }
}
