package app.avoor.planbot.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import app.avoor.planbot.R
import app.avoor.planbot.ui.theme.PlanbotTheme
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Add
import app.avoor.symbols.icons.Remove

/**
 * A counter loosely based on the Material 3 time picker.
 */
@Composable
fun NumberPicker(
    value: Int,
    acceptNegatives: Boolean = false,
    onValueChange: (Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val backgroundColor by animateColorAsState(
        if (isFocused) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "numberPickerBackColor"
    )

    val borderWeight by animateDpAsState(
        if (isFocused) {
            1.dp
        } else {
            0.dp
        },
        label = "numberPickerBorderWeight"
    )

    val textColor by animateColorAsState(
        if (isFocused) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "numberPickerTextColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BasicTextField(
            value = value.toString(),

            interactionSource = interactionSource,

            // accept only numbers
            onValueChange = {
                // explicitly treat the value "" because it causes a NumberFormatException
                // when parsed using toInt()
                if (it == "") {
                    onValueChange(0)
                } else if (it.isDigitsOnly()) {
                    val ni = it.toInt()
                    // only allow positive numbers unless negatives are explicitly allowed
                    if (acceptNegatives || ni in 0..<100)
                        onValueChange(it.toInt())
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            // one line
            singleLine = true,
            // larger font
            textStyle = MaterialTheme.typography.displayMedium.copy(
                // text color
                color = textColor,
                // center
                textAlign = TextAlign.Center
            ),

            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),

            modifier = Modifier
                // size to the text
                //.width(IntrinsicSize.Min)
                // size per m3 time picker guidelines
                .width(96.dp)
                .height(80.dp)
                // border
                .border(
                    width = borderWeight,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    backgroundColor,
                    shape = RoundedCornerShape(8.dp)
                )
                // 12dp padding
                .padding(12.dp)
        )
        // buttons
        Column(
            Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp))
        ) {
            // +
            IconButton(
                onClick = {
                    onValueChange(value + 1)
                },
                modifier = Modifier
                    .size(36.dp)
                    .padding(0.dp)
            ) {
                Icon(
                    Icons.Add,
                    stringResource(R.string.number_picker_increase),
                    Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            // line
            Spacer(
                Modifier
                    .height(1.dp)
                    .width(36.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
            // -
            IconButton(
                onClick = {
                    if (value > 0)
                    onValueChange(value - 1)
                },
                modifier = Modifier
                    .size(36.dp)
                    .padding(0.dp),
                enabled = (value > 0)
            ) {
                Icon(
                    Icons.Remove,
                    stringResource(R.string.number_picker_decrease),
                    Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
fun NumberPickerPreview() {
    PlanbotTheme {
        NumberPicker(
            4,
        ) {}
    }
}