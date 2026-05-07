package app.avoor.planbot.ui.viewmodel

data class StreakState(
    val currentStreak: Int = 0,
    val freezeCount: Int = 0,
    val longestStreak: Int = 0,
    val updatedToday: Boolean = false,
    val freezeUsedToday: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showExplainer: Boolean = false
)
