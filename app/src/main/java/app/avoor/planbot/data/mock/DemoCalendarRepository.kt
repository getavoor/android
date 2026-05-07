package app.avoor.planbot.data.mock

import app.avoor.planbot.data.calendar.CalendarEvent
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.data.calendar.LocalCalendarEvent
import app.avoor.planbot.data.calendar.toLocal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Instant

class DemoCalendarRepository: CalendarRepository {

    private var _plan: List<CalendarEvent>? = null

    private fun generatePlan(): List<CalendarEvent> {
        val systemTZ = TimeZone.currentSystemDefault()
        val now: LocalDateTime = Clock.System.now().toLocalDateTime(systemTZ)
        val sod = now.date.atStartOfDayIn(systemTZ)
        return listOf(
            CalendarEvent(
                id = generateEventID(),
                name = "Get ready",
                startDate = sod.plus(
                    7,
                    DateTimeUnit.HOUR
                ),
                endDate = sod.plus(
                    8,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                )
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "Calculus revision",
                startDate = sod.plus(
                    8,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                ),
                endDate = sod.plus(
                    10,
                    DateTimeUnit.HOUR
                )
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "Probability theory",
                startDate = sod.plus(
                    10,
                    DateTimeUnit.HOUR
                ),
                endDate = sod.plus(
                    12,
                    DateTimeUnit.HOUR
                )
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "Break",
                startDate = sod.plus(
                    12,
                    DateTimeUnit.HOUR
                ),
                endDate = sod.plus(
                    12,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                ),
                isBreak = true
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "English",
                startDate = sod.plus(
                    12,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                ),
                endDate = sod.plus(
                    13,
                    DateTimeUnit.HOUR
                )
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "French",
                startDate = sod.plus(
                    14,
                    DateTimeUnit.HOUR
                ),
                endDate = sod.plus(
                    15,
                    DateTimeUnit.HOUR
                )
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "Develop Avoor update",
                startDate = sod.plus(
                    15,
                    DateTimeUnit.HOUR
                ),
                endDate = sod.plus(
                    16,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                )
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "Drawing",
                startDate = sod.plus(
                    16,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                ),
                endDate = sod.plus(
                    18,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                )
            ),
            CalendarEvent(
                id = generateEventID(),
                name = "Dinner",
                startDate = sod.plus(
                    18,
                    DateTimeUnit.HOUR
                ).plus(
                    30,
                    DateTimeUnit.MINUTE
                ),
                endDate = sod.plus(
                    19,
                    DateTimeUnit.HOUR
                ),
                isBreak = true
            )
        )
    }

    private fun generateEventID(): String = "demo-" + UUID.randomUUID().toString()

    override suspend fun getEvents(): List<CalendarEvent> {
        if (_plan == null) {
            val gen = generatePlan()
            _plan = gen
            return gen
        } else {
            return _plan ?: generatePlan()
        }
    }

    override suspend fun getNearestEvent(maxSecondsAway: Long): CalendarEvent? {
        TODO("Not yet implemented")
    }

    override suspend fun startGap(): LocalCalendarEvent? {
        // load the first event
        // in a real repository this would be another direct call,
        // but here getEvents also generates the plan
        val plan = getEvents()[0]
        // find the start of the day
        val now: LocalDateTime = Clock.System.now().toLocalDateTime(systemTZ)
        val sod = now.date.atStartOfDayIn(systemTZ)
        // return it as a local event
        return CalendarEvent(
            "_startgap",
            "_startgap",
            sod,
            plan.startDate,
            true
        ).toLocal(systemTZ)
    }

    override fun getTimeZone(): TimeZone {
        return systemTZ
    }

    override fun getCurrentEvent(): Flow<LocalCalendarEvent?> {
        // high cortisol :sob:
        return flow {
            emit(CalendarEvent(
                generateEventID(),
                "current event ",
                Clock.System.now(),
                Clock.System.now().plus(30, DateTimeUnit.MINUTE),
                true
            ).toLocal(systemTZ))
            delay(5 * 60 * 1000L)
        }
    }

    override fun reloadEvents() {
        // DemoCalendarRepository runs fully in memory, so it doesn't need a cache reset
    }

    override suspend fun findEvents(query: String): List<CalendarEvent> {
        TODO("Not yet implemented")
    }

    override suspend fun addEvent(event: CalendarEvent) {
        TODO("Not yet implemented")
    }

    override suspend fun reschedule(
        event: CalendarEvent,
        start: Instant,
        end: Instant
    ) {
        TODO("Not yet implemented")
    }

    private var systemTZ = TimeZone.currentSystemDefault()

    fun updateTimeZone() {
        systemTZ = TimeZone.currentSystemDefault()
    }

    override suspend fun getLocalEvents(): List<LocalCalendarEvent> {
        return getEvents().map { event -> event.toLocal(systemTZ) }
    }
}