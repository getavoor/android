package app.avoor.planbot.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.AppContainer
import app.avoor.planbot.domain.LoginState
import app.avoor.planbot.ui.AppAction
import app.avoor.planbot.ui.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    val appContainer: AppContainer,
    val application: Application
): ViewModel() {
    private val _uiState = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    private val REDIRECT_TO_MAIN_ALLOWED_SCREENS = arrayOf(
        Screen.LOGIN.toString(),
        Screen.REGISTER.toString(),
        Screen.AUTO_LOGIN.toString(),
        Screen.NONE.toString()
    )

    private var navigator: Navigator? = null

    fun init(navigator: Navigator) {
        this.navigator = navigator
        listenToLogin()
        // check the version with the bouncer
        checkVersion()
    }

    private fun checkVersion() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showUpdateError = !appContainer.planbotApiRepository.isVersionApproved()
                )
            }
        }
    }

    private fun listenToLogin() {
        viewModelScope.launch {
            appContainer.userManager.loginState.collect { state ->
                Log.d("avr#mvm", "Received state")
                Log.d("avr#mvm", state.toString())
                // If this is an auto login connection error, show a connection error message
                if (state == LoginState.AutoLoginConnectionError) {
                    showConnectionError(AppAction.AUTO_LOGIN)
                }
                // Do the same for a regular connection error
                else if (state == LoginState.ConnectionError) {
                    showConnectionError()
                }
                // If the user is logged in, show the main page.
                else if (state == LoginState.LoggedIn) {
                    if (navigator?.currentScreen in REDIRECT_TO_MAIN_ALLOWED_SCREENS) {
                        if (!_uiState.value.initialLoginDone) {
                            goToMainPage()
                        } else {
                            navigator?.goBack()
                        }
                    }
                }
                // Otherwise:
                else {
                    // Update the UI state based on the login state
                    navigator?.navigate(
                        when (state) {
                            // Use the appropriate page for auto and manual login
                            LoginState.LoginRequired -> Screen.LOGIN
                            LoginState.AutoLoginInProgress -> Screen.AUTO_LOGIN
                            LoginState.SignUp -> Screen.REGISTER
                            // Start NUX if required
                            LoginState.NuxRequired -> Screen.NUX_START
                            // Show a verification screen if verification is required
                            LoginState.VerificationRequired -> Screen.VERIFY
                            // One that tells the user to wait when verification is in progress
                            LoginState.VerificationInProgress -> Screen.VERIFY_WAIT
                            // And a success screen if verification is done
                            LoginState.VerificationComplete -> Screen.VERIFY_COMPLETE
                            // All other states are ignored here, but as "when" requires a
                            // default case, use the main screen
                            else -> Screen.MAIN
                        },
                        state == LoginState.LoginRequired || state == LoginState.SignUp
                    )
                    Log.d("avr#mvm", "Navigated")

                    _uiState.update {
                        it.copy(
                            // Hide the progress
                            progress = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Picks an appropriate main page for the user and goes to it.
     */
    private fun goToMainPage() {
        val user = appContainer.userManager.getCurrentUser()
        // if the user isn't verified, show the verify screen
        if (user?.confirmed == false) {
            Log.d("avr#mvm", "showing verify screen")
            navigator?.navigate(Screen.VERIFY)
        }
        // TODO return the verify complete screen, with the rest of the nux

        // otherwise show the main screen
        else {
            // check for calendar permission
            if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED) {
                navigator?.navigate(Screen.NUX_PERMISSION, "transient")
                Log.d("avr#mvm", "showing perm grant screen")
            } else {
                Log.d("avr#mvm", "showing main screen")
                navigator?.navigate(Screen.MAIN)
            }
        }
    }

    fun checkLogin() {
        viewModelScope.launch {
            checkLoginSuspend()
        }
    }

    private suspend fun checkLoginSuspend() {
        // Notify that an action is in progress
        _uiState.update {
            it.copy(
                progress = true
            )
        }
        // Check for login
        val success = appContainer.userManager.checkLogin()
        // If the user is signed in, hide the progress bar
        if (success) {
            _uiState.update {
                it.copy(
                    progress = false
                )
            }
            if (!_uiState.value.initialLoginDone) {
                _uiState.update {
                    it.copy(
                        initialLoginDone = true
                    )
                }
            }
        }
    }

    private fun showConnectionError(failedAction: AppAction? = null) {
        Log.d("avr#mainvm","showing alert action=$failedAction")
        _uiState.update {
            it.copy(
                showConnectionError = true,
                failedAction = failedAction,
                // also hide the progress indicator
                progress = false
            )
        }
    }

    fun hideConnectionError() {
        _uiState.update {
            it.copy(
                showConnectionError = false,
                failedAction = null
            )
        }
    }

    fun autoLogin() {
        viewModelScope.launch {
            appContainer.userManager.autoLogin()
        }
    }

    fun setVerifyToken(token: String?) {
        if (token != null) {
            viewModelScope.launch {
                val verifySuccess = appContainer.userManager.checkVerifyToken(token)
                if (!verifySuccess) {
                    // if the user is already verified, take them to the main page
                    if (appContainer.userManager.getCurrentUser()?.confirmed == true) {
                        _uiState.update {
                            it.copy(
                                currentPage = Screen.MAIN
                            )
                        }
                    }
                }
            }
        }
    }

    fun joinGroupDiscovery(id: String?) {
        if (id != null) {
            viewModelScope.launch {
                checkLoginSuspend()
                Log.d("avr#mvm", "login checked")
                navigator?.navigate(Screen.JOIN_GROUP_DISCOVERY, id)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                MainViewModel(appContainer = appCtr, application = application)
            }
        }
    }
}
