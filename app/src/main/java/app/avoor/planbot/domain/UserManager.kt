package app.avoor.planbot.domain

import android.util.Log
import app.avoor.planbot.api.models.UserWithoutTokens
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.prefs.PreferenceStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

sealed class LoginState {
    data object AutoLoginInProgress : LoginState()
    data object NuxRequired : LoginState()
    data object LoginRequired : LoginState()
    data object LoggedIn : LoginState()
    data object ConnectionError: LoginState()
    data object SignUp: LoginState()
    data object VerificationRequired: LoginState()
    data object VerificationInProgress: LoginState()
    data object VerificationComplete: LoginState()
    data object VerificationFailed: LoginState()
    data object AutoLoginConnectionError: LoginState()
}

/**
 * Performs login and manages the User object.
 */
class UserManager(
    private val preferenceStore: PreferenceStore,
    private val authTokenProvider: AuthTokenProvider,
    private val apiRepository: PlanbotApiRepository,
    // TODO maybe split this into another migration class
    private val oldPreferenceStore: PreferenceStore? = null
) {
    private val _loginState = MutableSharedFlow<LoginState>()
    val loginState = _loginState.asSharedFlow()

    private var user: UserWithoutTokens? = null

    suspend fun checkLogin(): Boolean {
        // Check if there is a refresh token
        // For this the encoded and encrypted representation is enough
        if (preferenceStore.getRefreshToken() == null) {
            // Inform the UI that the user should go through NUX
            Log.d("avr#um", "No login!")
            _loginState.emit(LoginState.NuxRequired)
            return false
        }
        // If there is no access token (but there is a refresh token), do auto login
        else if (authTokenProvider.getAccessToken() == null) {
            return autoLogin()
        }
        // If the access token is expired, do auto login again
        else if (tokenIsExpired()) {
            // Remove the expired token first
            authTokenProvider.deleteAccessToken()
            return autoLogin()
        }
        // Otherwise the user is signed in
        _loginState.emit(LoginState.LoggedIn)
        return true
    }

    private suspend fun tokenIsExpired(): Boolean {
        // Try to get the current user.
        try {
            apiRepository.loginUsingAccessToken()
            return false
        }
        // If the server returns an error, the token has expired.
        catch (e: HttpException) {
            return true
        }
    }

    suspend fun migrateIntoOwnPreferenceStore() {
        // make sure that both stores are passed
        if (oldPreferenceStore == null) return

        // migrate the refresh token
        val rt = oldPreferenceStore.getRefreshToken()
        if (rt != null && preferenceStore.getRefreshToken() == null) {
            Log.d("avr#um", "migrating rt...")
            preferenceStore.setRefreshToken(rt)
        }
        // migrate the IV
        val iv = oldPreferenceStore.getIV()
        if (iv != null && preferenceStore.getIV() == null) {
            Log.d("avr#um", "migrating iv...")
            preferenceStore.setIV(iv)
        }
    }

    suspend fun autoLogin(): Boolean {
        // Try to migrate first
        migrateIntoOwnPreferenceStore()
        // Show the auto login page
        Log.d("avr#auth", "doing auto login")
        _loginState.emit(LoginState.AutoLoginInProgress)
        // Ask the repository to obtain an access token
        try {
            val accessToken = apiRepository.obtainAccessToken()
            // Provide it to the auth token provider
            authTokenProvider.setAccessToken(accessToken)
        }
        // Catch any HTTP error
        catch (ex: HttpException) {
            Log.d("avr#um", ex.message ?: "no message")
            // Error code 422 seems to indicate a mismatch between the secret
            // key used to generate the token and the key used to validate it
            // Ask the user to sign in again to obtain a new refresh token
            if (ex.code() == 422) {
                Log.d("avr#um", "Server rejected saved token!")
                _loginState.emit(LoginState.LoginRequired)
            }
            // Show any other error as a connection error
            else {
                Log.d("avr#um", "Server error during auto login: ${ex.code()}")
                _loginState.emit(LoginState.AutoLoginConnectionError)
            }
            return false  // Always return false on any HTTP error
        }
        // Detect if the server is down
        catch (e: ConnectException) {
            Log.d("avr#um", "Server is unavailable!")
            _loginState.emit(LoginState.AutoLoginConnectionError)
            return false
        }
        catch (e: SocketTimeoutException) {
            Log.d("avr#um", "Server is unavailable!")
            _loginState.emit(LoginState.AutoLoginConnectionError)
            return false
        }
        // Sign in using the access token
        try {
            val user = apiRepository.loginUsingAccessToken()
            // Save the user for short term access
            this.user = user.removeTokens()
            // Emit a successful state
            _loginState.emit(LoginState.LoggedIn)
        } catch (ex: HttpException) {
            // if the server returned error 400, the user has been deleted
            if (ex.code() == 400) {
                // delete the refresh token
                preferenceStore.deleteRefreshToken()
                // emit a state to return to the nux
                _loginState.emit(LoginState.NuxRequired)
            }
            // otherwise, this is an unknown error, log it and show an error message
            else {
                Log.w("avr#um", "failed to login using access token: ${ex.message} ${ex.code()}")
                _loginState.emit(LoginState.AutoLoginConnectionError)
            }
            return false
        }
        return true
    }

    fun getCurrentUser(): UserWithoutTokens? {
        return user
    }

    /**
     * Logs the user in.
     *
     * @param email the email.
     * @param password the password.
     *
     * @return an HTTP response code, or 0 in case of an unknown error.
     */
    suspend fun logIn(email: String, password: String): Int {
        try {
            // Ask the repository to log in
            val user = apiRepository.login(email, password)
            // Provide the tokens to the token provider
            Log.d("avr#um", "authed!")
            authTokenProvider.setAccessToken(user.accessToken)
            authTokenProvider.setRefreshToken(user.refreshToken)
            // Save the user for short term access
            this.user = user.removeTokens()
            Log.d("avr#um", "updated user")
            // Emit a successful state
            _loginState.emit(LoginState.LoggedIn)
            Log.d("avr#um", "done")
            return 200
        }
        // Detect if the server is down
        catch (e: ConnectException) {
            Log.d("avr#um", "Server is unavailable!")
            _loginState.emit(LoginState.ConnectionError)
        }
        // Detect server errors
        catch (e: HttpException) {
            e.message?.let { Log.d("avr#um", it) }
            if (e.message?.startsWith("HTTP 500") == true) {
                Log.d("avr#um", "Internal server error!")
                _loginState.emit(LoginState.ConnectionError)
            }
            return e.code()
        }
        return 0
    }

    /**
     * Logs the user in.
     *
     * @param email the email.
     * @param password the password.
     *
     * @return if the operation was successful.
     */
    suspend fun register(name: String, email: String, password: String): Boolean {
        try {
            // Ask the repository to log in
            val resp = apiRepository.signup(email, password, name)
            // If tokens were given:
            if (resp.refreshToken != null && resp.accessToken != null) {
                // Provide the tokens to the token provider
                Log.d("avr#um", "authed!")
                authTokenProvider.setAccessToken(resp.accessToken)
                authTokenProvider.setRefreshToken(resp.refreshToken)
                // Emit a state asking for verification
                _loginState.emit(LoginState.VerificationRequired)
                Log.d("avr#um", "done")
                return true
            }
        }
        // Detect if the server is down
        catch (e: ConnectException) {
            Log.d("avr#um", "Server is unavailable!")
            _loginState.emit(LoginState.ConnectionError)
        }
        return false
    }

    suspend fun signOut() {
        // Delete both tokens
        authTokenProvider.deleteTokens()
        Log.d("avr#um", "deleted tokens")
        // Clear the user variable
        this.user = null
        Log.d("avr#um", "cleared user")
        // Tell listeners that the user has logged out
        _loginState.emit(LoginState.NuxRequired)
        Log.d("avr#um", "done")
    }

    suspend fun showSignUp() {
       _loginState.emit(LoginState.SignUp)
    }

    suspend fun checkVerifyToken(token: String): Boolean {
        // First, try to sign in using an access token
        // Even unverified accounts are stored by the server, and an access token
        // is required to access the token endpoint
        Log.d("avr#um", "signing in with xt...")
        autoLogin()
        Log.d("avr#um", "ok")
        // Update the login state
        _loginState.emit(LoginState.VerificationInProgress)
        Log.d("avr#um", "emitted")
        try {
            // Ask the repository to confirm the user
            val resp = apiRepository.verifyEmail(token)

            if (resp.user != null) {
                // Provide the tokens to the token provider
                Log.d("avr#um", "verified!")
                // Save the user for short term access
                this.user = resp.user
                Log.d("avr#um", "updated user")
                // Emit a successful state
                _loginState.emit(LoginState.VerificationComplete)
                Log.d("avr#um", "done")
                return true
            } else {
                Log.d("avr#um", resp.msg)
                _loginState.emit(LoginState.VerificationFailed)
                return false
            }
        }
        // Detect if the server is down
        catch (e: ConnectException) {
            Log.d("avr#um", "Server is unavailable!")
            _loginState.emit(LoginState.ConnectionError)
        }
        return false
    }

}