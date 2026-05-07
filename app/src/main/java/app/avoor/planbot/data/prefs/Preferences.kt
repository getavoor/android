package app.avoor.planbot.data.prefs

import androidx.annotation.StringRes
import app.avoor.planbot.R

interface PreferenceStore {

    /**
     * Gets the encoded IV.
     *
     * @return the encoded IV, or null if it doesn't exist.
     */
    suspend fun getIV(): String?

    /**
     * Sets the encoded IV.
     *
     * @param value the IV.
     */
    suspend fun setIV(value: String)

    /**
     * Gets the encoded refresh token.
     *
     * @return the encoded refresh token, or null if it doesn't exist.
     */
    suspend fun getRefreshToken(): String?

    /**
     * Sets the encoded refresh token.
     *
     * @param value the refresh token.
     */
    suspend fun setRefreshToken(value: String)

    /**
     * Deletes the encoded refresh token.
     */
    suspend fun deleteRefreshToken()

    /**
     * Gets the timer mode (true = regular timer, false = pomodoro timer).
     */
    suspend fun getPomodoroRegularMode(): Boolean
    /**
     * Sets the timer mode (true = regular timer, false = pomodoro timer).
     */
    suspend fun setPomodoroRegularMode(value: Boolean)

    /**
     * Gets the Pomodoro preset.
     */
    suspend fun getPomodoroPreset(): PomodoroPreset
    /**
     * Sets the Pomodoro preset.
     */
    suspend fun setPomodoroPreset(value: PomodoroPreset)

    /**
     * Gets the Pomodoro work duration.
     */
    suspend fun getPomodoroWorkDur(): Int
    /**
     * Sets the Pomodoro work duration.
     */
    suspend fun setPomodoroWorkDur(value: Int)

    /**
     * Gets the Pomodoro work duration.
     */
    suspend fun getPomodoroBreakDur(): Int
    /**
     * Sets the Pomodoro work duration.
     */
    suspend fun setPomodoroBreakDur(value: Int)
}

enum class PomodoroPreset(
    val workTime: Int,
    val breakTime: Int,
    @StringRes val resID: Int
) {
    /**
     * 25 min work / 5 min break
     */
    CLASSIC(25, 5, R.string.setting_pomoDur_option_classic),
    /**
     * 50 min work / 10 min break
     */
    EXTENDED(50, 10, R.string.setting_pomoDur_option_extended),
    /**
     * custom
     */
    CUSTOM(0, 0, R.string.setting_pomoDur_option_custom)
}