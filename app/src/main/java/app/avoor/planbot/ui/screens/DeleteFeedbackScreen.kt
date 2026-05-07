package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.symbols.Icons
import app.avoor.planbot.R
import app.avoor.planbot.api.models.FeedbackReason
import app.avoor.planbot.ui.components.ConnectionErrorDialog
import app.avoor.planbot.ui.components.FailedActionDialog
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.DeleteFeedbackViewModel
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.icons.ArrowBack
import tech.cataspect.m3x.TwoButtons
import tech.cataspect.m3x.settings.RadioSetting
import tech.cataspect.m3x.settings.SettingsDividerPadding
import tech.cataspect.m3x.settings.SettingsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteFeedbackScreen(
    navigator: Navigator,
    viewModel: DeleteFeedbackViewModel = viewModel(factory = DeleteFeedbackViewModel.Factory)
) {
    val viewState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    viewModel.setNavigator(navigator)

    if (viewState.showActionError) {
        FailedActionDialog(
            true,
            mainAction = {viewModel.send()},
            onDismiss = {viewModel.hideActionError()}
        )
    }

    if (viewState.showConnectionError) {
        ConnectionErrorDialog(
            showTryAgain = true
        ) {
            viewModel.send()
        }
    }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.delete_feedback_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.goBack()
                    }) {
                        Icon(
                            imageVector = Icons.ArrowBack,
                            contentDescription = stringResource(R.string.btn_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {

            TwoButtons(
                positiveAction = {
                    viewModel.send()
                },
                positiveText = stringResource(R.string.next),
                negativeAction = {
                    navigator.navigate(Screen.DELETE_WARN)
                },
                negativeText = stringResource(R.string.skip),
                disablePositive = (
                    // If the reason is OTHER, require body text to be present
                    if (viewState.reason == FeedbackReason.OTHER) {
                        // If the value is null, disable it
                        if (viewState.body == null) {
                            true
                        } else {
                            // Otherwise check if it's empty
                            viewState.body!!.isEmpty()
                        }
                    }
                    // Otherwise check if it isn't null
                    else {
                        viewState.reason == null
                    }
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(R.string.delete_feedback_text),
                modifier = Modifier
                    .padding(horizontal = SettingsPadding, vertical = SettingsDividerPadding)
                    .fillMaxWidth()
            )
            /*RadioSetting(
                title = stringResource(R.string.delete_feedback_reason_recs),
                body = null,
                onClick = { viewModel.setReason(FeedbackReason.BAD_RECOMMENDATIONS) },
                selected = (viewState.reason == FeedbackReason.BAD_RECOMMENDATIONS),
                modifier = Modifier.fillMaxWidth()
            )*/
            RadioSetting(
                title = stringResource(R.string.delete_feedback_reason_ui),
                body = null,
                onClick = { viewModel.setReason(FeedbackReason.BAD_UI) },
                selected = (viewState.reason == FeedbackReason.BAD_UI),
                modifier = Modifier.fillMaxWidth()
            )
            RadioSetting(
                title = stringResource(R.string.delete_feedback_reason_slow),
                body = null,
                onClick = { viewModel.setReason(FeedbackReason.SLOW) },
                selected = (viewState.reason == FeedbackReason.SLOW),
                modifier = Modifier.fillMaxWidth()
            )
            RadioSetting(
                title = stringResource(R.string.delete_feedback_reason_other),
                body = null,
                onClick = { viewModel.setReason(FeedbackReason.OTHER) },
                selected = (viewState.reason == FeedbackReason.OTHER),
                modifier = Modifier.fillMaxWidth()
            )
            if (viewState.reason == FeedbackReason.OTHER) {
                TextField(
                    value = viewState.body ?: "",
                    onValueChange = {viewModel.setBody(it)},
                    label = {Text(stringResource(R.string.delete_feedback_reason_field))},
                    modifier = Modifier
                        .padding(horizontal = SettingsPadding, vertical = SettingsDividerPadding)
                        .fillMaxWidth()
                )
            }
        }
    }
}