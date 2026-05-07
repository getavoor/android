package app.avoor.planbot.pomodoro.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.avoor.planbot.R
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.domain.PlancoinController
import app.avoor.planbot.pomodoro.PomodoroService
import app.avoor.planbot.pomodoro.data.PomodoroState
import app.avoor.planbot.pomodoro.data.PomodoroStateBus
import app.avoor.planbot.pomodoro.haptics.PomodoroHaptics
import app.avoor.planbot.ui.theme.PlanbotTheme
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Plancoin
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var job: Job? = null

@Composable
fun PomodoroScreen(
    plancoinController: PlancoinController,
    calRepository: CalendarRepository,
    getTime: () -> Long,
    pomoHaptics: PomodoroHaptics,
    finish: () -> Unit
) {
    val remainingSeconds by PomodoroService.remainingSeconds.collectAsState()
    val currentEvent by calRepository.getCurrentEvent().collectAsState(null)
    val workPhase by PomodoroService.isWorkPhase.collectAsState()

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val hours = minutes / 60
    val timeText = if (hours >= 1) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }

    val timeColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(null) {
        // start haptics loop
        if (job == null) {
            job = MainScope().launch {
                while(true) {
                    pomoHaptics.tick()
                    delay(1000)
                }
            }
        }
    }

    LaunchedEffect(null) {
        MainScope().launch {
            PomodoroStateBus.collectEvents { event ->
                if (event == PomodoroState.STOPPING) {
                    job?.cancel()
                    finish()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    currentEvent?.name ?: "No task",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(12.dp).weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedVisibility(
                    visible = workPhase
                ) {
                    PlancoinCounter(
                        plancoinController.balance.collectAsState().value,
                        Modifier.padding(8.dp)
                    )
                }
            }
        },
        content = {x ->
            Box(Modifier
                .drawBehind {
                    val timerHeight = if (workPhase) {
                        size.height * (remainingSeconds * 1f / getTime())
                    } else {
                        size.height * (1-(remainingSeconds * 1f / getTime()))
                    }
                    drawRect(
                        color = timeColor,
                        size = Size(size.width, timerHeight),
                        topLeft = Offset(0f, size.height - timerHeight)
                    )
                }
                .fillMaxSize()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(x),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (seconds != -1L)
                    Text(
                        timeText,
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 96.sp,
                        modifier = Modifier.padding(x)
                    )
                    AnimatedVisibility(visible = !workPhase) {
                        Text(stringResource(R.string.pomodoro_screen_break))
                    }
                }
            }
        }
    )
}

@Composable
fun PlancoinCounter(plancoins: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical=6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Plancoin, stringResource(R.string.main_nav_plancoin))
            Text(plancoins.toString())
        }
    }
}

@Preview
@Composable
fun PlancoinCounterPreview() {
    PlanbotTheme {
        PlancoinCounter(1337, Modifier.padding(8.dp))
    }
}

