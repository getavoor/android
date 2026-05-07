package app.avoor.planbot.ui.viewmodel

import coil.request.ImageRequest

data class DiscoveryState(
    val showResult: Boolean = false,
    val showError: Boolean = false,
    val needsInit: Boolean = true,
    val compatible: Boolean = true,
    val splash: String? = null,
    val imageRequest: ImageRequest? = null
)