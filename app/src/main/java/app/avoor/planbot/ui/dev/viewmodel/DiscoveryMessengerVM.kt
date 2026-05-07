package app.avoor.planbot.ui.dev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.Gson
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.discovery.DiscoveryResponse
import app.avoor.planbot.domain.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A raw representation of a discovery request or response.
 *
 * Meant for development tools; for sending requests, use the functions in
 * [DiscoveryRepository], and for receiving responses, use [DiscoveryRepository.getEvents]
 * (which returns a [DiscoveryResponse]).
 */
data class RawDiscoveryReqRes(
    /**
     * If true, this is a request; otherwise, a response.
     */
    val isRequest: Boolean,
    val command: String,
    val argument: String
)

internal fun DiscoveryResponse.toReqRes(): RawDiscoveryReqRes {
    return RawDiscoveryReqRes(
        isRequest = false,
        command = this.command.id,
        argument =
            if (this.user != null) {
                Gson().toJson(this.user)
            } else "Argument unknown or not parseable by DiscoveryResponse"
    )
}

data class DiscoveryMessengerState(
    val progress: Boolean = false,
    val history: List<RawDiscoveryReqRes> = listOf(),
)

class DiscoveryMessengerVM(
    val repository: DiscoveryRepository,
    val userManager: UserManager,
    val authTokenProvider: AuthTokenProvider,
): ViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryMessengerState())
    val uiState: StateFlow<DiscoveryMessengerState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // start listening
            repository.getEvents().collect { response ->
                _uiState.update {
                    it.copy(
                        history = _uiState.value.history + response.toReqRes()
                    )
                }
            }
        }
    }

    fun send(cmd: String, arg: String, type: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    progress = true,
                    history = _uiState.value.history + RawDiscoveryReqRes(
                        isRequest = true,
                        command = cmd,
                        argument =
                            when (type) {
                                "Access token" -> "(user access token)"
                                "Refresh token" -> "(user refresh token)"
                                "None" -> "(none)"
                                else -> arg
                            }
                    )
                )
            }
            val resp = when (type) {
                "String" -> {
                    repository.sendRawData(cmd, arg)
                }
                "Access token" -> {
                    repository.sendRawData(cmd, authTokenProvider.getAccessToken()!!)
                }
                "Refresh token" -> {
                    repository.sendRawData(cmd, authTokenProvider.getRefreshToken()!!)
                }
                "Integer" -> {
                    repository.sendRawData(cmd, arg.toInt())
                }
                "None" -> {
                    repository.sendRawData(cmd, null)
                }

                else -> {0}
            }
            _uiState.update {
                it.copy(
                    progress = false,
                    history = _uiState.value.history + RawDiscoveryReqRes(
                        isRequest = false,
                        command = cmd,
                        argument = "(ack with resp code $resp)"
                    )
                )
            }
        }
    }

    fun newToken() {
        viewModelScope.launch {
            userManager.checkLogin()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                DiscoveryMessengerVM(
                    repository = appCtr.discoveryRepository,
                    userManager = appCtr.userManager,
                    authTokenProvider = appCtr.authTokenProvider,
                )
            }
        }
    }
}