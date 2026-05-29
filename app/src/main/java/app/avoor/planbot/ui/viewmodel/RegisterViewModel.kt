package app.avoor.planbot.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.R
import app.avoor.planbot.domain.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.regex.Pattern

class RegisterViewModel (
    val userManager: UserManager
): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun setName(name: String) {
        _uiState.update {
            it.copy(
                name = name
            )
        }
    }
    fun setEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailValid = isValidEmail(email)
            )
        }
    }
    fun setPassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = validatePassword(password)
            )
        }
    }

    private fun validatePassword(password: String): Int? {
        // If the password is empty, return nothing
        if (password.isEmpty()) return null

        // Check the length
        if (password.length < 8) return R.string.login_password_valid_len
        Log.d("avr#rvm", UPPER_REGEX.matcher(password).find().toString())
        // Check uppercase and lowercase letters
        if (!UPPER_REGEX.matcher(password).find()) return R.string.login_password_valid_upper
        if (!LOWER_REGEX.matcher(password).find()) return R.string.login_password_valid_lower
        // Check digits
        if (!DIGIT_REGEX.matcher(password).find()) return R.string.login_password_valid_digit
        // Check symbols
        if (!SYMBOL_REGEX.matcher(password).find()) return R.string.login_password_valid_symbol

        // If all checks pass, return no error
        return null
    }

    fun setConfirmPassword(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                passwordsMatch = confirmPassword == uiState.value.password
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

    fun toggleShowConfirmPassword() {
        _uiState.update {
            it.copy(
                showConfirmPassword = !_uiState.value.showConfirmPassword
            )
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    progress = true,
                    showConnectionError = false,
                    showActionError = false
                )
            }
            try {
                userManager.register(
                    _uiState.value.name,
                    _uiState.value.email,
                    _uiState.value.password
                )
            }
            // if the app can't connect to the server, show a connection error
            catch(e: SocketTimeoutException) {
                _uiState.update {
                    it.copy(
                        showConnectionError = true
                    )
                }
            }
            // if the server returns an error, show an action error
            catch (e: HttpException) {
                // HTTP 400 is returned if the account already exists
                // (assuming that the client is not at fault)
                if (e.code() == 400) {
                    _uiState.update {
                        it.copy(
                            accountExists = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            showActionError = true
                        )
                    }
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
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                RegisterViewModel(
                    userManager = appCtr.userManager
                )
            }
        }

        val DIGIT_REGEX = Pattern.compile("\\d")
        val UPPER_REGEX = Pattern.compile("[A-Z]")
        val LOWER_REGEX = Pattern.compile("[a-z]")
        val SYMBOL_REGEX = Pattern.compile("\\W")
    }
}