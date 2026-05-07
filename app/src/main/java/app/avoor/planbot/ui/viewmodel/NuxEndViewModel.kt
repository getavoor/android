package app.avoor.planbot.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.calendar.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Clock

class NuxEndViewModel(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuxEndState())
    val uiState: StateFlow<NuxEndState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // fetch an event that is at most 45 minutes away
            val event = repository.getNearestEvent(45 * 60L)
            // if no event was found, it should be too far away
            if (event == null) {
                _uiState.update {
                    it.copy(
                        eventState = NuxEndNearestEventState.FAR
                    )
                }
            } else {
                Log.d("avr#nevm", "found event: ${event.name}")
                // calculate the state based on how far away the event is
                val startDiff = event.startDate.epochSeconds - Clock.System.now().epochSeconds
                _uiState.update {
                    it.copy(
                        eventState = if (startDiff < 0) {
                            NuxEndNearestEventState.NOW
                        } else if (startDiff < 15*60L) {
                            NuxEndNearestEventState.NEAR
                        } else {
                            NuxEndNearestEventState.FAR
                        },
                        eventName = event.name
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                NuxEndViewModel(
                    repository = appCtr.calendarRepository
                )
            }
        }
    }
}
