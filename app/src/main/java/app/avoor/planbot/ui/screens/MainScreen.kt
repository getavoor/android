package app.avoor.planbot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.avoor.planbot.R
import app.avoor.planbot.data.calendar.CalendarRepository
import app.avoor.planbot.ui.components.ShuffleWarningDialog
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Plancoin
import app.avoor.symbols.icons.Settings
import app.avoor.symbols.icons.Shuffle
import app.avoor.symbols.icons.Streak
import app.avoor.symbols.icons.Target
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    navigator: Navigator,
    calendarRepository: CalendarRepository
) {
    val currentEvent by calendarRepository.getCurrentEvent().collectAsState(initial = null)
    var showShuffleWarning by remember {mutableStateOf(false)}
    val showTimer = currentEvent != null

    if (showShuffleWarning) {
        ShuffleWarningDialog(
            onConfirmation = {
                navigator.navigate(Screen.SHUFFLE_PREP)
            },
            onDismiss = {showShuffleWarning = false}
        )
    }


    val topBarBackground = Brush.verticalGradient(
        0.25f to MaterialTheme.colorScheme.background,
        1f to Color.Transparent
    )

    var offsetY by remember { mutableFloatStateOf(0f) }
    val maxOffset = 128.dp // roughly bar height
    val maxOffsetPx = with(LocalDensity.current) { maxOffset.toPx() }
    val uiExtrasScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // available.y is positive when scrolling down, negative when scrolling up.
                // We accumulate it into offsetY, clamped so the bars never go beyond
                // fully hidden (−maxOffsetPx) or fully visible (0).
                offsetY = (offsetY + available.y).coerceIn(-maxOffsetPx, 0f)
                // Consume all the scroll until the bars are fully visible.
                if (offsetY < 0f && offsetY > -maxOffsetPx) {
                    return available
                }
                return Offset.Zero
            }
        }
    }
    val date = remember {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val javaDate = today.toJavaLocalDate()
        val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
        javaDate.format(formatter)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(uiExtrasScrollConnection),
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {

            MainTopAppBar(
                title = {
                    Text(date, style=MaterialTheme.typography.titleLarge)
                },
                modifier = Modifier
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .background(topBarBackground)
                    .zIndex(1f)
            )
            // The toolbar should receive focus before the screen content for a11y, so place it
            // first. Make sure to set its zIndex so it's above the screen content visually.
            HorizontalFloatingToolbar(
                // Always expanded as the toolbar is bottom-centered. We will use a
                // FloatingToolbarScrollBehavior to hide both the toolbar and its FAB on scroll.
                expanded = true,
                floatingActionButton = {
                    if (true) {
                        TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Above
                                ),
                            tooltip = { PlainTooltip { Text(stringResource(R.string.desc_main_focus)) } },
                            state = rememberTooltipState(),
                        ) {
                            FloatingToolbarDefaults.StandardFloatingActionButton(
                                onClick = {
                                    navigator.navigate(Screen.TIMER)
                                },
                            ) {
                                Icon(
                                    Icons.Target,
                                    stringResource(R.string.desc_main_focus)
                                )
                            }
                        }
                    }
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .offset { IntOffset(0, -offsetY.roundToInt()) } // opposite direction
                        .offset(y = -ScreenOffset)
                        .zIndex(1f),
                content = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { stringResource(R.string.desc_main_streak) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = {
                            navigator.navigate(Screen.STREAK)
                        }) {
                            Icon(
                                Icons.Streak,
                                contentDescription = stringResource(R.string.desc_main_streak)
                            )
                        }
                    }
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { stringResource(R.string.main_nav_shuffle) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = {
                            showShuffleWarning = true
                        }) {
                            Icon(
                                Icons.Shuffle,
                                contentDescription = stringResource(R.string.main_nav_shuffle)
                            )
                        }
                    }
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { stringResource(R.string.main_nav_settings) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = {
                            navigator.navigate(Screen.SETTINGS)
                        }) {
                            Icon(
                                Icons.Settings,
                                contentDescription = stringResource(R.string.main_nav_settings)
                            )
                        }
                    }
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { stringResource(R.string.main_nav_plancoin) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = {
                            navigator.navigate(Screen.PLANCOIN)
                        }) {
                            Icon(
                                Icons.Plancoin,
                                contentDescription = stringResource(R.string.main_nav_plancoin)
                            )
                        }
                    }
                },
            )

            CalendarScreen()
        }
    }
}

@Composable
fun MainTopAppBar(title: @Composable () -> Unit, modifier: Modifier) {
    Box(
        modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).height(100.dp).padding(20.dp)
    ) {
        title()
    }
}