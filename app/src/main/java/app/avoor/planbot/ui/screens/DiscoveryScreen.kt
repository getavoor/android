package app.avoor.planbot.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.BuildConfig
import app.avoor.planbot.OnLifecycleEvent
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.FailedActionDialog
import app.avoor.planbot.ui.components.OutdatedDialog
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.DiscoveryViewModel
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Close

val dscRootPadding = 20.dp

@Composable
fun DiscoveryScreen(
    navigator: Navigator,
    ctx: Activity,
    type: String? = null,
    viewModel: DiscoveryViewModel = viewModel(factory = DiscoveryViewModel.Factory)
) {
    val viewState by viewModel.uiState.collectAsState()

    if (!viewState.compatible) {
        // TODO if switching to KMP: remove references to activities
        OutdatedDialog {
            try {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW,
                        "market://details?id=${BuildConfig.APPLICATION_ID}".toUri()),
                    null
                )
            } catch (_: ActivityNotFoundException) {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW,
                        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".toUri()),
                    null
                )
            }
        }
    }

    if (viewState.showError) {
        FailedActionDialog(mainAction = {
            viewModel.disconnect()
            navigator.goBack()
            viewModel.setNeedsInit()
        }) {
            viewModel.disconnect()
            navigator.goBack()
            viewModel.setNeedsInit()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.init(type)
    }

    OnLifecycleEvent { _, event ->
        // when the activity is restarted, join and continue discovery again
        if (event == Lifecycle.Event.ON_RESUME) { viewModel.onResume() }
        // when it is destroyed, disconnect
        else if (event == Lifecycle.Event.ON_DESTROY) { viewModel.disconnect() }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dscRootPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dscRootPadding, Alignment.CenterVertically)
        ) {
            // TODO fluff ui
        }
        IconButton(
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(10.dp).align(Alignment.TopStart),
            onClick = {
                viewModel.disconnect()
                navigator.goBack()
                viewModel.setNeedsInit()
            }
        ) {
            Icon (
                Icons.Close,
                stringResource(R.string.close)
            )
        }
    }
}