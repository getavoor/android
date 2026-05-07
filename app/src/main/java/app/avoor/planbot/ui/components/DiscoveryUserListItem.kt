package app.avoor.planbot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import app.avoor.symbols.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import app.avoor.planbot.R
import app.avoor.planbot.data.discovery.DiscoveryUser
import app.avoor.planbot.ui.theme.PlanbotTheme
import app.avoor.symbols.icons.Check

/**
 * Displays a [DiscoveryUser] as a list item.
 *
 * @param user the user to display.
 * @param approved is the user approved?
 * @param modifier an optional extra modifier.
 * @param onAccept what to do when the accept button is pressed. Optional, but required if
 * the user isn't approved.
 */
@Composable
fun DiscoveryUserListItem(
    user: DiscoveryUser,
    approved: Boolean,
    modifier: Modifier = Modifier,
    onAccept: (() -> Unit)?
) {
    val imageModifier = Modifier
        .size(48.dp)
        .clip(RoundedCornerShape(32.dp))
    Row(
        modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (user.photoUrl != null) {
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
            text = user.name,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.weight(1f))
        if (!approved) {
            IconButton(onClick = {
                if (onAccept != null) onAccept()
            }) {
                Icon(
                    Icons.Check,
                    stringResource(R.string.btn_approve)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDiscoveryUserListItem() {
    PlanbotTheme {
        Column {
            Card(Modifier.padding(8.dp)) {
                DiscoveryUserListItem(
                    user = DiscoveryUser(300, "Ari", null),
                    approved = false,
                    onAccept = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Card(Modifier.padding(8.dp)) {
                DiscoveryUserListItem(
                    user = DiscoveryUser(302, "Ari meow amogus long long", null),
                    approved = true,
                    onAccept = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}