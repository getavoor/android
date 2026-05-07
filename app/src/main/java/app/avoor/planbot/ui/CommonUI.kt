package app.avoor.planbot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlin.math.max
import kotlin.math.min

@Composable
fun MorphComposable(
    sizedMorph: Morph,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    innerContent: @Composable (() -> Unit)? = null
) {
    Box(
        modifier
            .fillMaxSize()
            .drawWithContent {
                val scale = min(size.width, size.height)
                val path = sizedMorph.toComposePath(progress, scale = scale)
                drawContent()
                drawPath(path, color)
            }) {
        if (innerContent != null) innerContent()
    }
}

@Composable
fun ImageMorphComposable(
    sizedMorph: Morph,
    progress: Float,
    imageRequest: ImageRequest,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val clip = remember(sizedMorph) {
        MorphShape(morph = sizedMorph, progress = progress)
    }
    Image(
        painter = rememberAsyncImagePainter(imageRequest),
        modifier = modifier.size(size).clip(clip),
        contentDescription = null /* ignore as it isn't important */,
        contentScale = ContentScale.Crop
    )
}

fun RoundedPolygon.getBounds() = calculateBounds().let { Rect(it[0], it[1], it[2], it[3]) }
class RoundedPolygonShape(
    private val polygon: RoundedPolygon,
    private var matrix: Matrix = Matrix()
) : Shape {
    private var path = Path()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        path.rewind()
        path = polygon.toPath().asComposePath()
        matrix.reset()
        val bounds = polygon.getBounds()
        val maxDimension = max(bounds.width, bounds.height)
        matrix.scale(size.width / maxDimension, size.height / maxDimension)
        matrix.translate(-bounds.left, -bounds.top)

        path.transform(matrix)
        return Outline.Generic(path)
    }
}
fun Morph.getBounds() = calculateBounds().let { Rect(it[0], it[1], it[2], it[3]) }
class MorphShape(
    private val morph: Morph,
    private val progress: Float,
    private var matrix: Matrix = Matrix()
) : Shape {
    private var path = Path()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        path.rewind()
        path = morph.toComposePath(progress)
        matrix.reset()
        val bounds = morph.getBounds()
        val maxDimension = max(bounds.width, bounds.height)
        matrix.scale(size.width / maxDimension, size.height / maxDimension)
        matrix.translate(-bounds.left, -bounds.top)

        path.transform(matrix)
        return Outline.Generic(path)
    }
}

/**
 * Transforms the morph at a given progress into a [Path].
 * It can optionally be scaled, using the origin (0,0) as pivot point.
 */
fun Morph.toComposePath(progress: Float, scale: Float = 1f, path: Path = Path()): Path {
    var first = true
    path.rewind()
    forEachCubic(progress) { bezier ->
        if (first) {
            path.moveTo(bezier.anchor0X * scale, bezier.anchor0Y * scale)
            first = false
        }
        path.cubicTo(
            bezier.control0X * scale, bezier.control0Y * scale,
            bezier.control1X * scale, bezier.control1Y * scale,
            bezier.anchor1X * scale, bezier.anchor1Y * scale
        )
    }
    path.close()
    return path
}