@file:Suppress("UnusedReceiverParameter")
package app.avoor.symbols.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.symbols.Icons

val Icons.Warning: ImageVector
    get() {
        if (_warning != null) {
            return _warning!!
        }
        _warning = Builder(name = "Warning", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(40.0f, 840.0f)
                lineTo(480.0f, 80.0f)
                lineTo(920.0f, 840.0f)
                lineTo(40.0f, 840.0f)
                close()
                moveTo(178.0f, 760.0f)
                lineTo(782.0f, 760.0f)
                lineTo(480.0f, 240.0f)
                lineTo(178.0f, 760.0f)
                close()
                moveTo(480.0f, 720.0f)
                quadTo(497.0f, 720.0f, 508.5f, 708.5f)
                quadTo(520.0f, 697.0f, 520.0f, 680.0f)
                quadTo(520.0f, 663.0f, 508.5f, 651.5f)
                quadTo(497.0f, 640.0f, 480.0f, 640.0f)
                quadTo(463.0f, 640.0f, 451.5f, 651.5f)
                quadTo(440.0f, 663.0f, 440.0f, 680.0f)
                quadTo(440.0f, 697.0f, 451.5f, 708.5f)
                quadTo(463.0f, 720.0f, 480.0f, 720.0f)
                close()
                moveTo(440.0f, 600.0f)
                lineTo(520.0f, 600.0f)
                lineTo(520.0f, 400.0f)
                lineTo(440.0f, 400.0f)
                lineTo(440.0f, 600.0f)
                close()
                moveTo(480.0f, 500.0f)
                lineTo(480.0f, 500.0f)
                lineTo(480.0f, 500.0f)
                close()
            }
        }
        .build()
        return _warning!!
    }

private var _warning: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Warning, contentDescription = "")
    }
}
