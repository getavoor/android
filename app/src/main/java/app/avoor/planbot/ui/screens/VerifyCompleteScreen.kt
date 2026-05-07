package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Check
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VerifyCompleteScreen(
    navigator: Navigator
) {

    // Calendar permission state
    val calPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_CALENDAR
    )

    LaunchedEffect(null) {
        // wait, then go to another screen
        delay(1500)
        when (calPermissionState.status) {
            // if the calendar permission has been granted, skip to the streak explainer
            is PermissionStatus.Granted -> {
                navigator.navigate(Screen.NUX_STREAK)
            }
            // if it hasn't been granted yet, ask for it
            is PermissionStatus.Denied -> {
                navigator.navigate(Screen.NUX_PERMISSION)
            }
        }
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        // indeterminate (no progress)
        Icon(
            Icons.Check,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text(stringResource(R.string.verify_complete_title))
    }
}