package app.avoor.planbot.api.requests

data class EmailPasswordNameBody (
    val email: String,
    val password: String,
    val name: String
)