package app.avoor.planbot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.api.models.User
import app.avoor.planbot.api.models.UserWithoutTokens
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

/**
 * Static profile picture.
 *
 * For an animated version that supports guest profiles use [GuestProfilePicAnimation].
 *
 * @param user the account to show the profile picture of.
 * @param contentDescription an optional description for accessibility services (this should always
 * be provided unless an image is used for decorative purposes and does not represent an action
 * the user can take)
 * @param modifier a modifier for the inner Image.
 * @param size the size of the profile picture (default: 16dp).
 */
@Composable
fun ProfilePicture(
    user: User?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    ProfilePicture(
        user?.photoUrl,
        contentDescription,
        modifier,
        size
    )
}

/**
 * Static profile picture.
 *
 * For an animated version that supports guest profiles use [GuestProfilePicAnimation].
 *
 * @param user the account to show the profile picture of.
 * @param contentDescription an optional description for accessibility services (this should always
 * be provided unless an image is used for decorative purposes and does not represent an action
 * the user can take)
 * @param modifier a modifier for the inner Image.
 * @param size the size of the profile picture (default: 16dp).
 */
@Composable
fun ProfilePicture(
    user: UserWithoutTokens?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    ProfilePicture(
        user?.photoUrl,
        contentDescription,
        modifier,
        size
    )
}


/**
 * Static profile picture.
 *
 * For an animated version that supports guest profiles use [GuestProfilePicAnimation].
 *
 * @param photoUrl the URL of the profile picture.
 * @param contentDescription an optional description for accessibility services (this should always
 * be provided unless an image is used for decorative purposes and does not represent an action
 * the user can take)
 * @param modifier a modifier for the inner Image.
 * @param size the size of the profile picture (default: 16dp).
 */
@Composable
fun ProfilePicture(
    photoUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    if (photoUrl != null) {
        // Load the user's profile picture, showing a placeholder if one wasn't uploaded
        val context = LocalContext.current
        val imageRequest = ImageRequest.Builder(context)
            .data(photoUrl)
            .memoryCacheKey(photoUrl)
            .diskCacheKey(photoUrl)
            .placeholder(R.drawable.default_pfp)
            .error(R.drawable.default_pfp)
            .fallback(R.drawable.default_pfp)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            modifier = Modifier.size(size).clip(RoundedCornerShape(50)).then(modifier)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.default_pfp),
            contentDescription = null,
            modifier = Modifier.size(size).clip(RoundedCornerShape(50)).then(modifier)
        )
    }
}