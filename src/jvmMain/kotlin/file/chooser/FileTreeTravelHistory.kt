package file.chooser

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

internal class FileTreeTravelHistory(
    initialDirectory: HierarchyFile,
    private val onVisit: (HierarchyFile) -> Unit = {}
) {
    private val _list = mutableListOf(initialDirectory)
    val list: List<HierarchyFile> = _list

    var currentIndex by mutableStateOf(0)
    val current by derivedStateOf { list[currentIndex] }

    init {
        onVisit(initialDirectory)
    }

    fun visit(directory: HierarchyFile) {
        if (list[currentIndex] == directory)
            return
        _list.dropLast(list.lastIndex - currentIndex)
        _list.add(directory)
        currentIndex++
        onVisit(directory)
    }

    fun undo() {
        currentIndex = max(0, currentIndex - 1)
        onVisit(list[currentIndex])
    }

    fun redo() {
        currentIndex = min(currentIndex + 1, list.lastIndex)
        onVisit(list[currentIndex])
    }

    fun moveTo(directory: HierarchyFile) {
        val index = list.indexOf(directory)
        if (index != -1) {
            currentIndex = index
            onVisit(list[currentIndex])
        }
    }
}

@Composable
internal fun rememberHistory(
    initial: HierarchyFile,
    onVisit: (HierarchyFile) -> Unit
) = remember {
    FileTreeTravelHistory(
        initialDirectory = initial,
        onVisit = onVisit
    )
}

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
                contentDescription = null
            )
        }
    }

    Row(modifier, horizontalArrangement = spacedBy(2.dp)) {
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
        SimpleButton(
            icon = Icons.Default.ArrowUpward,
            enabled = list[currentIndex].hasParent,
            onClick = { visit(list[currentIndex].parent) }
        )

        Box {
            var expanded by remember { mutableStateOf(false) }

            SimpleButton(
                icon = Icons.Default.ArrowCircleDown,
                enabled = list.size > 1,
                onClick = { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                list.reversed().forEachIndexed { index, file ->
                    DropdownMenuItem(
                        onClick = { expanded = false; moveTo(file) }
                    ) {
                        Icon(
                            imageVector = when {
                                index < list.lastIndex - currentIndex -> Icons.Default.KeyboardArrowUp
                                index > list.lastIndex - currentIndex -> Icons.Default.KeyboardArrowDown
                                else -> Icons.Default.Circle
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = file.path,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}