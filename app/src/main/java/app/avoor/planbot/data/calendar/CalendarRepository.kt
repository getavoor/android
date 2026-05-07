package app.avoor.planbot.data.calendar

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/**
 * Repository that provides access to a calendar.
 */
interface CalendarRepository {
    /**
     * Retrieve the user's [CalendarEvent]s.
     */
    suspend fun getEvents(): List<CalendarEvent>

    /**
     * Retrieve the first [CalendarEvent] that starts
     * at least maxSecondsAway seconds in the future.
     */
    suspend fun getNearestEvent(
        maxSecondsAway: Long
    ): CalendarEvent?

    /**
     * Find [CalendarEvent]s.
     */
    suspend fun findEvents(query: String): List<CalendarEvent>

    /**
     * Add a [CalendarEvent] to this calendar.
     *
     * @param event the event to add.
     */
    suspend fun addEvent(event: CalendarEvent)

    /**
     * Reschedule a [CalendarEvent].
     *
     * @param event the event to reschedule.
     * @param start the new start time.
     * @param end the new end time.
     */
    suspend fun reschedule(event: CalendarEvent, start: Instant, end: Instant)

    /**
     * Retrieve the user's [CalendarEvent]s in local time.
     */
    suspend fun getLocalEvents(): List<LocalCalendarEvent>

    /**
     * Generate a special gap event from the start of the day
     * to the start of the first event.
     *
     * Can be null if the calendar is empty.
     */
    suspend fun startGap(): LocalCalendarEvent?

    /**
     * Retrieve the timezone used for local event calculation.
     */
    fun getTimeZone(): TimeZone

    /**
     * Find the current event in the calendar.
     *
     * Can be null if there is no event right now.
     */
    fun getCurrentEvent(): Flow<LocalCalendarEvent?>

    /**
     * Reload the list of events.
     *
     * If this repository holds a cache of events, it should be cleared.
     * Otherwise, this function can be left empty.
     */
    fun reloadEvents()
}