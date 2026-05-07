package app.avoor.planbot.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.NuxEndNearestEventState
import app.avoor.planbot.ui.viewmodel.NuxEndViewModel
import app.avoor.planbot.ui.viewmodel.Screen
import kotlinx.coroutines.delay
import tech.cataspect.m3x.TwoButtons

@Composable
fun NuxEndScreen(
    navigator: Navigator
) {
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300L)
        showText = true
    }
    val viewModel: NuxEndViewModel = viewModel(factory = NuxEndViewModel.Factory)
    val viewState by viewModel.uiState.collectAsState()

    Scaffold (
        bottomBar = {

            TwoButtons(
                positiveAction = {
                    // put main first
                    navigator.override(Screen.MAIN)
                    // if the user has an event, go to the timer immediately
                    if (viewState.eventState == NuxEndNearestEventState.NOW)
                        navigator.navigate(Screen.TIMER)
                },
                positiveText = stringResource(R.string.next),
                negativeAction = {
                    navigator.goBack()
                },
                showNegative = false
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnimatedVisibility(
                visible = showText
            ) {
                Text(
                    stringResource(R.string.nux_end_title),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium
                )
            }
            AnimatedVisibility(
                visible = showText
            ) {
                Text(
                    stringResource(R.string.nux_end_desc_generic)
                )
            }
            // extra text
            if (viewState.eventState != NuxEndNearestEventState.FAR) {
                AnimatedVisibility(
                    visible = showText
                ) {
                    Text(
                        if (viewState.eventState == NuxEndNearestEventState.NEAR) {
                            stringResource(
                                R.string.nux_end_desc_event_soon
                            ).format(viewState.eventName)
                        } else {
                            stringResource(
                                R.string.nux_end_desc_event_now
                            ).format(viewState.eventName)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}