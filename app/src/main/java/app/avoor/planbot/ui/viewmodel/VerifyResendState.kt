package app.avoor.planbot.ui.viewmodel

data class VerifyResendState(
    val showConnectionError: Boolean = false,
    val showActionError: Boolean = false,
    val progress: Boolean = false,
    val success: Boolean = false
)