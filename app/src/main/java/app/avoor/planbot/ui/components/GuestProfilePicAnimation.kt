package app.avoor.planbot.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

val circlePolygon = RoundedPolygon(numVertices = 4,rounding = CornerRounding(1.0F, 0.0F))
    .normalized()

/**
 * An animated Material 3 star that turns into a circle, meant for profile
 * pictures of guests during group discovery.
 *
 * This composable is like [MorphingStar], but it doesn't expand, turns into a
 * circle instead of a star at the end and always shows the passed image.
 *
 * @param imageRequest the image to show. If none is provided, the primary color is used.
 * @param stabilize if true, the star that changes shape becomes a circle.
 * @param size the size of this animation.
 */
@Composable
fun GuestProfilePicAnimation(
    imageRequest: ImageRequest?,
    stabilize: Boolean = false,
    size: Dp = 96.dp
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
        ), label = "PfpThinkingIRR"
    )
    // Creates a child animation of float type as a part of the [InfiniteTransition].
    val starOuterRoundingRadius by discoveryThinkingTransition.animateFloat(
        initialValue = 0.928F,
        targetValue = 0.756f,
        animationSpec = infiniteRepeatable(
            // Infinitely repeating a 1000ms tween animation using default easing curve.
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "PfpThinkingORR"
    )
    val starInnerRadius by discoveryThinkingTransition.animateFloat(
        initialValue = 0.928F,
        targetValue = 0.756f,
        animationSpec = infiniteRepeatable(
            // Infinitely repeating a 1000ms tween animation using default easing curve.
            animation = tween(500, 550),
            repeatMode = RepeatMode.Reverse
        ), label = "PfpThinkingIR"
    )

    val starThinking = RoundedPolygon.star(numVerticesPerRadius = 12, innerRadius = starInnerRadius, rounding = CornerRounding(
        starOuterRoundingRadius, 0.0F
    ), innerRounding = CornerRounding(starInnerRoundRadius, 0.0F)
    ).normalized()

    val morph = Morph(starThinking, circlePolygon)
    val morphProgress: Float by animateFloatAsState(
        if (stabilize) 1f else 0f,
        label = "PfpMorphProgress"
    )

    if (imageRequest != null) {
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
fun GuestProfilePicAnimationPreview() {
    PlanbotTheme {
        Row {
            GuestProfilePicAnimation(null, false)
            Spacer(Modifier.width(8.dp))
            GuestProfilePicAnimation(null, true)
        }
    }
}