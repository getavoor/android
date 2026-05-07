package app.avoor.planbot.ui

import androidx.compose.ui.graphics.vector.ImageVector
import app.avoor.planbot.R
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Focus
import app.avoor.symbols.icons.ModeHeat
import app.avoor.symbols.icons.Shuffle

sealed class MainScreenRoute(val route: String, val label: Int, val icon: ImageVector) {
    object Home : MainScreenRoute(
        route = "home", label = R.string.main_nav_home, icon = Icons.Shuffle
    )
    object Profile : MainScreenRoute(
        route = "profile", label = R.string.main_nav_profile, icon = Icons.Shuffle
    )
    object Settings : MainScreenRoute(
        route = "settings", label = R.string.main_nav_settings, icon = Icons.Shuffle
    )
    object TimelineTest : MainScreenRoute(
        route = "timeline_test", label = R.string.btn_approve, icon = Icons.Shuffle
    )
    object PomodoroTest : MainScreenRoute(
        route = "pomo_test", label = R.string.btn_approve, icon = Icons.Focus
    )
    object Streak : MainScreenRoute(
        route = "streak", label = R.string.streak, icon = Icons.ModeHeat
    )
}