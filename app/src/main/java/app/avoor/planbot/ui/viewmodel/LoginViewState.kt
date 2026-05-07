package app.avoor.planbot.ui.viewmodel

data class LoginViewState (
    val email: String = "",
    val password: String = "",
    val enableLogin: Boolean = false,
    val showPassword: Boolean = false,
    val progress: Boolean = false,
    val invalidCredentials: Boolean = false,
    val showConnectionError: Boolean = false
)
