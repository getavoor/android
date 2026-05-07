package app.avoor.planbot.data.calendar

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * A calendar event in local time.
 *
 * It is not recommended to store data using this class;
 * use [CalendarEvent] instead as it is timezone agnostic.
 * This class should be used to display data to the user.
 *
 * In this case, "event" means anything stored in a calendar,
 * including both time blocks and, for example, appointments.
 */
data class LocalCalendarEvent(
    /**
     * A unique ID that identifies this event.
     */
    val id: String,
    /**
     * The name of this event.
     */
    val name: String,
    /**
     * The starting date and time.
     */
    val startDate: LocalDateTime,
    /**
     * The ending date and time.
     */
    val endDate: LocalDateTime,
    /**
     * Is this event a break?
     */
    val isBreak: Boolean = false,
    /**
     * Is this event a gap?
     *
     * This should be rendered as a _literal gap_, i.e. nothing; if you're looking
     * for a _planned_ gap between time blocks, you should instead use a break, i.e.
     * an event with [isBreak] set to true.
     */
    val isGap: Boolean = false
) {
    fun toCalendarEvent(timeZone: TimeZone): CalendarEvent {
        return CalendarEvent(
            this.id,
            this.name,
            this.startDate.toInstant(timeZone),
            this.endDate.toInstant(timeZone),
            this.isBreak,
            this.isGap
        )
    }
}

fun CalendarEvent.toLocal(timeZone: TimeZone): LocalCalendarEvent {
    return LocalCalendarEvent(
        this.id,
        this.name,
        this.startDate.toLocalDateTime(timeZone),
        this.endDate.toLocalDateTime(timeZone),
        this.isBreak,
        this.isGap
    )
}