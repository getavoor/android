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

val Icons.Settings: ImageVector
    get() {
        val current = _settings
        if (current != null) return current

        return ImageVector.Builder(
            name = "PlanbotTheme.Settings",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // m370 -80 -16 -128 q-13 -5 -24.5 -12 T307 -235 l-119 50 L78 -375 l103 -78 q-1 -7 -1 -13.5 v-27 q0 -6.5 1 -13.5 L78 -585 l110 -190 119 50 q11 -8 23 -15 t24 -12 l16 -128 h220 l16 128 q13 5 24.5 12 t22.5 15 l119 -50 110 190 -103 78 q1 7 1 13.5 v27 q0 6.5 -2 13.5 l103 78 -110 190 -118 -50 q-11 8 -23 15 t-24 12 L590 -80z m70 -80 h79 l14 -106 q31 -8 57.5 -23.5 T639 -327 l99 41 39 -68 -86 -65 q5 -14 7 -29.5 t2 -31.5 -2 -31.5 -7 -29.5 l86 -65 -39 -68 -99 42 q-22 -23 -48.5 -38.5 T533 -694 l-13 -106 h-79 l-14 106 q-31 8 -57.5 23.5 T321 -633 l-99 -41 -39 68 86 64 q-5 15 -7 30 t-2 32 q0 16 2 31 t7 30 l-86 65 39 68 99 -42 q22 23 48.5 38.5 T427 -266z m42 -180 q58 0 99 -41 t41 -99 -41 -99 -99 -41 q-59 0 -99.5 41 T342 -480 t40.5 99 99.5 41 m-2 -140
            path(
                fill = SolidColor(Color(0xFF1F1F1F)),
            ) {
                // M 370 880
                moveTo(x = 370.0f, y = 880.0f)
                // l -16 -128
                lineToRelative(dx = -16.0f, dy = -128.0f)
                // q -13 -5 -24.5 -12
                quadToRelative(
                    dx1 = -13.0f,
                    dy1 = -5.0f,
                    dx2 = -24.5f,
                    dy2 = -12.0f,
                )
                // T 307 725
                reflectiveQuadTo(
                    x1 = 307.0f,
                    y1 = 725.0f,
                )
                // l -119 50
                lineToRelative(dx = -119.0f, dy = 50.0f)
                // L 78 585
                lineTo(x = 78.0f, y = 585.0f)
                // l 103 -78
                lineToRelative(dx = 103.0f, dy = -78.0f)
                // q -1 -7 -1 -13.5
                quadToRelative(
                    dx1 = -1.0f,
                    dy1 = -7.0f,
                    dx2 = -1.0f,
                    dy2 = -13.5f,
                )
                // l 0 -27
                lineToRelative(dx = 0.0f, dy = -27.0f)
                // q 0 -6.5 1 -13.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -6.5f,
                    dx2 = 1.0f,
                    dy2 = -13.5f,
                )
                // L 78 375
                lineTo(x = 78.0f, y = 375.0f)
                // l 110 -190
                lineToRelative(dx = 110.0f, dy = -190.0f)
                // l 119 50
                lineToRelative(dx = 119.0f, dy = 50.0f)
                // q 11 -8 23 -15
                quadToRelative(
                    dx1 = 11.0f,
                    dy1 = -8.0f,
                    dx2 = 23.0f,
                    dy2 = -15.0f,
                )
                // t 24 -12
                reflectiveQuadToRelative(
                    dx1 = 24.0f,
                    dy1 = -12.0f,
                )
                // l 16 -128
                lineToRelative(dx = 16.0f, dy = -128.0f)
                // l 220 0
                lineToRelative(dx = 220.0f, dy = 0.0f)
                // l 16 128
                lineToRelative(dx = 16.0f, dy = 128.0f)
                // q 13 5 24.5 12
                quadToRelative(
                    dx1 = 13.0f,
                    dy1 = 5.0f,
                    dx2 = 24.5f,
                    dy2 = 12.0f,
                )
                // t 22.5 15
                reflectiveQuadToRelative(
                    dx1 = 22.5f,
                    dy1 = 15.0f,
                )
                // l 119 -50
                lineToRelative(dx = 119.0f, dy = -50.0f)
                // l 110 190
                lineToRelative(dx = 110.0f, dy = 190.0f)
                // l -103 78
                lineToRelative(dx = -103.0f, dy = 78.0f)
                // q 1 7 1 13.5
                quadToRelative(
                    dx1 = 1.0f,
                    dy1 = 7.0f,
                    dx2 = 1.0f,
                    dy2 = 13.5f,
                )
                // l 0 27
                lineToRelative(dx = 0.0f, dy = 27.0f)
                // q 0 6.5 -2 13.5
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 6.5f,
                    dx2 = -2.0f,
                    dy2 = 13.5f,
                )
                // l 103 78
                lineToRelative(dx = 103.0f, dy = 78.0f)
                // l -110 190
                lineToRelative(dx = -110.0f, dy = 190.0f)
                // l -118 -50
                lineToRelative(dx = -118.0f, dy = -50.0f)
                // q -11 8 -23 15
                quadToRelative(
                    dx1 = -11.0f,
                    dy1 = 8.0f,
                    dx2 = -23.0f,
                    dy2 = 15.0f,
                )
                // t -24 12
                reflectiveQuadToRelative(
                    dx1 = -24.0f,
                    dy1 = 12.0f,
                )
                // L 590 880z
                lineTo(x = 590.0f, y = 880.0f)
                close()
                // m 70 -80
                moveToRelative(dx = 70.0f, dy = -80.0f)
                // l 79 0
                lineToRelative(dx = 79.0f, dy = 0.0f)
                // l 14 -106
                lineToRelative(dx = 14.0f, dy = -106.0f)
                // q 31 -8 57.5 -23.5
                quadToRelative(
                    dx1 = 31.0f,
                    dy1 = -8.0f,
                    dx2 = 57.5f,
                    dy2 = -23.5f,
                )
                // T 639 633
                reflectiveQuadTo(
                    x1 = 639.0f,
                    y1 = 633.0f,
                )
                // l 99 41
                lineToRelative(dx = 99.0f, dy = 41.0f)
                // l 39 -68
                lineToRelative(dx = 39.0f, dy = -68.0f)
                // l -86 -65
                lineToRelative(dx = -86.0f, dy = -65.0f)
                // q 5 -14 7 -29.5
                quadToRelative(
                    dx1 = 5.0f,
                    dy1 = -14.0f,
                    dx2 = 7.0f,
                    dy2 = -29.5f,
                )
                // t 2 -31.5
                reflectiveQuadToRelative(
                    dx1 = 2.0f,
                    dy1 = -31.5f,
                )
                // t -2 -31.5
                reflectiveQuadToRelative(
                    dx1 = -2.0f,
                    dy1 = -31.5f,
                )
                // t -7 -29.5
                reflectiveQuadToRelative(
                    dx1 = -7.0f,
                    dy1 = -29.5f,
                )
                // l 86 -65
                lineToRelative(dx = 86.0f, dy = -65.0f)
                // l -39 -68
                lineToRelative(dx = -39.0f, dy = -68.0f)
                // l -99 42
                lineToRelative(dx = -99.0f, dy = 42.0f)
                // q -22 -23 -48.5 -38.5
                quadToRelative(
                    dx1 = -22.0f,
                    dy1 = -23.0f,
                    dx2 = -48.5f,
                    dy2 = -38.5f,
                )
                // T 533 266
                reflectiveQuadTo(
                    x1 = 533.0f,
                    y1 = 266.0f,
                )
                // l -13 -106
                lineToRelative(dx = -13.0f, dy = -106.0f)
                // l -79 0
                lineToRelative(dx = -79.0f, dy = 0.0f)
                // l -14 106
                lineToRelative(dx = -14.0f, dy = 106.0f)
                // q -31 8 -57.5 23.5
                quadToRelative(
                    dx1 = -31.0f,
                    dy1 = 8.0f,
                    dx2 = -57.5f,
                    dy2 = 23.5f,
                )
                // T 321 327
                reflectiveQuadTo(
                    x1 = 321.0f,
                    y1 = 327.0f,
                )
                // l -99 -41
                lineToRelative(dx = -99.0f, dy = -41.0f)
                // l -39 68
                lineToRelative(dx = -39.0f, dy = 68.0f)
                // l 86 64
                lineToRelative(dx = 86.0f, dy = 64.0f)
                // q -5 15 -7 30
                quadToRelative(
                    dx1 = -5.0f,
                    dy1 = 15.0f,
                    dx2 = -7.0f,
                    dy2 = 30.0f,
                )
                // t -2 32
                reflectiveQuadToRelative(
                    dx1 = -2.0f,
                    dy1 = 32.0f,
                )
                // q 0 16 2 31
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 16.0f,
                    dx2 = 2.0f,
                    dy2 = 31.0f,
                )
                // t 7 30
                reflectiveQuadToRelative(
                    dx1 = 7.0f,
                    dy1 = 30.0f,
                )
                // l -86 65
                lineToRelative(dx = -86.0f, dy = 65.0f)
                // l 39 68
                lineToRelative(dx = 39.0f, dy = 68.0f)
                // l 99 -42
                lineToRelative(dx = 99.0f, dy = -42.0f)
                // q 22 23 48.5 38.5
                quadToRelative(
                    dx1 = 22.0f,
                    dy1 = 23.0f,
                    dx2 = 48.5f,
                    dy2 = 38.5f,
                )
                // T 427 694z
                reflectiveQuadTo(
                    x1 = 427.0f,
                    y1 = 694.0f,
                )
                close()
                // m 42 -180
                moveToRelative(dx = 42.0f, dy = -180.0f)
                // q 58 0 99 -41
                quadToRelative(
                    dx1 = 58.0f,
                    dy1 = 0.0f,
                    dx2 = 99.0f,
                    dy2 = -41.0f,
                )
                // t 41 -99
                reflectiveQuadToRelative(
                    dx1 = 41.0f,
                    dy1 = -99.0f,
                )
                // t -41 -99
                reflectiveQuadToRelative(
                    dx1 = -41.0f,
                    dy1 = -99.0f,
                )
                // t -99 -41
                reflectiveQuadToRelative(
                    dx1 = -99.0f,
                    dy1 = -41.0f,
                )
                // q -59 0 -99.5 41
                quadToRelative(
                    dx1 = -59.0f,
                    dy1 = 0.0f,
                    dx2 = -99.5f,
                    dy2 = 41.0f,
                )
                // T 342 480
                reflectiveQuadTo(
                    x1 = 342.0f,
                    y1 = 480.0f,
                )
                // t 40.5 99
                reflectiveQuadToRelative(
                    dx1 = 40.5f,
                    dy1 = 99.0f,
                )
                // t 99.5 41
                reflectiveQuadToRelative(
                    dx1 = 99.5f,
                    dy1 = 41.0f,
                )
                // m -2 -140
                moveToRelative(dx = -2.0f, dy = -140.0f)
            }
        }.build().also { _settings = it }
    }

@Preview
@Composable
private fun IconPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(
            imageVector = Icons.Settings,
            contentDescription = null,
            modifier = Modifier
                .width((960.0).dp)
                .height((960.0).dp),
        )
    }
}

@Suppress("ObjectPropertyName")
private var _settings: ImageVector? = null
