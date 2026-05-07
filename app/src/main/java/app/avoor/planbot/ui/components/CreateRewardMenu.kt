package app.avoor.planbot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.avoor.planbot.R
import app.avoor.planbot.api.models.PlancoinReward

/**
 * A menu for creating a [PlancoinReward].
 */
@Composable
fun CreateRewardMenu(
    name: String,
    onNameChange: (String) -> Unit,
    cost: Int,
    onCostChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = {onNameChange(it)},
            placeholder = {Text(stringResource(R.string.plancoin_reward_name_field))}
        )
        Text(
            stringResource(R.string.price),
            Modifier.padding(18.dp).fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        NumberPicker(
            cost,
            onValueChange = {onCostChange(it)}
        )
    }
}