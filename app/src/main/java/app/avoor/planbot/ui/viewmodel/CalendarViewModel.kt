package app.avoor.planbot.ui.viewmodel

import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.data.calendar.LocalCalendarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toInstant
import java.util.UUID

class CalendarViewModel(
    val calendarRepository: CalendarRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(CalendarViewState())
    val uiState: StateFlow<CalendarViewState> = _uiState.asStateFlow()

    init {
        updateEvents()
    }

    /**
     * Update the event list from the calendar repository.
     */
    fun updateEvents() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    events = fetchEvents()
                )
            }
        }
    }

    private suspend fun fetchEvents(): List<LocalCalendarEvent> {
        val events = calendarRepository.getLocalEvents()
        // ask the repository to generate the first gap
        val firstGap = calendarRepository.startGap()

        // return value
        val eventsWithGaps = mutableListOf<LocalCalendarEvent>()
        if (firstGap != null) {
            eventsWithGaps.add(firstGap)
        }
        var lastEvent: LocalCalendarEvent? = null
        // add gaps:
        for (event in events) {
            // if the last event is not null and if it doesn't end at the same time
            // the current event starts:
            if (lastEvent != null && lastEvent.endDate != event.startDate) {
                // insert a gap event between the end time of that event
                // and the start time of the current event
                eventsWithGaps.add(LocalCalendarEvent(
                    "_gap_" + UUID.randomUUID().toString(),
                    "_gap",
                    startDate = lastEvent.endDate,
                    endDate = event.startDate,
                    isGap = true
                ))
            }
            // add the new event
            eventsWithGaps.add(event)
            // update the last event
            lastEvent = event
        }
        return eventsWithGaps
    }

    fun getSize(passedHeight: Dp, event: LocalCalendarEvent): Dp {
        val stz = calendarRepository.getTimeZone()
        val start = event.startDate.toInstant(stz)
        val end = event.endDate.toInstant(stz)
        Log.d("avr#cvm", "event ${event.name} lasts ${((end - start).inWholeMinutes)} min")
        return passedHeight * (((end - start).inWholeMinutes)/60f)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                CalendarViewModel(
                    calendarRepository = appCtr.calendarRepository
                )
            }
        }
    }
}