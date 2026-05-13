package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.api.models.PlancoinReward

data class PlancoinState(
    val plancoins: Int = 0,
    val isLoading: Boolean = true,
    /**
     * A generic message bus.
     *
     * This is used by [PlancoinViewModel] to inform the UI layer of results of actions.
     */
    val message: PlancoinMessage? = null,
    /**
     * An optional error description. Used primarily when [message] is [PlancoinMessage.GENERAL_ERROR].
     */
    val errorDesc: String? = null,
    val showExplainer: Boolean = false,
    val showAddReward: Boolean = false,
    val rewards: List<PlancoinReward>? = null
)