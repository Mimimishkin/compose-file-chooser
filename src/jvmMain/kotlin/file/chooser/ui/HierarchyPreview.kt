package file.chooser.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import file.chooser.utils.HierarchyFile

@Composable
internal fun HierarchyPreview(
    directories: List<HierarchyFile>,
    levelOf: (HierarchyFile) -> Int,
    isExpanded: (HierarchyFile) -> Boolean,
    onOpen: (HierarchyFile) -> Unit = {},
    onExpand: (HierarchyFile) -> Unit = {},
    onShirk: (HierarchyFile) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val verticalState = rememberLazyListState()

    Row {
        LazyColumn(modifier = modifier.weight(1f), state = verticalState) {
            items(directories) { dir ->
                HierarchyPreviewItem(
                    directory = dir,
                    onOpen = { onOpen(dir) },
                    level = levelOf(dir),
                    expanded = isExpanded(dir),
                    onExpand = { onExpand(dir) },
                    onShirk = { onShirk(dir) },
                )
            }
        }

        Spacer(Modifier.width(2.dp))

        VerticalScrollbar(rememberScrollbarAdapter(verticalState))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HierarchyPreviewItem(
    directory: HierarchyFile,
    modifier: Modifier = Modifier,
    onOpen: () -> Unit = {},
    level: Int = 0,
    expanded: Boolean = false,
    onExpand: () -> Unit = {},
    onShirk: () -> Unit = {},
) {
    var isSelected by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(if (isSelected) Color(0f, 0f, 0f, 0.1f) else Color.Transparent)
            .onFocusEvent { isSelected = it.hasFocus }
            .combinedClickable(
                onClick = onOpen,
                onDoubleClick = { onExpand(); onOpen() }
            )
            .padding(4.dp)
            .padding(start = 16.dp * level),
        contentAlignment = Alignment.Center
    ) {
        Row(Modifier.alpha(if (directory.isHidden) 0.7f else 1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (directory.subDirs.isNotEmpty()) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { if (expanded) onShirk() else onExpand() }
                        .size(20.dp)
                        .padding(2.dp)
                )
            } else {
                Box(Modifier.size(20.dp))
            }

            FileIcon(
                file = directory,
                modifier = Modifier.size(20.dp).padding(2.dp)
            )

            Text(
                text = directory.name,
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f).padding(end = 4.dp),
                maxLines = 1,
                fontSize = 12.sp,
            )
        }
    }
}