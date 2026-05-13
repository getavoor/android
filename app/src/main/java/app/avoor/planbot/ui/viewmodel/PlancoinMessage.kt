package app.avoor.planbot.ui.viewmodel

enum class PlancoinMessage {
    /**
     * A reward has been bought.
     */
    REWARD_BOUGHT,
    /**
     * A reward cannot be bought because the user doesn't have enough plancoins.
     */
    REWARD_ERROR_INSUFFICIENT_FUNDS,
    /**
     * A reward has been deleted.
     */
    REWARD_DELETED,
    /**
     * An action requires internet access, but the user is offline.
     */
    NO_INTERNET,
    /**
     * An unspecified error has occurred; see errorDesc.
     */
    GENERAL_ERROR,
}
