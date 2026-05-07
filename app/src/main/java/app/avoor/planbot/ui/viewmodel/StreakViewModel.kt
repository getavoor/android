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

class StreakViewModel(
    private val repository: PlanbotApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakState())
    val uiState: StateFlow<StreakState> = _uiState.asStateFlow()

    init {
        loadStreak()
        observeStreak()
    }

    private fun observeStreak() {
        viewModelScope.launch {
            repository.getStreakFlow().collect { streak ->
                if (streak != null) {
                    _uiState.update {
                        it.copy(
                            currentStreak = streak.currentStreak,
                            freezeCount = streak.freezeCount,
                            longestStreak = streak.longestStreak,
                            freezeUsedToday = streak.freezeUsedToday,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun loadStreak() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Check if updated today
                val updatedToday = repository.hasUpdatedStreakToday()
                _uiState.update { it.copy(updatedToday = updatedToday) }

                // Try to fetch from server first
                val serverStreak = repository.fetchStreakFromServer()
                if (serverStreak != null) {
                    _uiState.update {
                        it.copy(
                            currentStreak = serverStreak.currentStreak,
                            freezeCount = serverStreak.freezeCount,
                            longestStreak = serverStreak.longestStreak,
                            freezeUsedToday = serverStreak.freezeUsedToday,
                            isLoading = false
                        )
                    }
                } else {
                    // Fall back to local data
                    val localStreak = repository.getStreak()
                    if (localStreak != null) {
                        _uiState.update {
                            it.copy(
                                currentStreak = localStreak.currentStreak,
                                freezeCount = localStreak.freezeCount,
                                longestStreak = localStreak.longestStreak,
                                freezeUsedToday = localStreak.freezeUsedToday,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }

                // Check streak status (reset if needed)
                repository.checkAndUpdateStreakStatus()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun useStreakFreeze() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val success = repository.useStreakFreeze()
            if (!success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No freezes available"
                    )
                }
            } else {
                // Refresh streak data
                loadStreak()
            }
        }
    }

    fun syncPendingUpdates() {
        viewModelScope.launch {
            repository.syncPendingStreakUpdates()
            loadStreak()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun showExplainer() {
        _uiState.update { it.copy(showExplainer = true) }
    }

    fun hideExplainer() {
        _uiState.update { it.copy(showExplainer = false) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                StreakViewModel(
                    repository = appCtr.planbotApiRepository
                )
            }
        }
    }
}
