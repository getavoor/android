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
import retrofit2.HttpException
import java.net.SocketTimeoutException

class VerifyResendViewModel (
    val repo: PlanbotApiRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(VerifyResendState())
    val uiState: StateFlow<VerifyResendState> = _uiState.asStateFlow()

    fun send() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    progress = true,
                    success = false,
                    showConnectionError = false
                )
            }
            try {
                repo.resendVerifyEmail()
                _uiState.update {
                    it.copy(
                        success = true
                    )
                }
            }
            // if the app can't connect to the server, show a connection error
            catch(e: SocketTimeoutException) {
                _uiState.update {
                    it.copy(
                        showConnectionError = true
                    )
                }
            }
            catch(e: HttpException) {
                _uiState.update {
                    it.copy(
                        showActionError = true
                    )
                }
            }
            _uiState.update {
                it.copy(
                    progress = false
                )
            }
        }
    }

    fun hideActionError() {
        _uiState.update {
            it.copy(
                showActionError = false
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                VerifyResendViewModel(
                    repo = appCtr.planbotApiRepository
                )
            }
        }
    }
}