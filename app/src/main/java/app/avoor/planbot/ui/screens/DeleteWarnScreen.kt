package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import app.avoor.symbols.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.AccountDeleteDialog
import app.avoor.planbot.ui.components.ConnectionErrorDialog
import app.avoor.planbot.ui.components.FailedActionDialog
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.DeleteWarnViewModel
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.icons.ArrowBack
import tech.cataspect.m3x.Tip
import tech.cataspect.m3x.TwoButtons
import tech.cataspect.m3x.settings.SettingsDividerPadding
import tech.cataspect.m3x.settings.SettingsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteWarnScreen(
    navigator: Navigator,
    viewModel: DeleteWarnViewModel = viewModel(factory = DeleteWarnViewModel.Factory)
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

    if (viewState.success) {
        AccountDeleteDialog(
            { viewModel.hideSuccessDialog() }
        ) {
            viewModel.hideSuccessDialog()
        }
    }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.delete_warn_title),
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
                    navigator.navigate(Screen.MAIN)
                },
                positiveText = stringResource(R.string.no),
                negativeAction = {
                    viewModel.send()
                },
                negativeText = stringResource(R.string.yes)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(R.string.delete_warn_body),
                modifier = Modifier
                    .padding(horizontal = SettingsPadding, vertical = SettingsDividerPadding)
                    .fillMaxWidth()
            )
            // TODO re-enable in the full version
            /*
            Text(
                stringResource(R.string.delete_warn_google),
                modifier = Modifier
                    .padding(horizontal = SettingsPadding, vertical = SettingsDividerPadding)
                    .fillMaxWidth()
            )*/
        }
    }
}