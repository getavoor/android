package app.avoor.planbot.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.ConnectionErrorDialog
import app.avoor.planbot.ui.components.LoadingBar
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.LoginViewModel
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.ArrowBack
import tech.cataspect.m3x.TwoButtons

@Composable
fun LoginScreen(
    navigator: Navigator
) {
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
    val viewState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val paddingModifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)

    if (viewState.showConnectionError) {
        ConnectionErrorDialog(
            showTryAgain = true
        ) {
            viewModel.logIn()
        }
    }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.nux_login_title),
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
                    viewModel.logIn()
                },
                positiveText = stringResource(R.string.login_button),
                negativeAction = {
                    navigator.goBack()
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
        ) {
            AnimatedVisibility(viewState.progress) {
                LoadingBar()
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = viewState.email,
                    onValueChange = { viewModel.setEmail(it) },
                    label = { Text(stringResource(R.string.login_input_email)) },
                    singleLine = true,
                    isError = viewState.invalidCredentials,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(paddingModifier)
                )
                OutlinedTextField(
                    value = viewState.password,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text(stringResource(R.string.login_input_password)) },
                    singleLine = true,
                    isError = viewState.invalidCredentials,
                    visualTransformation =
                        if (viewState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = ImageVector.vectorResource(
                            if (viewState.showPassword) R.drawable.ic_visibility_off
                            else R.drawable.ic_visibility
                        )

                        // Please provide localized description for accessibility services
                        val description =
                            if (viewState.showPassword)
                                stringResource(R.string.login_input_password_hide)
                            else
                                stringResource(R.string.login_input_password_show)

                        IconButton(onClick = {
                            viewModel.toggleShowPassword()
                        }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(paddingModifier)
                )

                Text(stringResource(R.string.login_reg_cta))
                OutlinedButton(onClick = { viewModel.switchToRegister() }) {
                    Text(stringResource(R.string.login_button_reg))
                }
            }
        }
    }
}