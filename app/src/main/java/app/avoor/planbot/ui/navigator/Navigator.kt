package app.avoor.planbot.ui.navigator

import app.avoor.planbot.ui.viewmodel.Screen

/**
 * Navigate between app screens.
 */
interface Navigator {

    val currentScreen: String?

    /**
     * Navigate to a screen.
     */
    fun navigate(screen: Screen, ignoreCleanup: Boolean = false)

    /**
     * Navigate to a screen with an argument.
     */
    fun navigate(screen: Screen, argument: String, ignoreCleanup: Boolean = false)

    /**
     * Navigate to a screen and erase all past history.
     */
    fun override(screen: Screen)

    /**
     * Replace the current screen with another.
     */
    fun replace(screen: Screen)

    /**
     * Returns to a screen.
     *
     * @return if the user was returned to another screen.
     */
    fun returnTo(screen: Screen): Boolean

    /**
     * Attempts to return to a screen, and navigates to it if the user couldn't be
     * returned to it.
     */
    fun returnOrNavigate(screen: Screen) {
        if (!returnTo(screen)) {
            navigate(screen)
        }
    }

    /**
     * Go back.
     */
    fun goBack()
}