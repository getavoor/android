package app.avoor.planbot.pomodoro

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.R
import app.avoor.planbot.data.AppDatabase
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.data.prefs.PomodoroPreset
import app.avoor.planbot.domain.PlancoinController
import app.avoor.planbot.pomodoro.data.PomodoroState
import app.avoor.planbot.pomodoro.data.PomodoroStateBus
import app.avoor.planbot.worker.UpdateSyncWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.toInstant
import kotlin.math.min


const val POMODORO_CHANNEL = "pomodoro"

class PomodoroService: LifecycleService() {

    var timerStarted: Boolean = false
        private set

    private var currentTimer: CountDownTimer? = null
    private var manager: NotificationManagerCompat? = null
    private lateinit var calendarRepo: CalendarRepository
    private lateinit var apiRepo: PlanbotApiRepository
    private lateinit var db: AppDatabase
    private lateinit var plancoinController: PlancoinController

    private lateinit var readTaskNameJob: Job
    
    private var currentEventDuration: Long = -1L

    companion object {
        fun applyPreset(preset: PomodoroPreset) {
            TIME_WORK = preset.workTime * 60L
            TIME_BREAK = preset.breakTime * 60L
            PomodoroService.preset = preset
        }

        fun setCustomTime(workTime: Int, breakTime: Int) {
            TIME_WORK = workTime * 60L
            TIME_BREAK = breakTime * 60L
        }

        private const val TAG = "avr#ps"

        var isRegularTimer = false
        private val _isWorkPhase = MutableStateFlow(true)
        var isWorkPhase = _isWorkPhase.asStateFlow()

        var TIME_WORK = 25 * 60L
        var TIME_BREAK = 5 * 60L
        private val _remainingSeconds = MutableStateFlow(-1L)
        val remainingSeconds = _remainingSeconds.asStateFlow()
        var preset: PomodoroPreset = PomodoroPreset.CLASSIC
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        @Suppress("unused")
        fun getService(): PomodoroService = this@PomodoroService
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return LocalBinder()
    }

    /**
     * Create a notification channel for devices running Android 8.0 or higher.
     * A channel groups notifications with similar behavior.
     */
    private fun creatingNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                POMODORO_CHANNEL,
                getString(R.string.notification_channel_pomodoro),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true) // Turn on notification light
                lightColor = Color.GREEN
                enableVibration(true) // Allow vibration for notifications
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // android <13 has no notification permission, so the app
            // can always post notifications on those versions
            return true
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS
        val res: Int = applicationContext.checkCallingOrSelfPermission(permission)
        return (res == PackageManager.PERMISSION_GRANTED)
    }

    fun postNotification(seconds: Long, task: String) {
        val minutes = seconds / 60
        val hours = minutes / 60
        val secs = seconds % 60
        val timeText = if (hours < 1) {
            getString(R.string.timer_notif_left).format(minutes, secs)
        } else {
            getString(R.string.timer_notif_left_hour).format(hours, minutes, secs)
        }

        val stopAction = Intent(this, PomodoroReceiver::class.java).apply {
            action = "STOP_TIMER"
        }
        val stopIntent = PendingIntent.getBroadcast(
            this,
            1,
            stopAction,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build the notification
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, POMODORO_CHANNEL)
            .setShortCriticalText(timeText)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Notification priority for better visibility
            .setOngoing(true) // Makes the notification persistent
            .setOnlyAlertOnce(true) // Only notifies the user once; reduces notification spam
            .setRequestPromotedOngoing(true) // Request live updates
            .addAction(NotificationCompat.Action(
                R.drawable.ic_info,
                getString(R.string.pomodoro_action_stop),
                stopIntent
            ))

        if (_isWorkPhase.value) {
            // in the work phase: title is the timer, body is the name of the task
            builder
                .setContentTitle(timeText)
                .setContentText(getString(R.string.timer_notif_focusing).format(task))
                .setSmallIcon(R.drawable.ic_target)
        } else {
            // in the break phase: title is break time, body is the timer
            builder
                .setContentTitle(getString(R.string.pomodoro_screen_break))
                .setContentText(timeText)
                .setSmallIcon(R.drawable.ic_cafe)
        }

        // Display the notification
        try {
            manager?.notify(1, builder.build())
        } catch (_: SecurityException) {
            Log.d(TAG, "no permission to post pomodoro notification")
        }
    }

    suspend fun manageTimerNotification(repo: CalendarRepository) {
        // combine the repo event flow and the current time flow and collect it
        combine(repo.getCurrentEvent(), _remainingSeconds) { event, seconds ->
            Pair(event, seconds)
        }.collect { (event, seconds) ->
            // post a notification
            if (hasNotificationPermission() && seconds > -1) {
                postNotification(seconds, event?.name ?: getString(R.string.pomodoro_no_event))
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // first, emit STARTING
        lifecycleScope.launch {
            PomodoroStateBus.emit(PomodoroState.STARTING)
        }

        if (hasNotificationPermission()) {
            manager = NotificationManagerCompat.from(this)
            creatingNotificationChannel()
            // post a notification
            val nb = NotificationCompat.Builder(this, POMODORO_CHANNEL)
                .setShortCriticalText(getString(R.string.pomodoro_placeholder_noti))
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Notification priority for better visibility
                .setOngoing(true) // Makes the notification persistent
                .setOnlyAlertOnce(true) // Only notifies the user once; reduces notification spam
                .setRequestPromotedOngoing(true)
            // once the notification has been posted, start the service
            ServiceCompat.startForeground(
                /* service = */ this,
                /* id = */ 1, // Cannot be 0
                /* notification = */ nb.build(),
                /* foregroundServiceType = */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else {
                    0
                },
            )
            // this notification will be replaced with the one posted by manageTimerNotification
        }


        val container = (application as AvoorApplication).container
        calendarRepo = container.calendarRepository
        apiRepo = container.planbotApiRepository
        db = container.database
        plancoinController = container.plancoinController

        if (intent?.action == "STOP_TIMER") {
            Log.d(TAG, "stopping...")
            stopTimer()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
            } else {
                // TODO either test this on android 6 or raise minsdk to 24
                stopSelf()
            }
        } else {
            readTaskNameJob = lifecycleScope.launch {
                manageTimerNotification(calendarRepo)
            }
            startTimer()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun getTime(): Long {
        if (isRegularTimer) {
            // for a regular timer, return the current event duration
            return currentEventDuration
        } else {
            // for a pomodoro timer, get the correct length and return it
            // or the event duration, whichever is smaller
            val dur = if (_isWorkPhase.value) TIME_WORK
            else TIME_BREAK
            // check if the event duration has been initialized first
            return if (currentEventDuration > -1) {
                min(dur, currentEventDuration)
            } else {
                // if it hasn't been initialized, return the raw pomodoro duration
                dur
            }
        }
    }

    fun stopTimer() {
        if (!::plancoinController.isInitialized) return
        lifecycleScope.launch {
            PomodoroStateBus.emit(PomodoroState.STOPPING)
            // if the timer was stopped during a focus session, award plancoins
            if (_isWorkPhase.value) {
                plancoinController.addSpentTimePlancoins(_remainingSeconds.value.toInt())
            }
        }
        currentTimer?.cancel()
        _remainingSeconds.value = -1
        timerStarted = false
    }

    fun startTimer() {
        currentTimer?.cancel()
        timerStarted = true
        Log.d(TAG, "starting timer. irt=$isRegularTimer")

        lifecycleScope.launch {
            updateCurrentEventDuration()

            _remainingSeconds.value = getTime()

            currentTimer = object : CountDownTimer(getTime() * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000
                    _remainingSeconds.value = seconds
                    Log.d(TAG, "sunset seconds remaining: $seconds")
                }

                override fun onFinish() {
                    // Send streak update and add plancoins when work phase finishes
                    // TODO plancoins should be added to the local db every e.g. 5 minutes
                    // TODO instead of at the end
                    if (_isWorkPhase.value) {
                        lifecycleScope.launch {
                            sendStreakUpdateIfNeeded()
                            // Award plancoins for the completed focus session
                            val minutesCompleted = (getTime() / 60).toInt()
                            plancoinController.addSpentTimePlancoins(minutesCompleted)
                        }
                    }

                    if (isRegularTimer) {
                        lifecycleScope.launch {
                            PomodoroStateBus.emit(PomodoroState.EVENT_COMPLETE)
                        }
                    } else if (_isWorkPhase.value) {
                        // if we were working, emit break
                        lifecycleScope.launch {
                            PomodoroStateBus.emit(PomodoroState.START_BREAK)
                        }
                    } else {
                        lifecycleScope.launch {
                            PomodoroStateBus.emit(PomodoroState.START_WORK)
                        }
                    }

                    if (!isRegularTimer) {
                        _isWorkPhase.value = !_isWorkPhase.value
                        startTimer()
                    }
                }
            }.start()

        }
    }

    private suspend fun updateCurrentEventDuration() {
        // get the duration of the current event
        val currentEvent = calendarRepo.getCurrentEvent().first()
        if (currentEvent != null) {
            val endInstant = currentEvent.endDate.toInstant(calendarRepo.getTimeZone())
            val remainingSeconds = endInstant.epochSeconds - System.currentTimeMillis() / 1000
            Log.d(TAG, "end = $endInstant; rs = $remainingSeconds")
            currentEventDuration = remainingSeconds
        }
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }

    /**
     * Send a streak update if one hasn't been sent today yet.
     */
    private suspend fun sendStreakUpdateIfNeeded() {
        try {
            if (!apiRepo.hasUpdatedStreakToday()) {
                Log.d(TAG, "Sending streak update")
                apiRepo.sendStreakUpdate()
                // Enqueue a sync worker to ensure the update is synced
                // even if the immediate sync in sendStreakUpdate failed
                UpdateSyncWorker.enqueueOneTime(this@PomodoroService)
            } else {
                Log.d(TAG, "Streak already updated today, skipping")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send streak update", e)
            // Enqueue sync worker to retry when network is available
            UpdateSyncWorker.enqueueOneTime(this@PomodoroService)
        }
    }
}
