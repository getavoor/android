package app.avoor.planbot.api.models

enum class FeedbackReason(
    val id: String
) {
    BAD_RECOMMENDATIONS ("badRecs"),
    BAD_UI ("badUI"),
    SLOW ("slow"),
    OTHER ("other")
}