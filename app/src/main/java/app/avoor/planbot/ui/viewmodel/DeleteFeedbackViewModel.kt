package app.avoor.planbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.api.models.FeedbackReason
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.ui.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class DeleteFeedbackViewModel (
    val repo: PlanbotApiRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(DeleteFeedbackState())
    val uiState: StateFlow<DeleteFeedbackState> = _uiState.asStateFlow()

    private var navigator: Navigator? = null
    fun setNavigator(navigator: Navigator) {
        this.navigator = navigator
    }

    fun setReason(reason: FeedbackReason) {
       _uiState.update {
           it.copy(
               reason = reason
           )
       }
    }

    fun send() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    progress = true,
                    showConnectionError = false
                )
            }
            try {
                repo.sendFeedback(_uiState.value.reason!!, _uiState.value.body)
                navigator?.navigate(Screen.DELETE_WARN)
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

    fun setBody(body: String) {
        _uiState.update {
            it.copy(
                body = body
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                DeleteFeedbackViewModel(
                    repo = appCtr.planbotApiRepository
                )
            }
        }
    }
}

