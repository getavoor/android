package app.avoor.planbot.ui.navigator

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import app.avoor.planbot.ui.viewmodel.Screen

class MainNavigator : Navigator {

    private var navController: NavHostController? = null

    // Temporary screens
    private val TEMPORARY_SCREENS = arrayOf(
        Screen.NONE.toString(),
        Screen.LOGIN.toString(),
        Screen.REGISTER.toString(),
        Screen.VERIFY_COMPLETE.toString(),
        Screen.AUTO_LOGIN.toString()
    )

    fun setController(controller: NavHostController) {
        navController = controller
    }

    override val currentScreen: String?
        get() = navController?.currentBackStackEntry?.destination?.route

    override fun navigate(screen: Screen, ignoreCleanup: Boolean) {
        navController?.navigate(screen.toString()) {
            if (!ignoreCleanup) removeTemporaryScreens()
        }
    }

    private fun NavOptionsBuilder.removeTemporaryScreens() {
        // For each screen:
        for (screen in TEMPORARY_SCREENS) {
            // If it's the current screen, remove it
            if (navController?.currentBackStackEntry?.destination?.route == screen) {
                popUpTo(screen) {
                    inclusive = true
                }
            }
        }
    }

    override fun navigate(screen: Screen, argument: String, ignoreCleanup: Boolean) {
        navController?.navigate("$screen/$argument") {
            if (!ignoreCleanup) removeTemporaryScreens()
        }
    }

    override fun override(screen: Screen) {
        navController?.navigateAndClean(screen.toString())
    }

    override fun replace(screen: Screen) {
        navController?.navigate(screen.toString()) {
            // Remove everything up to and including the current screen
            // The requested screen will be the only one in the back history
            val currentScreen = navController?.currentBackStackEntry?.destination?.route
            if (currentScreen != null) {
                popUpTo(currentScreen) {
                    inclusive = true
                }
            }
        }
    }

    override fun returnTo(screen: Screen): Boolean {
        return navController?.popBackStack(screen.toString(), false) ?: false
    }

    override fun goBack() {
        navController?.navigateUp()
    }
}

fun NavHostController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(0) { inclusive = true }
    }
    graph.setStartDestination(route)
}