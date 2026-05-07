package app.avoor.planbot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.api.models.UserWithoutTokens
import app.avoor.planbot.ui.theme.PlanbotTheme
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun UserHeading(user: UserWithoutTokens?, modifier: Modifier = Modifier) {
    val imageModifier = Modifier.size(128.dp).clip(RoundedCornerShape(64.dp))
    Column(
        modifier = Modifier.fillMaxWidth().then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (user != null) {
            // Load the user's profile picture, showing a placeholder if one wasn't uploaded
            val context = LocalContext.current
            val imageRequest = ImageRequest.Builder(context)
                .data(user.photoUrl)
                .memoryCacheKey(user.photoUrl)
                .diskCacheKey(user.photoUrl)
                .placeholder(R.drawable.default_pfp)
                .error(R.drawable.default_pfp)
                .fallback(R.drawable.default_pfp)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                modifier = imageModifier
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.default_pfp),
                contentDescription = null,
                modifier = imageModifier
            )
        }

        Text(
            text = user?.name ?: "Please sign in",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = user?.email ?: "Tap on the picture to sign in"
        )
    }
}

@Composable
@Preview
fun PreviewUserHeading() {
    PlanbotTheme {
        Column {
            Text("Existing user (note that Coil doesn't load in previews):")
            UserHeading(
                UserWithoutTokens(
                    id = 0,
                    name = "Ari",
                    email = "meow@avoor.app",
                    photoUrl = "https://i.natgeofe.com/n/548467d8-c5f1-4551-9f58-6817a8d2c45e/NationalGeographic_2572187_square.jpg",
                    confirmed = true,
                    plancoins = 50,
                    currentStreak = 100,
                    longestStreak = 420
                )
            )
            Text("Null user:")
            UserHeading(null)
        }
    }
}