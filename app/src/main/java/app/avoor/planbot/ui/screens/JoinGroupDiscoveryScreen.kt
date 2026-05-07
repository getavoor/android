package app.avoor.planbot.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.BuildConfig
import app.avoor.planbot.OnLifecycleEvent
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.GuestProfilePicAnimation
import app.avoor.planbot.ui.components.InvalidLinkDialog
import app.avoor.planbot.ui.components.OutdatedDialog
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.JoinGroupDiscoveryViewModel

@Composable
fun JoinGroupDiscoveryScreen(
    groupID: String?,
    ctx: Activity,
    navigator: Navigator,
    viewModel: JoinGroupDiscoveryViewModel = viewModel(factory = JoinGroupDiscoveryViewModel.Factory)
) {
    val viewState by viewModel.uiState.collectAsState()

    viewModel.setNavigator(navigator)
    viewModel.initIfNecessary(groupID)
    viewModel.rejoinIfNecessary()

    // If the group ID is invalid, show an error message
    if (viewState.showLinkError) {
        InvalidLinkDialog(
            // When the OK button is pressed go to the main screen
            mainAction = {viewModel.hideLinkError()},
            // When the user tries to dismiss the dialog do the same
            onDismiss = {viewModel.hideLinkError()}
        )
    }

    if (!viewState.compatible) {
        // TODO if switching to KMP: remove references to activities
        OutdatedDialog {
            try {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")),
                    null
                )
            } catch (e: ActivityNotFoundException) {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")),
                    null
                )
            }
        }
    }

    OnLifecycleEvent { _, event ->
        // when the activity is restarted, join and continue discovery again
        if (event == Lifecycle.Event.ON_RESUME) { viewModel.onResume() }
        // when it is destroyed, disconnect
        else if (event == Lifecycle.Event.ON_DESTROY) { viewModel.disconnect() }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        GuestProfilePicAnimation(
            viewModel.getUserImageRequest(),
            stabilize = viewState.approved
        )
        AnimatedVisibility(visible = !viewState.approved) {
            Text(
                stringResource(R.string.dsc_join_guest_title),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        AnimatedVisibility(visible = !viewState.approved) {
            Text(
                stringResource(R.string.dsc_join_guest_body),
                textAlign = TextAlign.Center
            )
        }
        AnimatedVisibility(visible = viewState.approved) {
            Text(
                stringResource(R.string.dsc_join_member_title),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        AnimatedVisibility(visible = viewState.approved) {
            Text(
                stringResource(R.string.dsc_join_member_body),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {viewModel.leaveGroup()}
        ) {
            Text("Leave")
        }
    }
}