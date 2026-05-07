package app.avoor.planbot.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.Screen
import kotlinx.coroutines.delay

@Composable
fun NuxStartScreen(navigator: Navigator) {
    var shrinkIcon by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }

    val iconFraction by animateFloatAsState(
        targetValue = if (shrinkIcon) 0.65f else 1f,
        animationSpec = tween(durationMillis = 600),
        label = "iconFraction"
    )

    LaunchedEffect(Unit) {
        delay(600L)
        showTagline = true
        delay(1000L)
        shrinkIcon = true
        delay(300L)
        showLogin = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AnimatedVisibility(
            visible = showTagline,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                Modifier.fillMaxWidth().fillMaxHeight(iconFraction)
            )
        }

        AnimatedVisibility(
            visible = showLogin,
            enter = fadeIn()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    stringResource(R.string.nux_start_title),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    stringResource(R.string.nux_start_body),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { navigator.navigate(Screen.LOGIN) }) {
                        Text(stringResource(R.string.login_button))
                    }
                    Button(onClick = { navigator.navigate(Screen.REGISTER) }) {
                        Text(stringResource(R.string.next))
                    }
                }
            }
        }
    }
}