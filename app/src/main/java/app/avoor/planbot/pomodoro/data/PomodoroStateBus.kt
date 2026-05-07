package app.avoor.planbot.pomodoro.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PomodoroStateBus {
    // Using SharedFlow for broadcasting events to multiple consumers
    private val _events = MutableSharedFlow<PomodoroState>(replay = 0) // No replay history
    val events: SharedFlow<PomodoroState> = _events.asSharedFlow()

    // Function to emit an event
    suspend fun emit(event: PomodoroState) {
        _events.emit(event)
    }

    // Function to collect events
    suspend fun collectEvents(action: suspend (PomodoroState) -> Unit) {
        events.collect { event ->
            action(event)
        }
    }
}