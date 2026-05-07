package app.avoor.planbot.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import coil.request.ImageRequest
import app.avoor.planbot.ui.ImageMorphComposable
import app.avoor.planbot.ui.MorphComposable
import app.avoor.planbot.ui.theme.PlanbotTheme

val star = RoundedPolygon.star(numVerticesPerRadius = 15, innerRadius = 0.892F, rounding = CornerRounding(
    1.0F, 0.0F
), innerRounding = CornerRounding(1.0F, 0.0F)).normalized()

/**
 * An animated Material 3 star, primarily for use during discovery.
 *
 * At first, the star constantly changes shape to symbolize the recommendation
 * system "thinking", changing the corner shape from clearly divided circles to
 * a softer shape. Then, the star expands and stops morphing, and the provided
 * image is shown.
 *
 * @param imageRequest the image to show at the end. If none is provided, the primary color is used.
 * @param expand if true, the star stops morphing, expands, and the provided image is shown.
 */
@Composable
fun MorphingStar(
    imageRequest: ImageRequest?,
    expand: Boolean = false
) {

    val discoveryThinkingTransition = rememberInfiniteTransition(label = "DiscoveryThinking")

    // Creates a child animation of float type as a part of the [InfiniteTransition].
    val starInnerRoundRadius by discoveryThinkingTransition.animateFloat(
        initialValue = 0.928F,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            // Infinitely repeating a 1000ms tween animation using default easing curve.
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "DiscoveryThinkingIRR"
    )
    // Creates a child animation of float type as a part of the [InfiniteTransition].
    val starOuterRoundingRadius by discoveryThinkingTransition.animateFloat(
        initialValue = 0.928F,
        targetValue = 0.756f,
        animationSpec = infiniteRepeatable(
            // Infinitely repeating a 1000ms tween animation using default easing curve.
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "DiscoveryThinkingORR"
    )
    val starInnerRadius by discoveryThinkingTransition.animateFloat(
        initialValue = 0.928F,
        targetValue = 0.756f,
        animationSpec = infiniteRepeatable(
            // Infinitely repeating a 1000ms tween animation using default easing curve.
            animation = tween(500, 550),
            repeatMode = RepeatMode.Reverse
        ), label = "DiscoveryThinkingIR"
    )

    val starThinking = RoundedPolygon.star(numVerticesPerRadius = 12, innerRadius = starInnerRadius, rounding = CornerRounding(
        starOuterRoundingRadius, 0.0F
    ), innerRounding = CornerRounding(starInnerRoundRadius, 0.0F)
    ).normalized()

    val morph = Morph(starThinking, star)
    val morphProgress: Float by animateFloatAsState(
        if (expand) 1f else 0f,
        label = "DiscoveryMorphProgress"
    )
    val size: Dp by animateDpAsState(
        if (expand) 256.dp else 128.dp,
        label = "DiscoveryStarSize"
    )

    if (imageRequest != null && expand) {
        ImageMorphComposable(
            sizedMorph = morph,
            progress = morphProgress,
            imageRequest = imageRequest,
            size = size
        )
    } else {
        MorphComposable(
            sizedMorph = morph,
            progress = morphProgress,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(size)
        )
    }
}

@Preview
@Composable
fun MorphingStarPreview() {
    PlanbotTheme {
        MorphingStar(null, false)
    }
}
