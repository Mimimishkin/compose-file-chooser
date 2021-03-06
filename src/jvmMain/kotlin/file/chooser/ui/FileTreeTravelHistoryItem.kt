package file.chooser.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import file.chooser.utils.FileTreeTravelHistory
import file.chooser.icons.ArrowUpward
import file.chooser.icons.Circle

@Composable
internal fun FileTreeTravelHistoryItem(
    history: FileTreeTravelHistory,
    modifier: Modifier = Modifier
) = with(history) {
    @Composable
    fun SimpleButton(
        icon: ImageVector,
        enabled: Boolean = true,
        onClick: () -> Unit
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(1f / 1f)
                .fillMaxHeight()
                .clickable(onClick = onClick, enabled = enabled)
                .alpha(if (enabled) 1f else 0.3f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    Row(modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        SimpleButton(
            icon = Icons.Default.ArrowBack,
            enabled = currentIndex > 0,
            onClick = { undo() }
        )

        SimpleButton(
            icon = Icons.Default.ArrowForward,
            enabled = currentIndex < list.lastIndex,
            onClick = { redo() }
        )

        Box {
            var expanded by remember { mutableStateOf(false) }

            SimpleButton(
                icon = Icons.Default.KeyboardArrowDown,
                enabled = list.size > 1,
                onClick = { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                list.reversed().forEachIndexed { index, file ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .widthIn(max = 370.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { expanded = false; moveTo(list.lastIndex - index) }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                index < list.lastIndex - currentIndex -> Icons.Default.KeyboardArrowUp
                                index > list.lastIndex - currentIndex -> Icons.Default.KeyboardArrowDown
                                else -> Icons.Default.Circle
                            },
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = with(file) { if (!isSystem) path else name },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        SimpleButton(
            icon = Icons.Default.ArrowUpward,
            enabled = list[currentIndex].hasParent,
            onClick = { visit(list[currentIndex].parent) }
        )
    }
}