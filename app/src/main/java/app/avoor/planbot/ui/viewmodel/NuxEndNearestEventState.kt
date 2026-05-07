package app.avoor.planbot.ui.viewmodel

enum class NuxEndNearestEventState {
    /**
     * The next event is >30m away.
     */
    FAR,

    /**
     * The next event is <=30m away.
     */
    NEAR,

    /**
     * There is an event now.
     */
    NOW
}