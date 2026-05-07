package app.avoor.planbot.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.domain.UserManager
import app.avoor.planbot.ui.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class DeleteWarnViewModel (
    val repo: PlanbotApiRepository,
    val userManager: UserManager
): ViewModel() {
    private val _uiState = MutableStateFlow(DeleteWarnState())
    val uiState: StateFlow<DeleteWarnState> = _uiState.asStateFlow()

    private var navigator: Navigator? = null
    fun setNavigator(navigator: Navigator) {
        this.navigator = navigator
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
                repo.deleteAccount()
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

    fun hideSuccessDialog() {
        viewModelScope.launch {
            userManager.signOut()
            _uiState.update {
                it.copy(
                    success = false
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                DeleteWarnViewModel(
                    repo = appCtr.planbotApiRepository,
                    userManager = appCtr.userManager
                )
            }
        }
    }
}