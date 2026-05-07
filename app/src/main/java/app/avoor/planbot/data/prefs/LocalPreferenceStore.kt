package app.avoor.planbot.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class LocalPreferenceStore(
    private val dataStore: DataStore<Preferences>
): PreferenceStore {

    val IV = stringPreferencesKey("gmei")
    val TOKEN = stringPreferencesKey("gmet")

    val POMODORO_MODE = booleanPreferencesKey("pomoMode")
    val POMODORO_PRESET = intPreferencesKey("pomoPreset")
    val POMODORO_WORK_DUR = intPreferencesKey("pomoWork")
    val POMODORO_BREAK_DUR = intPreferencesKey("pomoBreak")

    override suspend fun getIV(): String? {
        return dataStore.data.first()[IV]
    }

    override suspend fun setIV(value: String) {
        dataStore.edit { settings ->
            settings[IV] = value
        }
    }

    override suspend fun getPomodoroRegularMode(): Boolean {
        return dataStore.data.first()[POMODORO_MODE] == true
    }

    override suspend fun setPomodoroRegularMode(value: Boolean) {
        dataStore.edit { settings ->
            settings[POMODORO_MODE] = value
        }
    }

    override suspend fun getPomodoroPreset(): PomodoroPreset {
        // save the ordinal
        val pomoPreset = dataStore.data.first()[POMODORO_PRESET]
        return try {
            PomodoroPreset.entries.first { it.ordinal == pomoPreset }
        } catch(_: NoSuchElementException) {
            PomodoroPreset.CLASSIC
        }
    }

    override suspend fun setPomodoroPreset(value: PomodoroPreset) {
        // save the ordinal
        dataStore.edit { settings ->
            settings[POMODORO_PRESET] = value.ordinal
        }
    }

    override suspend fun getPomodoroWorkDur(): Int {
        return dataStore.data.first()[POMODORO_WORK_DUR] ?: 0
    }

    override suspend fun setPomodoroWorkDur(value: Int) {
        dataStore.edit { settings ->
            settings[POMODORO_WORK_DUR] = value
        }
    }

    override suspend fun getPomodoroBreakDur(): Int {
        return dataStore.data.first()[POMODORO_BREAK_DUR] ?: 0
    }

    override suspend fun setPomodoroBreakDur(value: Int) {
        dataStore.edit { settings ->
            settings[POMODORO_BREAK_DUR] = value
        }
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[TOKEN]
    }

    override suspend fun setRefreshToken(value: String) {
        dataStore.edit { settings ->
            settings[TOKEN] = value
        }
    }

    override suspend fun deleteRefreshToken() {
        dataStore.edit { settings ->
            settings.remove(TOKEN)
        }
    }

}