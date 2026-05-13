package app.avoor.planbot.data

import android.content.ContentResolver
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import app.avoor.planbot.RetrofitInstance
import app.avoor.planbot.SocketIOInstance
import app.avoor.planbot.api.PlanbotApiService
import app.avoor.planbot.data.api.NetworkPlanbotApiRepository
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.calendar.AndroidCalendarRepository
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.discovery.SocketDiscoveryRepository
import app.avoor.planbot.data.prefs.PreferenceStore
import app.avoor.planbot.ShuffleImpl
import app.avoor.planbot.domain.PlancoinController
import app.avoor.planbot.domain.UserManager
import kotlinx.coroutines.MainScope
import java.io.File

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gmprst")
val Context.secureDataStore: DataStore<Preferences> by preferencesDataStore(name = "avrtmp")

interface AppContainer {
    val production: Boolean
    val planbotApiRepository: PlanbotApiRepository
    val authTokenProvider: AuthTokenProvider
    val preferenceStore: PreferenceStore
    val authPreferenceStore: PreferenceStore
    val discoveryRepository: DiscoveryRepository
    val userManager: UserManager
    val calendarRepository: CalendarRepository
    val database: AppDatabase
    val plancoinController: PlancoinController
    val shuffleImpl: ShuffleImpl
}

class DefaultAppContainer(
    override val authTokenProvider: AuthTokenProvider,
    override val preferenceStore: PreferenceStore,
    override val authPreferenceStore: PreferenceStore,
    val filesDir: File,
    val contentResolver: ContentResolver,
    val appDatabase: AppDatabase,
    override val production: Boolean = false
) : AppContainer {

    override val database by lazy {
        appDatabase
    }

    override val planbotApiRepository: PlanbotApiRepository by lazy {
        val retrofit = RetrofitInstance.getInstance(authTokenProvider, production)
        val retrofitService: PlanbotApiService by lazy {
            retrofit.create(PlanbotApiService::class.java)
        }
        NetworkPlanbotApiRepository(
            retrofitService,
            filesDir,
            appDatabase
        )
    }

    override val discoveryRepository: DiscoveryRepository by lazy {
        val socket = SocketIOInstance.getSocket(production)
        socket.connect()
        SocketDiscoveryRepository(socket)
    }

    override val userManager: UserManager by lazy {
        UserManager(authPreferenceStore, authTokenProvider, planbotApiRepository)
    }

    override val calendarRepository: CalendarRepository by lazy {
        AndroidCalendarRepository(contentResolver, MainScope())
    }

    override val plancoinController: PlancoinController by lazy {
        PlancoinController(userManager, appDatabase.plancoinDao(), MainScope())
    }

    override val shuffleImpl: ShuffleImpl by lazy {
        ShuffleImpl(calendarRepository)
    }
}
