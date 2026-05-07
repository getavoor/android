package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.api.models.FeedbackReason

data class DeleteFeedbackState(
    val reason: FeedbackReason? = null,
    val body: String? = null,
    val showConnectionError: Boolean = false,
    val showActionError: Boolean = false,
    val progress: Boolean = false
)

