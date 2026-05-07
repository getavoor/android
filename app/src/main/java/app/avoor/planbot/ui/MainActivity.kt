package app.avoor.planbot.ui

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.BuildConfig
import app.avoor.planbot.R
import app.avoor.planbot.UriUtils
import app.avoor.planbot.pomodoro.PomodoroService
import app.avoor.planbot.pomodoro.haptics.PomodoroAndroidHaptics
import app.avoor.planbot.pomodoro.ui.PomodoroScreen
import app.avoor.planbot.ui.components.ConnectionErrorDialog
import app.avoor.planbot.ui.components.OutdatedDialog
import app.avoor.planbot.ui.dev.screens.DiscoveryMessenger
import app.avoor.planbot.ui.helper.ProfilePictureManager
import app.avoor.planbot.ui.navigator.MainNavigator
import app.avoor.planbot.ui.screens.DeleteFeedbackScreen
import app.avoor.planbot.ui.screens.DeleteWarnScreen
import app.avoor.planbot.ui.screens.DiscoveryScreen
import app.avoor.planbot.ui.screens.GroupDiscoveryScreen
import app.avoor.planbot.ui.screens.JoinGroupDiscoveryScreen
import app.avoor.planbot.ui.screens.LoginScreen
import app.avoor.planbot.ui.screens.MainScreen
import app.avoor.planbot.ui.screens.MainSettingsScreen
import app.avoor.planbot.ui.screens.NuxEndScreen
import app.avoor.planbot.ui.screens.NuxNotisPermissionScreen
import app.avoor.planbot.ui.screens.NuxPermissionScreen
import app.avoor.planbot.ui.screens.NuxPlancoinScreen
import app.avoor.planbot.ui.screens.NuxStartScreen
import app.avoor.planbot.ui.screens.NuxStreakScreen
import app.avoor.planbot.ui.screens.PlancoinScreen
import app.avoor.planbot.ui.screens.RegisterScreen
import app.avoor.planbot.ui.screens.StreakScreen
import app.avoor.planbot.ui.screens.TimelineTestScreen
import app.avoor.planbot.ui.screens.VerifyCompleteScreen
import app.avoor.planbot.ui.screens.VerifyScreen
import app.avoor.planbot.ui.theme.PlanbotTheme
import app.avoor.planbot.ui.viewmodel.MainViewModel
import app.avoor.planbot.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val pfpManager = ProfilePictureManager(this)

    private lateinit var pomodoroService: PomodoroService
    private var pomoServiceBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService().  */
    private val pomoConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as PomodoroService.LocalBinder
            pomodoroService = binder.getService()
            pomoServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            pomoServiceBound = false
        }
    }


    override fun onStart() {
        super.onStart()
        // Bind to pomo service.
        Intent(this, PomodoroService::class.java).also { intent ->
            bindService(intent, pomoConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(pomoConnection)
        pomoServiceBound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // create haptics engine
        val pomoHaptics = PomodoroAndroidHaptics(this)

        // Retrieve the URI this activity was started with
        val uri: Uri? = intent?.data

        val userManager = (application as AvoorApplication).container.userManager

        setContent {
            // Create a navigation controller and provide it to the rest of the app
            val navController = rememberNavController()
            val navigator = MainNavigator()
            navigator.setController(navController)

            val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
            val viewState by viewModel.uiState.collectAsState()

            var loginAlreadyOpened by remember {
                mutableStateOf(false)
            }
            PlanbotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (viewState.showConnectionError) {
                        ConnectionErrorDialog(
                            viewState.failedAction != null
                        ) {
                            viewModel.hideConnectionError()
                            // perform the requested action
                            if (viewState.failedAction == AppAction.AUTO_LOGIN) {
                                viewModel.autoLogin()
                            }
                        }
                    }
                    if (viewState.showUpdateError) {
                        OutdatedDialog {
                            try {
                                startActivity(
                                    Intent(Intent.ACTION_VIEW,
                                        "market://details?id=${BuildConfig.APPLICATION_ID}".toUri()),
                                    null
                                )
                            } catch (_: ActivityNotFoundException) {
                                startActivity(
                                    Intent(Intent.ACTION_VIEW,
                                        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".toUri()),
                                    null
                                )
                            }
                        }
                    }
                    NavHost(navController = navController, startDestination = Screen.NONE.toString()) {
                        composable(Screen.MAIN.toString()) {
                            MainScreen(navigator, (application as AvoorApplication).container.calendarRepository)
                        }
                        composable(Screen.TIMER.toString()) {
                            // start the timer if necessary
                            LaunchedEffect(pomoServiceBound) {
                                if (pomoServiceBound && !pomodoroService.timerStarted) {
                                    // Start pomodoro service
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(Intent(this@MainActivity, PomodoroService::class.java))
                                    } else {
                                        startService(Intent(this@MainActivity, PomodoroService::class.java))
                                    }
                                }
                            }
                            PomodoroScreen(
                                (application as AvoorApplication).container.plancoinController,
                                (application as AvoorApplication).container.calendarRepository,
                                getTime = {
                                    return@PomodoroScreen if (pomoServiceBound) {
                                        pomodoroService.getTime()
                                    } else 0L
                                },
                                pomoHaptics = pomoHaptics,
                                finish = {
                                    navigator.goBack()
                                }
                            )
                        }
                        composable(Screen.DISCOVERY.toString()) {
                            DiscoveryScreen(navigator, this@MainActivity)
                        }
                        composable(
                            Screen.DISCOVERY.toString() + "/{restType}",
                            arguments = listOf(navArgument("restType"){
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val restType = backStackEntry.arguments?.getString("restType")
                            DiscoveryScreen(navigator, this@MainActivity, restType)
                        }
                        composable(Screen.DISCOVERY_GROUP.toString()) {
                            GroupDiscoveryScreen(ctx = this@MainActivity, navigator)
                        }
                        composable(Screen.NUX_START.toString()) {
                            NuxStartScreen(navigator)
                        }
                        composable(Screen.LOGIN.toString()) {
                            LoginScreen(navigator)
                        }
                        composable(Screen.AUTO_LOGIN.toString()) {
                            AutoLoginScreen()
                        }
                        composable(Screen.REGISTER.toString()) {
                            RegisterScreen(navigator)
                        }
                        composable(Screen.VERIFY.toString()) {
                            VerifyScreen(navigator)
                        }
                        composable(Screen.VERIFY_WAIT.toString()) {
                            VerifyWaitScreen()
                        }
                        composable(Screen.VERIFY_COMPLETE.toString()) {
                            VerifyCompleteScreen(navigator)
                        }
                        composable(Screen.NUX_PERMISSION.toString()) {
                            NuxPermissionScreen(navigator, false)
                        }
                        composable(Screen.NUX_PERMISSION.toString() + "/transient") {
                            NuxPermissionScreen(navigator, true)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            composable(Screen.NUX_NOTIFY_PERMISSION.toString()) {
                                NuxNotisPermissionScreen(navigator, false)
                            }
                            composable(Screen.NUX_NOTIFY_PERMISSION.toString() + "/transient") {
                                NuxNotisPermissionScreen(navigator, true)
                            }
                        }
                        composable(route = Screen.NUX_STREAK.toString()) {
                            NuxStreakScreen(navigator)
                        }
                        composable(route = Screen.NUX_PLANCOIN.toString()) {
                            NuxPlancoinScreen(navigator)
                        }
                        composable(route = Screen.NUX_END.toString()) {
                            NuxEndScreen(navigator)
                        }
                        composable(
                            Screen.JOIN_GROUP_DISCOVERY.toString() + "/{groupID}",
                            arguments = listOf(navArgument("groupID"){
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val groupID = backStackEntry.arguments?.getString("groupID")
                            JoinGroupDiscoveryScreen(groupID, this@MainActivity, navigator)
                        }
                        composable(Screen.DELETE_FEEDBACK.toString()) {
                            DeleteFeedbackScreen(navigator)
                        }
                        composable(Screen.DELETE_WARN.toString()) {
                            DeleteWarnScreen(navigator)
                        }
                        composable(Screen.NONE.toString()){}
                        composable(Screen.DEV_MESSENGER.toString()) {
                            DiscoveryMessenger()
                        }
                        composable(route = Screen.TIMELINE_TEST.toString()) {
                            TimelineTestScreen()
                        }
                        composable(route = Screen.STREAK.toString()) {
                            StreakScreen(navigator)
                        }
                        composable(route = Screen.PLANCOIN.toString()) {
                            PlancoinScreen(navigator)
                        }
                        composable(route = Screen.SETTINGS.toString()) {
                            MainSettingsScreen(
                                context = this@MainActivity,
                                navigator = navigator,
                                prefStore = (application as AvoorApplication).container.preferenceStore
                            )
                        }
                    }
                }
            }

            // Check the URI, and check the login if it's OK
            viewModel.init(navigator)
            val ignoreLoginCheck = checkVerifyUri(uri, viewModel)
            if (!ignoreLoginCheck) {
                viewModel.checkLogin()
            }
        }
    }

    private fun checkVerifyUri(uri: Uri?, viewModel: MainViewModel): Boolean {
        // If there is a URL:
        if (uri != null) {
            Log.d("avr#ma", uri.path.toString())
            // Check if it is a valid Planbot URL
            if (UriUtils.isAvoorUrl(uri)) {
                // Check if it is trying to confirm
                if (uri.path?.startsWith("/confirm") == true) {
                    // Get the path segments
                    val segments = uri.pathSegments
                    // Check if there is a verification token in the arguments
                    if (segments.size == 2) {
                        // The second argument is the verification token - provide it
                        viewModel.setVerifyToken(segments[1])
                        return true
                    }
                }
                // Check if it is trying to join a group
                else if (uri.path?.startsWith("/join") == true) {
                    // Get the path segments
                    val segments = uri.pathSegments
                    // only allow joining groups in debug builds
                    if (BuildConfig.DEBUG) {
                        // Check if there is a group ID in the arguments
                        if (segments.size == 2) {
                            // The second argument is the ID - provide it
                            viewModel.joinGroupDiscovery(segments[1])
                            return true
                        }
                    }
                }
            }
            Log.d("avr#ma", uri.path.toString())
        }
        return false
    }
}

/**
 * A screen to show when the app is signing in automatically (e.g. using social login or a refresh token).
 */
@Composable
fun AutoLoginScreen() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // indeterminate (no progress)
        CircularProgressIndicator()
        Text(stringResource(R.string.auto_sign_in))
    }
}

/**
 * A screen to show when the app is processing email verification.
 */
@Composable
fun VerifyWaitScreen() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // indeterminate (no progress)
        CircularProgressIndicator()
        Text(stringResource(R.string.verify_in_progress))
    }
}