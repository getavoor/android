package app.avoor.planbot.data.calendar

import kotlin.time.Instant

/**
 * A calendar event.
 *
 * In this case, "event" means anything stored in a calendar,
 * including both time blocks and, for example, appointments.
 */
data class CalendarEvent(
    /**
     * A unique ID that identifies this event.
     */
    val id: String,
    /**
     * The name of this event.
     */
    val name: String,
    /**
     * The starting date and time, represented as a kotlin Instant.
     */
    val startDate: Instant,
    /**
     * The ending date and time, represented as a kotlin Instant.
     */
    val endDate: Instant,
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
    val isGap: Boolean = false,
    /**
     * Can this event be shuffled?
     */
    val canBeShuffled: Boolean = true
)