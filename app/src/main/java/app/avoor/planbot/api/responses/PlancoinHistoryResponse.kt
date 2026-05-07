package app.avoor.planbot.api.responses

import app.avoor.planbot.api.models.PlancoinTransaction

/**
 * A response containing plancoin transaction history.
 */
data class PlancoinHistoryResponse(
    /**
     * The list of transactions.
     */
    val transactions: List<PlancoinTransaction>
)
