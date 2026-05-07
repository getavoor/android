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

val Icons.Search: ImageVector
    get() {
        if (_search != null) {
            return _search!!
        }
        _search = Builder(name = "Search", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(784.0f, 840.0f)
                lineTo(532.0f, 588.0f)
                quadTo(502.0f, 612.0f, 463.0f, 626.0f)
                quadTo(424.0f, 640.0f, 380.0f, 640.0f)
                quadTo(271.0f, 640.0f, 195.5f, 564.5f)
                quadTo(120.0f, 489.0f, 120.0f, 380.0f)
                quadTo(120.0f, 271.0f, 195.5f, 195.5f)
                quadTo(271.0f, 120.0f, 380.0f, 120.0f)
                quadTo(489.0f, 120.0f, 564.5f, 195.5f)
                quadTo(640.0f, 271.0f, 640.0f, 380.0f)
                quadTo(640.0f, 424.0f, 626.0f, 463.0f)
                quadTo(612.0f, 502.0f, 588.0f, 532.0f)
                lineTo(840.0f, 784.0f)
                lineTo(784.0f, 840.0f)
                close()
                moveTo(380.0f, 560.0f)
                quadTo(455.0f, 560.0f, 507.5f, 507.5f)
                quadTo(560.0f, 455.0f, 560.0f, 380.0f)
                quadTo(560.0f, 305.0f, 507.5f, 252.5f)
                quadTo(455.0f, 200.0f, 380.0f, 200.0f)
                quadTo(305.0f, 200.0f, 252.5f, 252.5f)
                quadTo(200.0f, 305.0f, 200.0f, 380.0f)
                quadTo(200.0f, 455.0f, 252.5f, 507.5f)
                quadTo(305.0f, 560.0f, 380.0f, 560.0f)
                close()
            }
        }
        .build()
        return _search!!
    }

private var _search: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Search, contentDescription = "")
    }
}
