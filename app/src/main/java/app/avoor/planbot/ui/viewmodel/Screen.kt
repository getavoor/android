package app.avoor.planbot.ui.viewmodel

enum class Screen {
    /**
     * No screen.
     */
    NONE,

    /**
     * Start of NUX.
     */
    NUX_START,

    /**
     * Login screen.
     */
    LOGIN,

    /**
     * Automatic login screen.
     */
    AUTO_LOGIN,

    /**
     * Permission request screen.
     */
    NUX_PERMISSION,

    /**
     * Notification permission request screen (Android 13)+.
     */
    NUX_NOTIFY_PERMISSION,

    /**
     * Streak explainer screen.
     */
    NUX_STREAK,

    /**
     * Plancoin explainer screen.
     */
    NUX_PLANCOIN,

    /**
     * End of the NUX.
     */
    NUX_END,

    /**
     * Main screen.
     */
    MAIN,

    /**
     * Discovery screen.
     */
    // TODO to become planbot chat screen
    DISCOVERY,

    /**
     * Discovery customizer.
     */
    // TODO to become planbot chat screen
    DISCOVERY_CUSTOM,

    /**
     * Group discovery screen.
     */
    DISCOVERY_GROUP,

    /**
     * Registration screen.
     */
    REGISTER,

    /**
     * A screen that tells users that they should check their email for a confirmation link.
     */
    VERIFY,

    /**
     * A screen shown when the account is successfully confirmed.
     */
    VERIFY_COMPLETE,
    VERIFY_WAIT,

    /**
     * Development screen for sending raw messages to the discovery mode socket.
     */
    DEV_MESSENGER,
    JOIN_GROUP_DISCOVERY,

    /**
     * A screen that asks users for feedback before deleting their account.
     */
    DELETE_FEEDBACK,

    /**
     * A screen that warns users of the consequences of deleting their account.
     */
    DELETE_WARN,

    TIMELINE_TEST,

    /**
     * Timer screen.
     */
    TIMER,

    /**
     * Streak screen showing current streak and freeze count.
     */
    STREAK,

    /**
     * Plancoin screen.
     */
    PLANCOIN,

    /**
     * Settings screen.
     */
    SETTINGS,

    /**
     * Screen that shuffles events.
     */
    SHUFFLE_PREP
}
