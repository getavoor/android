package app.avoor.planbot.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * An indeterminate loading bar.
 */
@Composable
fun LoadingBar() {
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth()
    )
}