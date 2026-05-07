package app.avoor.planbot.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.avoor.symbols.Icons
import app.avoor.planbot.R
import app.avoor.symbols.icons.Info

@Composable
fun InvalidLinkDialog(
    mainAction: () -> Unit,
    onDismiss: (() -> Unit)?,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Info, null /*this icon isn't necessary to understand the dialog*/)
        },
        title = {
            Text(text = stringResource(R.string.dlg_link_title))
        },
        text = {
            Text(text = stringResource(R.string.dlg_link_body))
        },
        onDismissRequest = {
            if (onDismiss != null) onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = mainAction
            ) {
                Text(
                    stringResource(R.string.ok)
                )
            }
        }
    )
}