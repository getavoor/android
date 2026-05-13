package app.avoor.planbot.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.ConnectionErrorDialog
import app.avoor.planbot.ui.components.FailedActionDialog
import app.avoor.planbot.ui.components.LoadingBar
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.planbot.ui.viewmodel.VerifyResendViewModel
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.ArrowBack
import kotlinx.coroutines.launch

@Composable
fun VerifyScreen(
    navigator: Navigator,
    viewModel: VerifyResendViewModel = viewModel(factory = VerifyResendViewModel.Factory)
) {
    val viewState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val resendString = stringResource(R.string.verify_resend_success)

    LaunchedEffect(viewState.success) {
        if (viewState.success) {
            scope.launch {
                snackbarHostState.showSnackbar(resendString)
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.verify_cta_title),
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(viewState.progress) {
                    LoadingBar()
                }

                Text(
                    stringResource(R.string.verify_cta),
                    Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.verify_resend_cta))
                    TextButton(onClick = { viewModel.send() }) {
                        Text(stringResource(R.string.verify_resend))
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.verify_login_cta))
                    TextButton(onClick = { navigator.navigate(Screen.LOGIN) }) {
                        Text(stringResource(R.string.verify_login))
                    }
                }
            }
        }
    )
}