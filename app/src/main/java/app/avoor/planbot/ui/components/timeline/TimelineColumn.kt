package app.avoor.planbot.ui.components.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.avoor.planbot.ui.theme.PlanbotTheme

/**
 * Height of calendar entry representing 1 hour
 */
val HOUR_HEIGHT = 200.dp

/**
 * Draws timeline lines in the background.
 */
fun Modifier.drawTimeline(color: Color) = drawBehind {
    val canvasWidth = size.width
    val canvasHeight = size.height
    // calculate the hour line height in pixels
    // (note: HOUR_HEIGHT.toPx() is not enough here because the canvas size may change)
    val hourLineHeight = canvasHeight / 24
    for (i in 0..24) {
        // draw a full width line at the calculated hour line offset
        drawLine(
            color,
            start = Offset(0F, hourLineHeight * i),
            end = Offset(canvasWidth, hourLineHeight * i),
            strokeWidth = 1F
        )
    }
}

@Composable
fun TimelineColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(Dp) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = HOUR_HEIGHT * 24) // 2. Enforce minimum height
            .fillMaxHeight() // 3. Fill remaining space if screen is taller
            .drawTimeline(MaterialTheme.colorScheme.surface)
            .then(modifier)
    ) {
        content(HOUR_HEIGHT)
    }
}

@Composable
fun TimelineLazyColumn(
    modifier: Modifier = Modifier,
    content: LazyListScope.(Dp) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(HOUR_HEIGHT * 24)
            .drawTimeline(MaterialTheme.colorScheme.primaryContainer)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        content(HOUR_HEIGHT)
    }
}