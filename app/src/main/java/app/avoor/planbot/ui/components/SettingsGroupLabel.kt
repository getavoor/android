package app.avoor.planbot.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.planbot.ui.theme.PlanbotTheme

/**
 * A label style for the titles of groups of settings.
 */
// TODO maybe move into m3x and replace SettingsTitle
@Composable
fun SettingsGroupLabel(
    content: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = content,
        style = MaterialTheme.typography.titleMediumEmphasized,
        modifier = Modifier.padding(4.dp).then(modifier),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
fun SettingsGroupLabelPreview() {
    PlanbotTheme {
        Surface {
            SettingsGroupLabel("Main settings")
        }
    }
}