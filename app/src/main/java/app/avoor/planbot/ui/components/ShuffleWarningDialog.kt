package app.avoor.planbot.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.avoor.planbot.R
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Info


/**
 * Warning about shuffle mode editing the calendar.
 */
@Composable
fun ShuffleWarningDialog(
    onConfirmation: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Info, null /*this icon isn't necessary to understand the dialog*/)
        },
        title = {
            Text(text = stringResource(R.string.dlg_shuffle_enable_title))
        },
        text = {
            Text(text = stringResource(R.string.dlg_shuffle_enable_desc))
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.dlg_shuffle_enable_cancel))
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.dlg_shuffle_enable_ok))
            }
        }
    )
}