@file:Suppress("UnusedReceiverParameter")
package app.avoor.symbols.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.symbols.Icons

val Icons.Remove: ImageVector
    get() {
        val current = _remove
        if (current != null) return current

        return ImageVector.Builder(
            name = "PlanbotTheme.Remove",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // M200 -440 v-80 h560 v80z
            path(
                fill = SolidColor(Color(0xFF1F1F1F)),
            ) {
                // M 200 520
                moveTo(x = 200.0f, y = 520.0f)
                // l 0 -80
                lineToRelative(dx = 0.0f, dy = -80.0f)
                // l 560 0
                lineToRelative(dx = 560.0f, dy = 0.0f)
                // l 0 80z
                lineToRelative(dx = 0.0f, dy = 80.0f)
                close()
            }
        }.build().also { _remove = it }
    }

@Preview
@Composable
private fun IconPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(
            imageVector = Icons.Remove,
            contentDescription = null,
            modifier = Modifier
                .width((960.0).dp)
                .height((960.0).dp),
        )
    }
}

@Suppress("ObjectPropertyName")
private var _remove: ImageVector? = null
