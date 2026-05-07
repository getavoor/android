package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.data.discovery.DiscoveryUser

data class GroupDiscoveryState(
    val link: String? = null,
    val compatible: Boolean = true,
    val showActionError: Boolean = false,
    val requesters: List<DiscoveryUser> = listOf(),
    val members: List<DiscoveryUser> = listOf()
)