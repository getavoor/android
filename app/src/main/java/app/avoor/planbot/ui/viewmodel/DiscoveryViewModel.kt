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
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.discovery.DiscoveryFailedException
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.discovery.DiscoveryStartBody
import app.avoor.planbot.data.discovery.DiscoveryStatusCodes
import app.avoor.planbot.domain.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    val repository: DiscoveryRepository,
    val authTokenProvider: AuthTokenProvider,
    val userManager: UserManager,
    val context: Application
): ViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryState())
    val uiState: StateFlow<DiscoveryState> = _uiState.asStateFlow()

    val needsInit = mutableStateOf(true)
    val onResumeHandlerEnabled = mutableStateOf(false)

    fun init(type: String?) {
        viewModelScope.launch {
            // connect if necessary
            if (!repository.isConnected()) {
                repository.connect()
            }

            // first, check if this version of the app is still supported
            Log.d("avr#dvm","checking compat")
            val compatResult = repository.compatCheck()
            _uiState.update {
                it.copy(
                    compatible = compatResult.compatible
                )
            }
            Log.d("avr#dvm","checking compat")
            auth()
            // finally, start discovery
            try {
                Log.d("avr#dvm", "starting dsc")
                repository.start(DiscoveryStartBody(
                    type
                ))
            }
            // catch any exceptions that may occur
            catch (e: DiscoveryFailedException) {
                Log.d("avr#dvm", "err - stat code: ${e.statusCode}")
                // if the server sends "unauthorized", the access token probably expired
                // get a new one!
                if (e.statusCode == DiscoveryStatusCodes.UNAUTHORIZED) {
                    userManager.checkLogin()
                }
                // otherwise, show an error
                else if (e.statusCode != DiscoveryStatusCodes.OK) {
                    _uiState.update {
                        it.copy(
                            showError = true
                        )
                    }
                }
            }

            // start listening
            repository.getEvents().collect { response ->
                // TODO [fluff] add parsing for commands
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
        val statusCode = repository.logIn(accessToken)
        // if the server sends "unauthorized", the access token probably expired
        // get a new one!
        if (statusCode == DiscoveryStatusCodes.UNAUTHORIZED) {
            userManager.checkLogin()
        }
        Log.d("avr#dvm", "stat code: $statusCode")
    }

    fun discoverAgain() {
        // clear the old result
        _uiState.update {
            it.copy(
                showResult = false,
                imageRequest = null
            )
        }
        viewModelScope.launch {
            // continue discovery
            try {
                Log.d("avr#dvm", "continuing dsc")
                repository.continueDiscovery()
            }
            // catch any exceptions that may occur
            catch (e: DiscoveryFailedException) {
                Log.d("avr#dvm#dac", "err - stat code: ${e.statusCode}")
                // if the server sends "unauthorized", the access token probably expired
                if (e.statusCode == DiscoveryStatusCodes.UNAUTHORIZED) {
                    Log.d("avr#dvm#dac", "we're unauthorized")
                    // check login
                    userManager.checkLogin()
                    // get the access token
                    Log.d("avr#dvm#dac", "requesting access token")
                    val accessToken = authTokenProvider.getAccessToken()!!
                    // sign in to discovery
                    Log.d("avr#dvm#dac", "signing in to discovery socket")
                    repository.logIn(accessToken)
                    // retry
                    discoverAgain()
                }
            }
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
                // if there isn't a visible result, restart discovery
                if (!_uiState.value.showResult) {
                    discoverAgain()
                }
            }
        }
    }

    fun disconnect() {
        // disconnect from the underlying discovery repo
        repository.disconnect()
        // make sure this VM is initialized the next time the discovery screen is shown
        // (as the discovery API likely has no reference to this user anymore)
        needsInit.value = true
    }

    fun setNeedsInit() {
        Log.d("avr#dvm", "Setting needsInit...")
        // make sure this VM is initialized the next time the discovery screen is shown
        // (as the discovery API likely has no reference to this user anymore)
        needsInit.value = true
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                DiscoveryViewModel(
                    repository = appCtr.discoveryRepository,
                    authTokenProvider = appCtr.authTokenProvider,
                    userManager = appCtr.userManager,
                    context = application
                )
            }
        }
    }
}