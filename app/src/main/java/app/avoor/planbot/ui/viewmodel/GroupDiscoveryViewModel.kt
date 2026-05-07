package app.avoor.planbot.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.discovery.DiscoveryCommand
import app.avoor.planbot.data.discovery.DiscoveryFailedException
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.discovery.DiscoveryStatusCodes
import app.avoor.planbot.data.discovery.DiscoveryUser
import app.avoor.planbot.domain.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupDiscoveryViewModel(
    val discoveryRepository: DiscoveryRepository,
    val authTokenProvider: AuthTokenProvider,
    val userManager: UserManager,
    val apiRepository: PlanbotApiRepository,
    val context: Application
): ViewModel() {

    private val _uiState = MutableStateFlow(GroupDiscoveryState())
    val uiState: StateFlow<GroupDiscoveryState> = _uiState.asStateFlow()

    val needsInit = mutableStateOf(true)
    val onResumeHandlerEnabled = mutableStateOf(false)

    fun init() {
        viewModelScope.launch {
            needsInit.value = false
            // first, check if this version of the app is still supported
            Log.d("avr#gdvm","checking compat")
            val compatResult = discoveryRepository.compatCheck()
            _uiState.update {
                it.copy(
                    compatible = compatResult.compatible
                )
            }
            Log.d("avr#gdvm","authing")
            auth()
            // create a group
            Log.d("avr#gdvm","creating group")
            val grpLink = "placeholder" //apiRepository.createDiscoveryGroup()
            _uiState.update {
                it.copy(
                    link = grpLink
                )
            }

            // start listening
            discoveryRepository.getEvents().collect { response ->
                when (response.command) {
                    // If a user wants to join, add them to the requester list
                    DiscoveryCommand.JOIN -> {
                        _uiState.update {
                            it.copy(
                                requesters = _uiState.value.requesters + response.user!!
                            )
                        }
                    }
                    // If a user was approved, move them to the member list
                    DiscoveryCommand.APPROVE -> {
                        _uiState.update {
                            it.copy(
                                members = _uiState.value.members + response.user!!,
                                requesters = _uiState.value.requesters.filter { user -> user != response.user }
                            )
                        }
                    }
                    // If a user has left, remove them from both lists
                    DiscoveryCommand.LEAVE -> {
                        _uiState.update {
                            it.copy(
                                members = _uiState.value.members.filter { user -> user != response.user },
                                requesters = _uiState.value.requesters.filter { user -> user != response.user }
                            )
                        }
                    }
                    // Don't handle any other commands - they are out of scope for this screen
                    else -> {}
                }
            }
        }
    }

    suspend fun auth() {
        Log.d("avr#dvm", "checking login")
        // the user must be signed in for this action
        userManager.checkLogin()
        // get the access token (it must exist after this)
        Log.d("avr#dvm", "requesting access token")
        val accessToken = authTokenProvider.getAccessToken()!!
        // sign in to discovery
        Log.d("avr#dvm", "signing in to discovery socket")
        val statusCode = discoveryRepository.logIn(accessToken)
        // if the server sends "unauthorized", the access token probably expired
        // get a new one!
        if (statusCode == DiscoveryStatusCodes.UNAUTHORIZED) {
            userManager.checkLogin()
        }
        Log.d("avr#dvm", "stat code: $statusCode")
    }

    fun initIfNecessary() {
        if (needsInit.value) {
            init()
        }
    }

    fun onResume() {
        Log.d("avr#dvm","onResume called!")
        // android calls onResume when the activity is started for the first time too,
        // which causes the app to send duplicate messages

        // so, if this handler is called for the first time, enable the full functionality
        // and do nothing else
        if (!onResumeHandlerEnabled.value) {
            Log.d("avr#dvm","ignoring onResume")
            onResumeHandlerEnabled.value = true
        }
        // otherwise (if this isn't the first time):
        else {
            Log.d("avr#dvm","starting disc again")
            viewModelScope.launch {
                // the socket likely has a new sid, so sign in again
                auth()
            }
        }
    }

    fun disconnect() {
        discoveryRepository.disconnect()
    }

    fun approve(user: DiscoveryUser) {
        viewModelScope.launch {
            try {
                discoveryRepository.approve(user)
            } catch(e: DiscoveryFailedException) {
                _uiState.update {
                    it.copy(
                        showActionError = true
                    )
                }
            }
        }
    }

    fun leaveGroup() {
        viewModelScope.launch {
            discoveryRepository.leave()
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
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                GroupDiscoveryViewModel(
                    discoveryRepository = appCtr.discoveryRepository,
                    authTokenProvider = appCtr.authTokenProvider,
                    userManager = appCtr.userManager,
                    apiRepository = appCtr.planbotApiRepository,
                    context = application
                )
            }
        }
    }
}