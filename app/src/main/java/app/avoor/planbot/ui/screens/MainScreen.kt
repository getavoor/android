package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.navigation.compose.rememberNavController
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

    val exitAlwaysScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)
    Scaffold(modifier = Modifier.nestedScroll(exitAlwaysScrollBehavior)) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
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
                        .offset(y = -ScreenOffset)
                        .zIndex(1f),
                scrollBehavior = exitAlwaysScrollBehavior,
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