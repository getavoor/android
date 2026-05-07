package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.api.models.User
import app.avoor.planbot.ui.AppAction

data class MainState(
    /**
     * Is an action currently in progress?
     */
    val progress: Boolean = false,
    /**
     * The current screen.
     */
    val currentPage: Screen = Screen.NONE,
    /**
     * The current user.
     */
    val user: User? = null,
    /**
     * An action that has failed.
     *
     * If not null, a message informing the user that the server is unavailable will be shown.
     */
    val failedAction: AppAction? = null,
    /**
     * Should a connection error message be shown?
     */
    val showConnectionError: Boolean = false,
    /**
     * Should an "update Planbot" error message be shown?
     */
    val showUpdateError: Boolean = false,
    val initialLoginDone: Boolean = false
)
