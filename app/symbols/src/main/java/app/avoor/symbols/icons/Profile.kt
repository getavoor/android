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

val Icons.Profile: ImageVector
    get() {
        val current = _profile
        if (current != null) return current

        return ImageVector.Builder(
            name = "PlanbotTheme.Profile",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // M234 -276 q51 -39 114 -61.5 T480 -360 t132 22.5 T726 -276 q35 -41 54.5 -93 T800 -480 q0 -133 -93.5 -226.5 T480 -800 t-226.5 93.5 T160 -480 q0 59 19.5 111 t54.5 93 m146.5 -204.5 Q340 -521 340 -580 t40.5 -99.5 T480 -720 t99.5 40.5 T620 -580 t-40.5 99.5 T480 -440 t-99.5 -40.5 M480 -80 q-83 0 -156 -31.5 T197 -197 t-85.5 -127 T80 -480 t31.5 -156 T197 -763 t127 -85.5 T480 -880 t156 31.5 T763 -763 t85.5 127 T880 -480 t-31.5 156 T763 -197 t-127 85.5 T480 -80 m100 -95.5 q47 -15.5 86 -44.5 -39 -29 -86 -44.5 T480 -280 t-100 15.5 -86 44.5 q39 29 86 44.5 T480 -160 t100 -15.5 M523 -537 q17 -17 17 -43 t-17 -43 -43 -17 -43 17 -17 43 17 43 43 17 43 -17 m-43 317
            path(
                fill = SolidColor(Color(0xFF1F1F1F)),
            ) {
                // M 234 684
                moveTo(x = 234.0f, y = 684.0f)
                // q 51 -39 114 -61.5
                quadToRelative(
                    dx1 = 51.0f,
                    dy1 = -39.0f,
                    dx2 = 114.0f,
                    dy2 = -61.5f,
                )
                // T 480 600
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 600.0f,
                )
                // t 132 22.5
                reflectiveQuadToRelative(
                    dx1 = 132.0f,
                    dy1 = 22.5f,
                )
                // T 726 684
                reflectiveQuadTo(
                    x1 = 726.0f,
                    y1 = 684.0f,
                )
                // q 35 -41 54.5 -93
                quadToRelative(
                    dx1 = 35.0f,
                    dy1 = -41.0f,
                    dx2 = 54.5f,
                    dy2 = -93.0f,
                )
                // T 800 480
                reflectiveQuadTo(
                    x1 = 800.0f,
                    y1 = 480.0f,
                )
                // q 0 -133 -93.5 -226.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -133.0f,
                    dx2 = -93.5f,
                    dy2 = -226.5f,
                )
                // T 480 160
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 160.0f,
                )
                // t -226.5 93.5
                reflectiveQuadToRelative(
                    dx1 = -226.5f,
                    dy1 = 93.5f,
                )
                // T 160 480
                reflectiveQuadTo(
                    x1 = 160.0f,
                    y1 = 480.0f,
                )
                // q 0 59 19.5 111
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 59.0f,
                    dx2 = 19.5f,
                    dy2 = 111.0f,
                )
                // t 54.5 93
                reflectiveQuadToRelative(
                    dx1 = 54.5f,
                    dy1 = 93.0f,
                )
                // m 146.5 -204.5
                moveToRelative(dx = 146.5f, dy = -204.5f)
                // Q 340 439 340 380
                quadTo(
                    x1 = 340.0f,
                    y1 = 439.0f,
                    x2 = 340.0f,
                    y2 = 380.0f,
                )
                // t 40.5 -99.5
                reflectiveQuadToRelative(
                    dx1 = 40.5f,
                    dy1 = -99.5f,
                )
                // T 480 240
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 240.0f,
                )
                // t 99.5 40.5
                reflectiveQuadToRelative(
                    dx1 = 99.5f,
                    dy1 = 40.5f,
                )
                // T 620 380
                reflectiveQuadTo(
                    x1 = 620.0f,
                    y1 = 380.0f,
                )
                // t -40.5 99.5
                reflectiveQuadToRelative(
                    dx1 = -40.5f,
                    dy1 = 99.5f,
                )
                // T 480 520
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 520.0f,
                )
                // t -99.5 -40.5
                reflectiveQuadToRelative(
                    dx1 = -99.5f,
                    dy1 = -40.5f,
                )
                // M 480 880
                moveTo(x = 480.0f, y = 880.0f)
                // q -83 0 -156 -31.5
                quadToRelative(
                    dx1 = -83.0f,
                    dy1 = 0.0f,
                    dx2 = -156.0f,
                    dy2 = -31.5f,
                )
                // T 197 763
                reflectiveQuadTo(
                    x1 = 197.0f,
                    y1 = 763.0f,
                )
                // t -85.5 -127
                reflectiveQuadToRelative(
                    dx1 = -85.5f,
                    dy1 = -127.0f,
                )
                // T 80 480
                reflectiveQuadTo(
                    x1 = 80.0f,
                    y1 = 480.0f,
                )
                // t 31.5 -156
                reflectiveQuadToRelative(
                    dx1 = 31.5f,
                    dy1 = -156.0f,
                )
                // T 197 197
                reflectiveQuadTo(
                    x1 = 197.0f,
                    y1 = 197.0f,
                )
                // t 127 -85.5
                reflectiveQuadToRelative(
                    dx1 = 127.0f,
                    dy1 = -85.5f,
                )
                // T 480 80
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 80.0f,
                )
                // t 156 31.5
                reflectiveQuadToRelative(
                    dx1 = 156.0f,
                    dy1 = 31.5f,
                )
                // T 763 197
                reflectiveQuadTo(
                    x1 = 763.0f,
                    y1 = 197.0f,
                )
                // t 85.5 127
                reflectiveQuadToRelative(
                    dx1 = 85.5f,
                    dy1 = 127.0f,
                )
                // T 880 480
                reflectiveQuadTo(
                    x1 = 880.0f,
                    y1 = 480.0f,
                )
                // t -31.5 156
                reflectiveQuadToRelative(
                    dx1 = -31.5f,
                    dy1 = 156.0f,
                )
                // T 763 763
                reflectiveQuadTo(
                    x1 = 763.0f,
                    y1 = 763.0f,
                )
                // t -127 85.5
                reflectiveQuadToRelative(
                    dx1 = -127.0f,
                    dy1 = 85.5f,
                )
                // T 480 880
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 880.0f,
                )
                // m 100 -95.5
                moveToRelative(dx = 100.0f, dy = -95.5f)
                // q 47 -15.5 86 -44.5
                quadToRelative(
                    dx1 = 47.0f,
                    dy1 = -15.5f,
                    dx2 = 86.0f,
                    dy2 = -44.5f,
                )
                // q -39 -29 -86 -44.5
                quadToRelative(
                    dx1 = -39.0f,
                    dy1 = -29.0f,
                    dx2 = -86.0f,
                    dy2 = -44.5f,
                )
                // T 480 680
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 680.0f,
                )
                // t -100 15.5
                reflectiveQuadToRelative(
                    dx1 = -100.0f,
                    dy1 = 15.5f,
                )
                // t -86 44.5
                reflectiveQuadToRelative(
                    dx1 = -86.0f,
                    dy1 = 44.5f,
                )
                // q 39 29 86 44.5
                quadToRelative(
                    dx1 = 39.0f,
                    dy1 = 29.0f,
                    dx2 = 86.0f,
                    dy2 = 44.5f,
                )
                // T 480 800
                reflectiveQuadTo(
                    x1 = 480.0f,
                    y1 = 800.0f,
                )
                // t 100 -15.5
                reflectiveQuadToRelative(
                    dx1 = 100.0f,
                    dy1 = -15.5f,
                )
                // M 523 423
                moveTo(x = 523.0f, y = 423.0f)
                // q 17 -17 17 -43
                quadToRelative(
                    dx1 = 17.0f,
                    dy1 = -17.0f,
                    dx2 = 17.0f,
                    dy2 = -43.0f,
                )
                // t -17 -43
                reflectiveQuadToRelative(
                    dx1 = -17.0f,
                    dy1 = -43.0f,
                )
                // t -43 -17
                reflectiveQuadToRelative(
                    dx1 = -43.0f,
                    dy1 = -17.0f,
                )
                // t -43 17
                reflectiveQuadToRelative(
                    dx1 = -43.0f,
                    dy1 = 17.0f,
                )
                // t -17 43
                reflectiveQuadToRelative(
                    dx1 = -17.0f,
                    dy1 = 43.0f,
                )
                // t 17 43
                reflectiveQuadToRelative(
                    dx1 = 17.0f,
                    dy1 = 43.0f,
                )
                // t 43 17
                reflectiveQuadToRelative(
                    dx1 = 43.0f,
                    dy1 = 17.0f,
                )
                // t 43 -17
                reflectiveQuadToRelative(
                    dx1 = 43.0f,
                    dy1 = -17.0f,
                )
                // m -43 317
                moveToRelative(dx = -43.0f, dy = 317.0f)
            }
        }.build().also { _profile = it }
    }

@Preview
@Composable
private fun IconPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(
            imageVector = Icons.Profile,
            contentDescription = null,
            modifier = Modifier
                .width((960.0).dp)
                .height((960.0).dp),
        )
    }
}

@Suppress("ObjectPropertyName")
private var _profile: ImageVector? = null
