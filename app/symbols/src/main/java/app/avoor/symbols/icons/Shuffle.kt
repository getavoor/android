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

val Icons.Shuffle: ImageVector
    get() {
        if (_shuffle != null) {
            return _shuffle!!
        }
        _shuffle = Builder(name = "Shuffle", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(560.0f, 800.0f)
                lineTo(560.0f, 720.0f)
                lineTo(664.0f, 720.0f)
                lineTo(537.0f, 593.0f)
                lineTo(594.0f, 536.0f)
                lineTo(720.0f, 662.0f)
                lineTo(720.0f, 560.0f)
                lineTo(800.0f, 560.0f)
                lineTo(800.0f, 800.0f)
                lineTo(560.0f, 800.0f)
                close()
                moveTo(216.0f, 800.0f)
                lineTo(160.0f, 744.0f)
                lineTo(664.0f, 240.0f)
                lineTo(560.0f, 240.0f)
                lineTo(560.0f, 160.0f)
                lineTo(800.0f, 160.0f)
                lineTo(800.0f, 400.0f)
                lineTo(720.0f, 400.0f)
                lineTo(720.0f, 296.0f)
                lineTo(216.0f, 800.0f)
                close()
                moveTo(367.0f, 423.0f)
                lineTo(160.0f, 216.0f)
                lineTo(216.0f, 160.0f)
                lineTo(423.0f, 367.0f)
                lineTo(367.0f, 423.0f)
                close()
            }
        }
        .build()
        return _shuffle!!
    }

/**
 * Avoor synonym for [Icons.Shuffle]
 */
@Deprecated("Replace with Icons.Shuffle.", ReplaceWith(
    "Icons.Shuffle",
    "app.avoor.symbols.Icons"
))
val Icons.Yinyang: ImageVector
    get() { return Icons.Shuffle }

private var _shuffle: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Shuffle, contentDescription = "")
    }
}
