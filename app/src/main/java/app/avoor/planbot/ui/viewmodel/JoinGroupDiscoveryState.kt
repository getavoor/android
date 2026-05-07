package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.data.discovery.DiscoveryUser
import app.avoor.planbot.data.discovery.GroupDiscoverySession

data class JoinGroupDiscoveryState(
    val groupID: String? = null,
    val compatible: Boolean = true,
    val showActionError: Boolean = false,
    val showLinkError: Boolean = false,
    val approved: Boolean = false,
    val members: List<DiscoveryUser> = listOf(),
    val group: GroupDiscoverySession? = null
)