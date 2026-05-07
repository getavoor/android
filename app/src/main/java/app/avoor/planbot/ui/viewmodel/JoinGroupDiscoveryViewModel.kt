package app.avoor.planbot.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.request.CachePolicy
import coil.request.ImageRequest
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.R
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.discovery.DiscoveryCommand
import app.avoor.planbot.data.discovery.DiscoveryFailedException
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.discovery.DiscoveryStatusCodes
import app.avoor.planbot.domain.UserManager
import app.avoor.planbot.ui.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val pfpPlaceholder = R.drawable.default_pfp

class JoinGroupDiscoveryViewModel(
    val discoveryRepository: DiscoveryRepository,
    val authTokenProvider: AuthTokenProvider,
    val userManager: UserManager,
    val context: Application
): ViewModel() {

    private val _uiState = MutableStateFlow(JoinGroupDiscoveryState())
    val uiState: StateFlow<JoinGroupDiscoveryState> = _uiState.asStateFlow()

    private var navigator: Navigator? = null

    fun setNavigator(navigator: Navigator) {
        this.navigator = navigator
    }

    val needsInit = mutableStateOf(true)
    val onResumeHandlerEnabled = mutableStateOf(false)

    fun init(groupID: String?) {
        viewModelScope.launch {
            needsInit.value = false
            // first, check if this version of the app is still supported
            val compatResult = discoveryRepository.compatCheck()
            _uiState.update {
                it.copy(
                    compatible = compatResult.compatible,
                    groupID = groupID
                )
            }
            // then, sign in
            auth()
            // try to join the group
            joinGroup(groupID)


            // start listening
            discoveryRepository.getEvents().collect { response ->
                when (response.command) {
                    // If a user was approved:
                    DiscoveryCommand.APPROVE -> {
                        var approved = _uiState.value.approved
                        Log.d("avr#jgdvm", "approved=$approved")
                        // Check if that user was this one
                        if (response.user!!.id == userManager.getCurrentUser()?.id) {
                            Log.d("avr#jgdvm", "we're in")
                            // If the user isn't already marked as approved, mark them
                            if (!approved) approved = true
                        }
                        _uiState.update {
                            it.copy(
                                // add the user to the member list
                                members = _uiState.value.members + response.user,
                                // update approved
                                approved = approved
                            )
                        }
                        Log.d("avr#jgdvm", "upd uistate")
                    }
                    // If a user has left, remove them from both lists
                    DiscoveryCommand.LEAVE -> {
                        _uiState.update {
                            it.copy(
                                members = _uiState.value.members.filter { user -> user != response.user }
                            )
                        }
                    }
                    // Don't handle any other commands - they are out of scope for this screen
                    else -> {}
                }
            }
        }
    }

    private suspend fun joinGroup(groupID: String?) {
        // null group IDs are automatically considered invalid
        if (groupID == null) {
            _uiState.update {
                it.copy(
                    showLinkError = true
                )
            }
        }
        // if it's not null, try to join this group
        else {
            try {
                discoveryRepository.join(groupID)
            }
            // in case of a server error:
            catch (e: DiscoveryFailedException) {
                // if the error is invalid data, the group ID is invalid - show an invalid link message
                if (e.statusCode == DiscoveryStatusCodes.INVALID_DATA) {
                    _uiState.update {
                        it.copy(
                            showLinkError = true
                        )
                    }
                }
                // if it's unauthorized, the token has expired
                else if (e.statusCode == DiscoveryStatusCodes.UNAUTHORIZED) {
                    auth()
                }
            }
        }
    }

    suspend fun auth() {
        Log.d("avr#jdvm", "checking login")
        // the user must be signed in for this action
        userManager.checkLogin()
        // get the access token (it must exist after this)
        Log.d("avr#jdvm", "requesting access token")
        val accessToken = authTokenProvider.getAccessToken()!!
        // sign in to discovery
        Log.d("avr#jdvm", "signing in to discovery socket")
        val statusCode = discoveryRepository.logIn(accessToken)
        // if the server sends "unauthorized", the access token probably expired
        // get a new one!
        if (statusCode == DiscoveryStatusCodes.UNAUTHORIZED) {
            userManager.checkLogin()
        }
        Log.d("avr#jdvm", "stat code: $statusCode")
    }

    fun initIfNecessary(groupID: String?) {
        if (needsInit.value) {
            Log.d("avr#jdvm", "doing init")
            init(groupID)
        }
    }

    fun onResume() {
        Log.d("avr#jdvm", "onResume called!")
        // android calls onResume when the activity is started for the first time too,
        // which causes the app to send duplicate messages

        // so, if this handler is called for the first time, enable the full functionality
        // and do nothing else
        if (!onResumeHandlerEnabled.value) {
            Log.d("avr#jdvm", "ignoring onResume")
            onResumeHandlerEnabled.value = true
        }
        // otherwise (if this isn't the first time):
        else {
            Log.d("avr#jdvm", "starting disc again")
            viewModelScope.launch {
                // the socket likely has a new sid, so sign in again
                auth()
                // and then try to rejoin

            }
        }
    }

    fun disconnect() {
        discoveryRepository.disconnect()
    }

    fun leaveGroup() {
        viewModelScope.launch {
            // leave the group
            discoveryRepository.leave()
            // disconnect from the discovery socket
            discoveryRepository.disconnect()
            // and finally, return to the main screen
            navigator?.returnOrNavigate(Screen.MAIN)
        }
    }

    fun hideLinkError() {
        // hide the error
        _uiState.update {
            it.copy(
                showLinkError = false
            )
        }
        // go to the main screen and remove all history
        // (at this point the only screens in history should be this one, which should
        //  be closed anyway, and main, which will be reopened)
        navigator?.returnOrNavigate(Screen.MAIN)
    }

    fun getUserImageRequest(): ImageRequest {
        return ImageRequest.Builder(context)
            .data(userManager.getCurrentUser()?.photoUrl)
            .memoryCacheKey(userManager.getCurrentUser()?.photoUrl)
            .diskCacheKey(userManager.getCurrentUser()?.photoUrl)
            .placeholder(pfpPlaceholder)
            .error(pfpPlaceholder)
            .fallback(pfpPlaceholder)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    fun rejoinIfNecessary() {
        viewModelScope.launch {
            // Try to get the current group
            try {
                val grp = discoveryRepository.getGroup()
                // Provide it to the UI too
                _uiState.update {
                    it.copy(
                        group = grp
                    )
                }
            }
            // Catch any unsuccessful response codes
            catch (e: DiscoveryFailedException) {
                // If the response code is UNAUTHORIZED, the token has expired
                // While this is unlikely to happen as this function is called shortly after auth,
                // get a new token anyway
                if (e.statusCode == DiscoveryStatusCodes.UNAUTHORIZED) {
                    auth()
                }
                // If the code is INVALID_DATA, the user isn't in a group (e.g. they have left
                // a group, then decided to join another)
                // In that case join the one this screen is for
                else if (e.statusCode == DiscoveryStatusCodes.INVALID_DATA) {
                    joinGroup(_uiState.value.groupID)
                    // Retrieve the group again
                    val grp = discoveryRepository.getGroup()
                    // Provide it to the UI too
                    _uiState.update {
                        it.copy(
                            group = grp
                        )
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                JoinGroupDiscoveryViewModel(
                    discoveryRepository = appCtr.discoveryRepository,
                    authTokenProvider = appCtr.authTokenProvider,
                    userManager = appCtr.userManager,
                    context = application
                )
            }
        }
    }
}