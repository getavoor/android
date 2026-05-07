package app.avoor.planbot.ui.viewmodel

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.domain.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException


fun isValidEmail(target: String): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

class LoginViewModel (
    val userManager: UserManager
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginViewState())
    val uiState: StateFlow<LoginViewState> = _uiState.asStateFlow()

    fun setEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                enableLogin = shouldEnableLogin()
            )
        }
    }

    private fun shouldEnableLogin(): Boolean {
        return (
            // email must be valid
            isValidEmail(uiState.value.email) &&
            // the password must have at least 8 characters
            // detailed verification was done during registration so this should be OK
            uiState.value.password.length >= 8
        )
    }

    fun setPassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                enableLogin = shouldEnableLogin()
            )
        }
    }

    fun toggleShowPassword() {
        _uiState.update {
            it.copy(
                showPassword = !_uiState.value.showPassword
            )
        }
    }

    fun logIn() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    progress = true,
                    showConnectionError = false
                )
            }
            try {
                val resp = userManager.logIn(
                    _uiState.value.email,
                    _uiState.value.password
                )
                if (resp != 200) {
                    _uiState.update {
                        it.copy(
                            invalidCredentials = true
                        )
                    }
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
            _uiState.update {
                it.copy(
                    progress = false
                )
            }
        }
    }

    fun switchToRegister() {
        viewModelScope.launch {
            userManager.showSignUp()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                LoginViewModel(
                    userManager = appCtr.userManager
                )
            }
        }
    }
}