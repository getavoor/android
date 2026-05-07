package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.api.models.PlancoinReward

data class PlancoinState(
    val plancoins: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showExplainer: Boolean = false,
    val showAddReward: Boolean = false,
    val rewards: List<PlancoinReward>? = null
)