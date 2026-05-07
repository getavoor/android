package app.avoor.planbot.api.responses

/**
 * A response received when adding plancoins.
 */
data class AddPlancoinsResponse(
    /**
     * A message returned from the server.
     */
    val msg: String,
    /**
     * The new plancoin balance.
     */
    val plancoins: Int
)
