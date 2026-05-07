package app.avoor.planbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.api.PlanbotApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NuxStreakViewModel(
    private val repository: PlanbotApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuxStreakState())
    val uiState: StateFlow<NuxStreakState> = _uiState.asStateFlow()

    fun increaseStreak() {
        viewModelScope.launch {
            // only update the streak once per day
            if (!repository.hasUpdatedStreakToday()) {
                repository.sendStreakUpdate()
                repository.syncPendingStreakUpdates()
            }
            _uiState.update { it.copy(nextSwitchesScreens = true) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                NuxStreakViewModel(
                    repository = appCtr.planbotApiRepository
                )
            }
        }
    }
}
