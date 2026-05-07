package app.avoor.planbot.ui.components.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.planbot.ui.theme.PlanbotTheme

@Preview(showBackground = true)
@Composable
private fun TimelinePreview() {
    PlanbotTheme() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TimelineNode() { modifier -> MessageBubble(modifier, containerColor = Color.LightGray) }
            TimelineNode() { modifier -> MessageBubble(modifier, containerColor = Color.Red) }
            TimelineNode() { modifier -> MessageBubble(modifier, containerColor = Color.Cyan) }
        }
    }
}

