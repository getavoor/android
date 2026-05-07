package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.api.models.UserWithoutTokens

data class ProfileState (
    val progress: Boolean = false,
    val user: UserWithoutTokens? = null
)

