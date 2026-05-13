package app.avoor.planbot.data.calendar

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.util.Log
import app.avoor.planbot.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val projection = arrayOf(
    CalendarContract.Instances.EVENT_ID,
    CalendarContract.Instances.TITLE,
    CalendarContract.Instances.BEGIN,
    CalendarContract.Instances.END,
    CalendarContract.Instances.RRULE
)

@Suppress("unused")
const val PROJECTION_EVENT_ID = 0
const val PROJECTION_EVENT_TITLE = 1
const val PROJECTION_EVENT_BEGIN = 2
const val PROJECTION_EVENT_END = 3
@Suppress("unused")
const val PROJECTION_EVENT_RRULE = 4

/**
 * maximum calendar cache age in milliseconds
 */
const val CALENDAR_CACHE_MAX_AGE = 5 * 60 * 1000L // 5 minutes

/**
 * Calendar repository using Android ContentProvider.
 */
class AndroidCalendarRepository(
    val contentResolver: ContentResolver,
    val scope: CoroutineScope
): CalendarRepository {

    private var systemTZ = TimeZone.currentSystemDefault()

    private var calendarCache: List<CalendarEvent> = listOf()
    private var calendarCacheUpdated: Long = 0

    // used to force _currentEvent to recheck the calendar repo

    private val _forceRecheck = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private fun queryEvents(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        startFromSOD: Boolean = false,
        upperBound: Long = 86_400_000, // 1 day
        onlyOne: Boolean = false,
        shorten: Boolean = true
    ): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val startMillis =
            if (!startFromSOD) System.currentTimeMillis()
            else Clock.System.now().toLocalDateTime(systemTZ).date.atStartOfDayIn(systemTZ).toEpochMilliseconds()
        val endMillis = startMillis + upperBound
        // Build URI with time bounds
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)
        val cursor = contentResolver.query(
            builder.build(),
            projection,
            selection,
            selectionArgs,
            "${CalendarContract.Instances.BEGIN} ASC"
        )
        if (cursor == null) {
            Log.d("avr#acr", "cursor is null")
        }
        cursor?.apply {
            Log.d("avr#acr", "cursor obtained")
            /*
             * Moves to the next row in the cursor. Before the first movement in the cursor, the
             * "row pointer" is -1, and if you try to retrieve data at that position you get an
             * exception.
             */
            while (moveToNext()) {
                // obtain info about event from android
                val _startDate = getLong(PROJECTION_EVENT_BEGIN)
                val endDate = getLong(PROJECTION_EVENT_END)
                val title = getString(PROJECTION_EVENT_TITLE)
                val id = getString(PROJECTION_EVENT_ID)

                // reduce the start date to the start time if needed
                val startDate = if (shorten) {
                    maxOf(_startDate, startMillis)
                } else _startDate

                if (BuildConfig.DEBUG) {
                    Log.d(
                        "avr#acr",
                        "$title: $startDate - $endDate startMillis = $startMillis len=${endDate - startDate} lsh=${startMillis - startDate}"
                    )
                }
                // ignore all day events by checking if they last 24 hours or more
                // and ignore events that have a length of zero
                if (endDate - startDate < 86_400_000 && endDate != startDate) {
                    // add this event to the events list
                    events.add(
                        CalendarEvent(
                            id = id,
                            name = title,
                            startDate = Instant.fromEpochMilliseconds(startDate),
                            endDate = Instant.fromEpochMilliseconds(endDate)
                        )
                    )
                    // if only one event should be returned, stop the loop
                    if (onlyOne) {
                        break
                    }
                }
            }
        }
        cursor?.close()
        // return the list
        return events.toList()
    }

    override suspend fun getEvents(): List<CalendarEvent> {
        // get the current time
        val startMillis = System.currentTimeMillis()
        // if the cache is too old, update it
        if (startMillis - calendarCacheUpdated > CALENDAR_CACHE_MAX_AGE) {
            calendarCache = queryEvents(startFromSOD = true)
            calendarCacheUpdated = startMillis
        }
        return calendarCache
    }

    override suspend fun getNearestEvent(maxSecondsAway: Long): CalendarEvent? {
        val now = Clock.System.now()
        return findFirstEvent(
            start = now,
            maxTime = maxSecondsAway,
            nowOnly = false
        )
    }

    override suspend fun findEvents(query: String): List<CalendarEvent> {
        return queryEvents(
            selection = "(${CalendarContract.Instances.TITLE} LIKE ?)",
            selectionArgs = arrayOf(query)
        )
    }

    override suspend fun addEvent(event: CalendarEvent) {
        val values = ContentValues()
        Log.d("avr#acr", "acr creating event: ${event.name}")

        // add the title
        values.put(Events.TITLE, event.name)

        // add the date
        values.put(Events.DTSTART, event.startDate.epochSeconds)
        // CalendarEvent assumes UTC
        values.put(Events.EVENT_TIMEZONE, "UTC")

        // use the default calendar
        values.put(Events.CALENDAR_ID, 1)

        // get the difference and set it as the duration
        val diff = event.endDate - event.startDate
        values.put(Events.DURATION, diff.toIsoString())

        // add notification
        values.put(Events.HAS_ALARM, 1)

        // insert the event
        val url = contentResolver.insert(Events.CONTENT_URI, values)
        Log.d("avr#rta", "INSERTED! $url")
        Log.d("avr#rta", "${event.startDate} - dur=$diff")
    }

    override suspend fun reschedule(
        event: CalendarEvent,
        start: Instant,
        end: Instant
    ) {
        val eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id.toLong())

        // check if it's recurring
        val isRecurring = contentResolver.query(
            eventUri,
            arrayOf(Events.RRULE),
            null, null, null
        )?.use {
            if (it.moveToFirst()) !it.getString(0).isNullOrEmpty() else false
        } ?: false

        if (isRecurring) {
            // fetch the calendar ID from the original event
            val calendarId = contentResolver.query(
                eventUri,
                arrayOf(Events.CALENDAR_ID),
                null, null, null
            )?.use {
                if (it.moveToFirst()) it.getLong(0) else null
            } ?: return  // bail if we can't find the event

            // create values
            val values = ContentValues().apply {
                put(Events.CALENDAR_ID, calendarId)

                // copy the title of the original event
                put(Events.TITLE, event.name)

                // use the new start/end dates
                put(Events.DTSTART, start.toEpochMilliseconds())
                put(Events.DTEND, end.toEpochMilliseconds())
                put(Events.EVENT_TIMEZONE, "UTC")
                put(Events.EVENT_END_TIMEZONE, "UTC")

                // tell the calendar this is an exception to a specific instance
                put(Events.ORIGINAL_ID, event.id)
                put(Events.ORIGINAL_INSTANCE_TIME, event.startDate.toEpochMilliseconds())

                // mark it as an exception (not a new standalone event)
                put(Events.STATUS, Events.STATUS_CONFIRMED)
            }

            // insert as a new event — this creates the exception instance
            contentResolver.insert(Events.CONTENT_URI, values)
        } else {
            // just update the existing event directly
            val values = ContentValues().apply {
                put(Events.DTSTART, start.toEpochMilliseconds())
                put(Events.DTEND, end.toEpochMilliseconds())
                put(Events.EVENT_TIMEZONE, "UTC")
                put(Events.EVENT_END_TIMEZONE, "UTC")
            }
            contentResolver.update(eventUri, values, null, null)
        }
        Log.d("avr#rta", "$start - $end")
    }

    override suspend fun getLocalEvents(): List<LocalCalendarEvent> {
        return getEvents().map { event -> event.toLocal(systemTZ) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun startGap(): LocalCalendarEvent? {
        // find the start of the day
        val now: LocalDateTime = Clock.System.now().toLocalDateTime(systemTZ)
        val sod = now.date.atStartOfDayIn(systemTZ)

        // find the closest event to the start of the day
        val firstEvent = findFirstEvent(sod)?.toLocal(systemTZ)

        // return a corresponding startgap
        if (firstEvent != null)
            return CalendarEvent(
                "sg_" + Uuid.random(),
                "_startgap",
                sod,
                firstEvent.startDate.toInstant(systemTZ),
                isGap = true
            ).toLocal(systemTZ)
        return null
    }

    /**
     * Find the first event between `start` and `start + maxTime`.
     * @param start the Instant from which to start searching for events.
     * @param maxTime specifies the maximum length of an event, in milliseconds (default: 1 day)
     * @param nowOnly if true, events that start before `start` will be ignored (default: true)
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun findFirstEvent(
        start: Instant,
        maxTime: Long = (24L * 60 * 60 * 1000),
        nowOnly: Boolean = true
    ): CalendarEvent? {
        var event: CalendarEvent? = null
        val startMillis = start.toEpochMilliseconds()
        val endMillis = startMillis + maxTime
        // Build URI with time bounds
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)
        val cursor = contentResolver.query(
            builder.build(),
            projection,
            null,
            null,
            "${CalendarContract.Instances.BEGIN} ASC"
        )
        if (cursor == null) {
            Log.d("avr#acr", "cursor is null")
        }
        cursor?.apply {
            Log.d("avr#acr", "cursor obtained")
            if (BuildConfig.DEBUG) {
                Log.d("avr#acr", "${cursor.count} events")
            }
            // iterate until we find the first
            while (moveToNext()) {
                // obtain info about event from android
                val startDate = getLong(PROJECTION_EVENT_BEGIN)
                val endDate = getLong(PROJECTION_EVENT_END)
                val title = getString(PROJECTION_EVENT_TITLE)
                val id = getString(PROJECTION_EVENT_ID)
                if (BuildConfig.DEBUG) {
                    Log.d(
                        "avr#acr",
                        "considering event $title w/ sd $startDate ${start.epochSeconds}"
                    )
                }

                // add the event if:
                if (
                    // it is not all day long,
                    endDate - startDate < 86_400_000 &&
                    // and either nowOnly is false or the event starts on or after the query start time
                    (!nowOnly || startDate >= start.epochSeconds * 1000)
                ) {
                    // create an avoor event
                    event = CalendarEvent(
                        id = id,
                        name = title,
                        startDate = Instant.fromEpochMilliseconds(startDate),
                        endDate = Instant.fromEpochMilliseconds(endDate)
                    )

                    if (BuildConfig.DEBUG) {
                        Log.d(
                            "avr#acr",
                            "found first event $title w/ sd $startDate ${start.epochSeconds}"
                        )
                    }
                    // stop the loop
                    break
                }
            }
        }
        cursor?.close()
        return event
    }

    override fun getTimeZone(): TimeZone {
        return systemTZ
    }

    val MAX_RECHECK_MS = (5 * 60 * 1000L)
    private val _currentEvent: StateFlow<LocalCalendarEvent?> = flow {
        while (true) {
            val now = Clock.System.now()
            val event = findFirstEvent(now, MAX_RECHECK_MS, false)
            // if the event exists and has not passed:
            if (event != null && event.endDate.toEpochMilliseconds() > now.toEpochMilliseconds()) {
                emit(event.toLocal(systemTZ))

                // wait until the event ends, then recheck
                val delayMs = event.endDate.toEpochMilliseconds().minus(now.toEpochMilliseconds())
                if (BuildConfig.DEBUG) {
                    Log.d(
                        "avr#acr",
                        "delayMs=${delayMs} endDate=${event.endDate.toEpochMilliseconds()} now=${now.toEpochMilliseconds()}"
                    )
                }
                delayUnlessForceRecheck(delayMs.coerceAtLeast(1000L))
            } else {
                // if it hasn't, wait for the maximum
                delayUnlessForceRecheck(MAX_RECHECK_MS)
            }
        }
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = scope, // inject this
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override fun getCurrentEvent(): Flow<LocalCalendarEvent?> = _currentEvent
    override fun reloadEvents() {
        _forceRecheck.tryEmit(Unit)
    }

    suspend fun delayUnlessForceRecheck(ms: Long) {
        withTimeoutOrNull(ms) {
            _forceRecheck.first() // suspends until a force recheck event arrives
        }
    }
}