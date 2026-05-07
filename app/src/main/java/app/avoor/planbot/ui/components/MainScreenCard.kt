package app.avoor.planbot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.avoor.planbot.ui.theme.PlanbotTheme


/**
 * A card for the main screen.
 */
@Composable
fun MainScreenCard(
    text: String,
    title: String? = null,
    primaryButton: @Composable (()->Unit)? = null,
    secondaryButton: @Composable (()->Unit)? = null
) {
    OutlinedCard() {
        Column(
            Modifier.padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (primaryButton != null) {
                    primaryButton()
                }
                if (secondaryButton != null) {
                    secondaryButton()
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainCard() {
    PlanbotTheme {
        MainScreenCard(
            text = "I'm a cute cat. You can play with me using this card... or face the consequences.",
            title = "Meow!",
            primaryButton = { Button(onClick={}){ Text("Pet") } },
            secondaryButton = { OutlinedButton(onClick={}){ Text("Boop") } }
        )
    }
}