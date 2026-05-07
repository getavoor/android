package app.avoor.planbot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import tech.cataspect.m3x.MaterialListStyling

fun <T> LazyListScope.animatedMaterialListItems(
    items: List<T>,
    styling: MaterialListStyling = MaterialListStyling.Default,
    onItemClick: ((item: T) -> Unit)? = null,
    content: @Composable ((item: T) -> Unit)
) {
    itemsIndexed(items) {index, item ->
        val shape =
            // If there is only 1 item, use larger corner radius for all 4 corners
            if (items.size == 1) RoundedCornerShape(styling.largeCornerRadius)
            // Otherwise:
            else {
                when (index) {
                    // If this is the first item, use large radius for the top corners
                    // and small for the bottom ones
                    0 -> RoundedCornerShape(
                        styling.largeCornerRadius,
                        styling.largeCornerRadius,
                        styling.cornerRadius,
                        styling.cornerRadius
                    )
                    // If this is the last item, use small radius for the top corners
                    // and large for the bottom ones
                    items.size - 1 -> RoundedCornerShape(
                        styling.cornerRadius,
                        styling.cornerRadius,
                        styling.largeCornerRadius,
                        styling.largeCornerRadius
                    )
                    // Otherwise, use regular corner radius for all 4 sides
                    else -> RoundedCornerShape(styling.cornerRadius)
                }
            }

        // Create a surface to house this item
        if (onItemClick != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .clip(shape)
                    .clickable { onItemClick(item) },
                shape = shape,
                tonalElevation = 2.dp,
            ) {content(item)}
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth().animateItem(),
                shape = shape,
                tonalElevation = 2.dp
            ) {content(item)}
        }
    }
}