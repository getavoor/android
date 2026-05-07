package app.avoor.planbot.ui.viewmodel

import app.avoor.planbot.data.calendar.LocalCalendarEvent

data class CalendarViewState(
    val events: List<LocalCalendarEvent>? = null
)
