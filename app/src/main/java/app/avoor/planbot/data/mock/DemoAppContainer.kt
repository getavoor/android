package app.avoor.planbot.data.mock

import app.avoor.planbot.ShuffleImpl
import app.avoor.planbot.data.AppContainer
import app.avoor.planbot.data.AppDatabase
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.data.auth.AuthTokenProvider
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.prefs.PreferenceStore
import app.avoor.planbot.domain.PlancoinController
import app.avoor.planbot.domain.UserManager
import kotlinx.coroutines.MainScope

class DemoAppContainer(
    override val authTokenProvider: AuthTokenProvider,
    override val preferenceStore: PreferenceStore,
    override val database: AppDatabase
) : AppContainer {

    override val authPreferenceStore: PreferenceStore by lazy {
        preferenceStore
    }

    override val planbotApiRepository: PlanbotApiRepository by lazy {
        DemoPlanbotApiRepository()
    }

    override val discoveryRepository: DiscoveryRepository by lazy {
        DemoDiscoveryRepository()
    }

    override val calendarRepository: CalendarRepository by lazy {
        DemoCalendarRepository()
    }

    override val userManager: UserManager by lazy {
        UserManager(preferenceStore, authTokenProvider, planbotApiRepository)
    }

    override val plancoinController: PlancoinController by lazy {
        PlancoinController(userManager, database.plancoinDao(), MainScope())
    }

    override val shuffleImpl: ShuffleImpl by lazy {
        ShuffleImpl(calendarRepository)
    }
}
