package app.avoor.planbot.pomodoro.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class PomodoroAndroidHaptics(
    val context: Context
): PomodoroHaptics {

    val vibrator: Vibrator = context.getSystemService(Vibrator::class.java)

    override fun tick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined( VibrationEffect.EFFECT_TICK))
        }
    }
}