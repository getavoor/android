package app.avoor.planbot.ui.viewmodel

data class NuxEndState(
    val eventState: NuxEndNearestEventState = NuxEndNearestEventState.FAR,
    val eventName: String = ""
)