package app.avoor.planbot.ui.screens

import android.os.Build
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import tech.cataspect.m3x.TwoButtons

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NuxPermissionScreen(
    navigator: Navigator,
    transient: Boolean
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val notifPermState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    // permission state
    val readPermState = rememberPermissionState(
        android.Manifest.permission.READ_CALENDAR,
        onPermissionResult = {
            if (it) {
                // on android 13 additionally check for notify
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notifPermState.status.isGranted) {
                    navigator.replace(Screen.NUX_NOTIFY_PERMISSION)
                }
                // if the screen is transient, go to the main screen,
                // otherwise the next nux screen
                else if (transient) {
                    navigator.replace(Screen.MAIN)
                } else {
                    navigator.replace(Screen.NUX_STREAK)
                }
            }
        }
    )
    // permission state
    val permState = rememberPermissionState(
        android.Manifest.permission.WRITE_CALENDAR,
        onPermissionResult = {
            if (it) {
                readPermState.launchPermissionRequest()
            }
        }
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(stringResource(R.string.nux_calendar_usage_title))
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {

            TwoButtons(
                positiveAction = {
                    permState.launchPermissionRequest()
                },
                positiveText = stringResource(R.string.nux_permission_allow),
                negativeAction = {
                    navigator.goBack()
                },
                showNegative = false
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .padding(innerPadding).padding(16.dp)
                .verticalScroll(rememberScrollState())
        ){
            Text(stringResource(R.string.nux_calendar_usage_desc))
        }
    }
}