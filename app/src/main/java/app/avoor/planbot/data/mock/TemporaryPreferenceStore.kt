package app.avoor.planbot.data.mock

import android.util.Log
import app.avoor.planbot.data.prefs.PomodoroPreset
import app.avoor.planbot.data.prefs.PreferenceStore

class TemporaryPreferenceStore: PreferenceStore {

    var iv: String? = null
    var rt: String? = "t"

    var pomoMode: Boolean = false
    var pomoBreakDur: Int = 0
    var pomoWorkDur: Int = 0
    var pomoPreset: PomodoroPreset = PomodoroPreset.CLASSIC

    override suspend fun getIV(): String? {
        return iv
    }

    override suspend fun setIV(value: String) {
        iv = value
    }

    override suspend fun getRefreshToken(): String? {
        Log.d("avr#tps", rt ?: "no rt")
        return rt
    }

    override suspend fun setRefreshToken(value: String) {
        Log.d("avr#tps", value)
        rt = value
    }

    override suspend fun deleteRefreshToken() {
        iv = null
    }

    override suspend fun getPomodoroRegularMode(): Boolean {
        return pomoMode
    }

    override suspend fun setPomodoroRegularMode(value: Boolean) {
        pomoMode = value
    }

    override suspend fun getPomodoroPreset(): PomodoroPreset {
        return pomoPreset
    }

    override suspend fun setPomodoroPreset(value: PomodoroPreset) {
        pomoPreset = value
    }

    override suspend fun getPomodoroWorkDur(): Int {
        return pomoWorkDur
    }

    override suspend fun setPomodoroWorkDur(value: Int) {
        pomoWorkDur = value
    }

    override suspend fun getPomodoroBreakDur(): Int {
        return pomoBreakDur
    }

    override suspend fun setPomodoroBreakDur(value: Int) {
        pomoBreakDur = value
    }

}