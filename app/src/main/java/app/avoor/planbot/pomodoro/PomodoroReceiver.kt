package app.avoor.planbot.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PomodoroReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == "STOP_TIMER") {
            val serviceIntent = Intent(context, PomodoroService::class.java).apply {
                action = "STOP_TIMER"
            }
            context?.startService(serviceIntent)
        }
    }
}
