package app.avoor.planbot.pomodoro.data

enum class PomodoroState {
    /**
     * A work session has started.
     * If the timer is not in pomodoro mode, this is fired when an event has started.
     */
    START_WORK,
    /**
     * A break session has started.
     * Only fired in pomodoro mode.
     */
    START_BREAK,
    /**
     * An event has finished.
     */
    EVENT_COMPLETE,
    /**
     * The timer service is starting.
     */
    STOPPING,
    /**
     * The timer service is stopping.
     */
    STARTING
}