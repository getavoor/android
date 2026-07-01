package app.avoor.planbot.data

import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.prefs.PreferenceStore
import app.avoor.planbot.ShuffleImpl
import app.avoor.planbot.domain.PlancoinController
import app.avoor.planbot.domain.UserManager

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

