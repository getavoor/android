package app.avoor.planbot

import app.avoor.liboc.ShuffleUtils
import app.avoor.planbot.data.calendar.CalendarRepository

class ShuffleImpl(
    private val calendarRepository: CalendarRepository
) {
    private val shuffleUtils = ShuffleUtils()

    /**
     * Shuffles the user's calendar events.
     */
    suspend fun shuffleEvents() {
        val events = calendarRepository.getEvents()
        if (events.size <= 1) return

        val indices = Array(events.size) { it }
        shuffleUtils.shuffle(indices)

        val shuffledEvents = indices.map { events[it] }
        val timeSlots = events.map { it.startDate to it.endDate }

        for (i in shuffledEvents.indices) {
            val event = shuffledEvents[i]
            val (newStart, newEnd) = timeSlots[i]
            calendarRepository.reschedule(event, newStart, newEnd)
        }

        calendarRepository.reloadEvents()
    }
}