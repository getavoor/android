package app.avoor.planbot.api.models

enum class PlancoinReason {
    /**
     * This transaction was caused by a focus session.
     */
    FOCUS,
    /**
     * This transaction was caused by a reward.
     */
    REWARD,
    /**
     * This transaction was caused by a promotional offer.
     */
    PROMOTION,
    /**
     * This transaction was created by support staff.
     */
    SUPPORT,
    /**
     * This transaction was caused by a fuzz session.
     */
    FUZZ,

    /**
     * Reserved for future use.
     */
    RESERVED_A,
    /**
     * Reserved for future use.
     */
    RESERVED_B,
    /**
     * Reserved for future use.
     */
    RESERVED_C
}