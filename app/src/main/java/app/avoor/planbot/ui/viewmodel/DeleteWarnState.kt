package app.avoor.planbot.ui.viewmodel

data class DeleteWarnState(
    val success: Boolean = false,
    val showConnectionError: Boolean = false,
    val showActionError: Boolean = false,
    val progress: Boolean = false
)