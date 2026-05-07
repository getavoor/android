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

val Icons.Target: ImageVector
    get() {
        if (_target != null) {
            return _target!!
        }
        _target = Builder(name = "Target", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.0f, 880.0f)
                quadTo(397.0f, 880.0f, 324.0f, 848.5f)
                quadTo(251.0f, 817.0f, 197.0f, 763.0f)
                quadTo(143.0f, 709.0f, 111.5f, 636.0f)
                quadTo(80.0f, 563.0f, 80.0f, 480.0f)
                quadTo(80.0f, 397.0f, 111.5f, 324.0f)
                quadTo(143.0f, 251.0f, 197.0f, 197.0f)
                quadTo(251.0f, 143.0f, 324.0f, 111.5f)
                quadTo(397.0f, 80.0f, 480.0f, 80.0f)
                quadTo(563.0f, 80.0f, 636.0f, 111.5f)
                quadTo(709.0f, 143.0f, 763.0f, 197.0f)
                quadTo(817.0f, 251.0f, 848.5f, 324.0f)
                quadTo(880.0f, 397.0f, 880.0f, 480.0f)
                quadTo(880.0f, 563.0f, 848.5f, 636.0f)
                quadTo(817.0f, 709.0f, 763.0f, 763.0f)
                quadTo(709.0f, 817.0f, 636.0f, 848.5f)
                quadTo(563.0f, 880.0f, 480.0f, 880.0f)
                close()
                moveTo(480.0f, 800.0f)
                quadTo(614.0f, 800.0f, 707.0f, 707.0f)
                quadTo(800.0f, 614.0f, 800.0f, 480.0f)
                quadTo(800.0f, 346.0f, 707.0f, 253.0f)
                quadTo(614.0f, 160.0f, 480.0f, 160.0f)
                quadTo(346.0f, 160.0f, 253.0f, 253.0f)
                quadTo(160.0f, 346.0f, 160.0f, 480.0f)
                quadTo(160.0f, 614.0f, 253.0f, 707.0f)
                quadTo(346.0f, 800.0f, 480.0f, 800.0f)
                close()
                moveTo(480.0f, 720.0f)
                quadTo(380.0f, 720.0f, 310.0f, 650.0f)
                quadTo(240.0f, 580.0f, 240.0f, 480.0f)
                quadTo(240.0f, 380.0f, 310.0f, 310.0f)
                quadTo(380.0f, 240.0f, 480.0f, 240.0f)
                quadTo(580.0f, 240.0f, 650.0f, 310.0f)
                quadTo(720.0f, 380.0f, 720.0f, 480.0f)
                quadTo(720.0f, 580.0f, 650.0f, 650.0f)
                quadTo(580.0f, 720.0f, 480.0f, 720.0f)
                close()
                moveTo(480.0f, 640.0f)
                quadTo(546.0f, 640.0f, 593.0f, 593.0f)
                quadTo(640.0f, 546.0f, 640.0f, 480.0f)
                quadTo(640.0f, 414.0f, 593.0f, 367.0f)
                quadTo(546.0f, 320.0f, 480.0f, 320.0f)
                quadTo(414.0f, 320.0f, 367.0f, 367.0f)
                quadTo(320.0f, 414.0f, 320.0f, 480.0f)
                quadTo(320.0f, 546.0f, 367.0f, 593.0f)
                quadTo(414.0f, 640.0f, 480.0f, 640.0f)
                close()
                moveTo(480.0f, 560.0f)
                quadTo(447.0f, 560.0f, 423.5f, 536.5f)
                quadTo(400.0f, 513.0f, 400.0f, 480.0f)
                quadTo(400.0f, 447.0f, 423.5f, 423.5f)
                quadTo(447.0f, 400.0f, 480.0f, 400.0f)
                quadTo(513.0f, 400.0f, 536.5f, 423.5f)
                quadTo(560.0f, 447.0f, 560.0f, 480.0f)
                quadTo(560.0f, 513.0f, 536.5f, 536.5f)
                quadTo(513.0f, 560.0f, 480.0f, 560.0f)
                close()
            }
        }
        .build()
        return _target!!
    }

private var _target: ImageVector? = null

/**
 * Avoor synonym for [Icons.Target]
 */
val Icons.Focus: ImageVector
    get() { return Icons.Target }

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Target, contentDescription = "")
    }
}

@Preview
@Composable
private fun PreviewSynonym() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Focus, contentDescription = "")
    }
}
