package app.avoor.planbot

import android.app.Application
import androidx.room.Room
import app.avoor.planbot.data.AppContainer
import app.avoor.planbot.data.AppDatabase
import app.avoor.planbot.data.DefaultAppContainer
import app.avoor.planbot.data.auth.KeystoreAuthTokenProvider
import app.avoor.planbot.data.dataStore
import app.avoor.planbot.data.migrations.MIGRATION_FROM_3_TO_4
import app.avoor.planbot.data.migrations.MIGRATION_FROM_4_TO_5
import app.avoor.planbot.data.mock.DemoAppContainer
import app.avoor.planbot.data.mock.TemporaryAuthTokenProvider
import app.avoor.planbot.data.mock.TemporaryPreferenceStore
import app.avoor.planbot.data.prefs.LocalPreferenceStore
import app.avoor.planbot.data.prefs.PomodoroPreset
import app.avoor.planbot.data.secureDataStore
import app.avoor.planbot.pomodoro.PomodoroService
import app.avoor.planbot.worker.UpdateSyncWorker
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AvoorApplication(
    val demo: Boolean = false,
    val production: Boolean = true
): Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024)
                    .build()
            }
            .logger(DebugLogger())
            .networkObserverEnabled(false)
            .respectCacheHeaders(false)
            .build()
    }

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        val preferenceStore = LocalPreferenceStore(this.dataStore)
        // separate pref store for auth
        val authPreferenceStore = LocalPreferenceStore(this.secureDataStore)
        val authTokenProvider = KeystoreAuthTokenProvider(authPreferenceStore)

        // create a room database
        val db = if (demo) {
            // use a RAM database for demo mode
            Room.inMemoryDatabaseBuilder(
                applicationContext,
                AppDatabase::class.java
            ).build()
        } else {
            // load a database from disk in real mode
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "pxcdb"
            ).addMigrations(
                MIGRATION_FROM_3_TO_4,
                MIGRATION_FROM_4_TO_5
            ).build()
        }

        container = if (demo) {
            DemoAppContainer(
                TemporaryAuthTokenProvider(),
                TemporaryPreferenceStore(),
                db
            )
        } else {
            DefaultAppContainer(
                authTokenProvider,
                preferenceStore,
                authPreferenceStore,
                this.filesDir,
                this.applicationContext.contentResolver,
                db,
                production
            )
        }

        // Schedule periodic streak sync
        UpdateSyncWorker.schedulePeriodicSync(this)

        // load preferences
        MainScope().launch {
            PomodoroService.isRegularTimer = preferenceStore.getPomodoroRegularMode()

            val pomoPreset = preferenceStore.getPomodoroPreset()
            if (pomoPreset == PomodoroPreset.CUSTOM) {
                PomodoroService.setCustomTime(
                    preferenceStore.getPomodoroWorkDur(),
                    preferenceStore.getPomodoroBreakDur()
                )
            } else {
                PomodoroService.applyPreset(pomoPreset)
            }
        }
    }
}