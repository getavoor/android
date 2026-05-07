package app.avoor.planbot.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.EventCard
import app.avoor.planbot.ui.components.timeline.HOUR_HEIGHT
import app.avoor.planbot.ui.components.timeline.TimelineLazyColumn
import app.avoor.planbot.ui.viewmodel.CalendarViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun CalendarScreen(
) {
    val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
    val viewState by viewModel.uiState.collectAsState()

    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val currentTimePositionDp = HOUR_HEIGHT * (now.hour + now.minute / 60f)

    Box {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = viewState.events.isNullOrEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.cal_empty_title)
                )
                Text(
                    stringResource(R.string.cal_empty_desc)
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = viewState.events?.isNotEmpty() ?: false,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val viewportHeight = maxHeight

                LaunchedEffect(Unit) {
                    val scrollOffset = (currentTimePositionDp - viewportHeight / 2).coerceAtLeast(0.dp)
                    scrollState.scrollTo(with(density) { scrollOffset.toPx().toInt() })
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    TimelineLazyColumn(
                        modifier = Modifier
                            .semantics {
                                collectionInfo = CollectionInfo(
                                    rowCount = viewState.events?.count() ?: 0,
                                    columnCount = 1
                                )
                            }
                    ) { passedHeight ->
                        itemsIndexed(viewState.events ?: listOf()) { i, event ->
                            // gap becomes a spacer
                            if (event.isGap) {
                                Spacer(
                                    Modifier.height(viewModel.getSize(passedHeight, event))
                                )
                            }
                            // anything else -> eventcard
                            else {
                                EventCard(
                                    event.name,
                                    event.startDate,
                                    event.endDate,
                                    Modifier
                                        .padding(horizontal=8.dp, vertical=0.dp)
                                        .height(viewModel.getSize(passedHeight, event))
                                        .semantics {
                                            collectionItemInfo = CollectionItemInfo(
                                                rowIndex = i,
                                                rowSpan = 0,
                                                columnIndex = 0,
                                                columnSpan = 0
                                            )
                                        }
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = currentTimePositionDp),
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 2.dp
                    )
                }
            }
        }
    }
}
