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

val Icons.ModeHeat: ImageVector
    get() {
        if (_modeHeat != null) {
            return _modeHeat!!
        }
        _modeHeat = Builder(name = "ModeHeat", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(240.0f, 560.0f)
                quadTo(240.0f, 612.0f, 261.0f, 658.5f)
                quadTo(282.0f, 705.0f, 321.0f, 740.0f)
                quadTo(320.0f, 735.0f, 320.0f, 731.0f)
                quadTo(320.0f, 727.0f, 320.0f, 722.0f)
                quadTo(320.0f, 690.0f, 332.0f, 662.0f)
                quadTo(344.0f, 634.0f, 367.0f, 611.0f)
                lineTo(480.0f, 500.0f)
                lineTo(593.0f, 611.0f)
                quadTo(616.0f, 634.0f, 628.0f, 662.0f)
                quadTo(640.0f, 690.0f, 640.0f, 722.0f)
                quadTo(640.0f, 727.0f, 640.0f, 731.0f)
                quadTo(640.0f, 735.0f, 639.0f, 740.0f)
                quadTo(678.0f, 705.0f, 699.0f, 658.5f)
                quadTo(720.0f, 612.0f, 720.0f, 560.0f)
                quadTo(720.0f, 510.0f, 701.5f, 465.5f)
                quadTo(683.0f, 421.0f, 648.0f, 386.0f)
                lineTo(648.0f, 386.0f)
                quadTo(628.0f, 399.0f, 606.0f, 405.5f)
                quadTo(584.0f, 412.0f, 561.0f, 412.0f)
                quadTo(499.0f, 412.0f, 453.5f, 371.0f)
                quadTo(408.0f, 330.0f, 401.0f, 270.0f)
                lineTo(401.0f, 270.0f)
                quadTo(362.0f, 303.0f, 332.0f, 338.5f)
                quadTo(302.0f, 374.0f, 281.5f, 410.5f)
                quadTo(261.0f, 447.0f, 250.5f, 485.0f)
                quadTo(240.0f, 523.0f, 240.0f, 560.0f)
                close()
                moveTo(480.0f, 612.0f)
                lineTo(423.0f, 668.0f)
                quadTo(412.0f, 679.0f, 406.0f, 693.0f)
                quadTo(400.0f, 707.0f, 400.0f, 722.0f)
                quadTo(400.0f, 754.0f, 423.5f, 777.0f)
                quadTo(447.0f, 800.0f, 480.0f, 800.0f)
                quadTo(513.0f, 800.0f, 536.5f, 777.0f)
                quadTo(560.0f, 754.0f, 560.0f, 722.0f)
                quadTo(560.0f, 706.0f, 554.0f, 692.5f)
                quadTo(548.0f, 679.0f, 537.0f, 668.0f)
                lineTo(480.0f, 612.0f)
                close()
                moveTo(480.0f, 120.0f)
                lineTo(480.0f, 252.0f)
                quadTo(480.0f, 286.0f, 503.5f, 309.0f)
                quadTo(527.0f, 332.0f, 561.0f, 332.0f)
                quadTo(579.0f, 332.0f, 594.5f, 324.5f)
                quadTo(610.0f, 317.0f, 622.0f, 302.0f)
                lineTo(640.0f, 280.0f)
                quadTo(714.0f, 322.0f, 757.0f, 397.0f)
                quadTo(800.0f, 472.0f, 800.0f, 560.0f)
                quadTo(800.0f, 694.0f, 707.0f, 787.0f)
                quadTo(614.0f, 880.0f, 480.0f, 880.0f)
                quadTo(346.0f, 880.0f, 253.0f, 787.0f)
                quadTo(160.0f, 694.0f, 160.0f, 560.0f)
                quadTo(160.0f, 431.0f, 246.5f, 315.0f)
                quadTo(333.0f, 199.0f, 480.0f, 120.0f)
                close()
            }
        }
        .build()
        return _modeHeat!!
    }

/**
 * Avoor synonym for [Icons.ModeHeat]
 */
val Icons.Streak: ImageVector
    get() { return Icons.ModeHeat }

private var _modeHeat: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.ModeHeat, contentDescription = "")
    }
}
@Preview
@Composable
private fun PreviewSynonym() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Streak, contentDescription = "")
    }
}
