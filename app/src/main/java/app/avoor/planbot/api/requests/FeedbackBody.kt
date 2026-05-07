package app.avoor.planbot.api.requests

data class FeedbackBody(
    val id: String,
    val reason: String? = null
)