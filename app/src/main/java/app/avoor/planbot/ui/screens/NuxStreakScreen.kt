package app.avoor.planbot.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.ConnectionErrorDialog
import app.avoor.planbot.ui.components.LoadingBar
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.LoginViewModel
import app.avoor.planbot.ui.viewmodel.NuxStreakViewModel
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.ArrowBack
import tech.cataspect.m3x.TwoButtons

@Composable
fun NuxStreakScreen(
    navigator: Navigator
) {
    val viewModel: NuxStreakViewModel = viewModel(factory = NuxStreakViewModel.Factory)
    val viewState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val bodyPhase1 = stringResource(R.string.nux_streak_body_phase1)
    val bodyPhase2 = stringResource(R.string.nux_streak_body_phase2)
    var bodyText by remember {
        mutableStateOf(bodyPhase1)
    }
    var streak by remember {
        mutableIntStateOf(0)
    }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.nux_streak_title),
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
                    if (viewState.nextSwitchesScreens)
                        navigator.navigate(Screen.NUX_PLANCOIN)
                    else {
                        viewModel.increaseStreak()
                        // update the ui
                        streak++
                        bodyText = bodyPhase2
                    }
                },
                positiveText = stringResource(R.string.next),
                negativeAction = {
                    navigator.goBack()
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(targetState = bodyText) { targetState ->
                Text(targetState)
            }
            Spacer(Modifier.height(32.dp))
            AnimatedContent(targetState = streak) { targetState ->
                Text(
                    text = targetState.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 72.sp,
                    modifier = Modifier.padding(bottom = 0.dp)
                )
            }
            AnimatedContent(targetState = streak) { targetState ->
                Text(
                    text = pluralStringResource(
                        R.plurals.streak_days,
                        targetState
                    ),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}