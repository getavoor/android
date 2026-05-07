package app.avoor.planbot.api.requests

/**
 * Request body for adding plancoins.
 */
data class AddPlancoinsBody(
    /**
     * The amount of plancoins to add.
     */
    val amount: Int,
    /**
     * The reason for adding plancoins.
     */
    val reason: String? = null
)
