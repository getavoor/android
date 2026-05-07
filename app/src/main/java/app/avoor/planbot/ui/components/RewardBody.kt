package app.avoor.planbot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.api.models.PlancoinReward
import app.avoor.planbot.ui.theme.PlanbotTheme
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Add
import app.avoor.symbols.icons.Plancoin

/**
 * Shows a [PlancoinReward] and allows the user to get it.
 *
 * @param reward the reward to show.
 * @param gettable can the user get it?
 * @param onClick what to do if the user presses "Get".
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RewardBody(
    reward: PlancoinReward,
    gettable: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = reward.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
            val plancoinCost = pluralStringResource(
                R.plurals.plancoin_cost,
                reward.cost
            )
            Row(
                modifier = Modifier.semantics(
                    mergeDescendants = true
                ) {
                    contentDescription = plancoinCost
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Plancoin,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    reward.cost.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Button(
            onClick = onClick,
            contentPadding =
                ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight, hasStartIcon = true),
            modifier = Modifier
                .heightIn(ButtonDefaults.ExtraSmallContainerHeight)
                .padding(end = 16.dp),
            enabled = gettable
        ) {
            Icon(
                Icons.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.ExtraSmallContainerHeight)),
            )
            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.ExtraSmallContainerHeight)))
            Text(stringResource(R.string.plancoin_reward_buy))
        }
    }
}

@Composable
@Preview
private fun RewardBodyPreview() {
    PlanbotTheme {
        Surface() {
            RewardBody(PlancoinReward(
                "test",
                "Test reward",
                1337,
                42
            )) {}
        }
    }
}