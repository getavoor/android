package app.avoor.planbot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.timeline.HOUR_HEIGHT
import app.avoor.planbot.ui.theme.PlanbotTheme
import app.avoor.planbot.util.DateTimeFormats
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * A card for a calendar event.
 */
@Composable
fun EventCard(
    name: String,
    dateFrom: LocalDateTime,
    dateTo: LocalDateTime,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Surface(
        // make the event card appear as one entry for assistive tech
        modifier = modifier.semantics (mergeDescendants = true) {},
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Text(
                name,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                // TODO add support for AM/PM
                text = (
                    stringResource(R.string.event_hours)
                ).format(
                    dateFrom.format(DateTimeFormats.HOUR_MINUTE),
                    dateTo.format(DateTimeFormats.HOUR_MINUTE)
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun EventCardPreview() {
    val systemTZ = TimeZone.currentSystemDefault()
    val now: LocalDateTime = Clock.System.now().toLocalDateTime(systemTZ)
    PlanbotTheme {
        EventCard(
            "Pet kitty",
            now,
            now
                .toInstant(systemTZ)
                .plus(30, DateTimeUnit.MINUTE)
                .toLocalDateTime(systemTZ),
            Modifier.size(height=HOUR_HEIGHT, width=350.dp)
        )
    }
}

