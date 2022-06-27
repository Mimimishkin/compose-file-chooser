package file.chooser

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Press
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Scroll
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun FilesSite(
    files: List<HierarchyFile>,
    modifier: Modifier = Modifier,
    asGrid: Boolean = true,
    allowSelect: (HierarchyFile) -> Boolean,
    onChosen: (Set<HierarchyFile>) -> Unit,
    onOpen: (dir: HierarchyFile) -> Unit,
    onSort: (FilesTableColumn, reverse: Boolean) -> Unit,
) {
    val normalCellSize = 80.dp
    var scale by remember { mutableStateOf(1f) }

    val onChooseOrOpen = { file: HierarchyFile ->
        if (file.isDirectory) {
            onOpen(if (file.isLink) file.linkLocation else file)
        } else {
            onChosen(setOf(file))
        }
    }

    var selected by remember { mutableStateOf(setOf<HierarchyFile>()) }
    var lastSelected by remember(files) { mutableStateOf<HierarchyFile?>(null) }
    var multiselectMode by remember { mutableStateOf(false) }
    var reverseMode by remember { mutableStateOf(false) }
    val onSelected = fun(multi: Boolean, reverse: Boolean, file: HierarchyFile) {
        if (lastSelected == null) {
            selected = setOf(file)
            lastSelected = file
        } else {
            when {
                !multi && !reverse -> {
                    selected = setOf(file)
                }
                !multi && reverse -> {
                    selected = if (file in selected) selected - file else selected + file
                }
                multi && !reverse -> {
                    val range = files.sublist(files.indexOf(lastSelected), files.indexOf(file))
                    selected = selected + range
                }
                else /*multi && reverse*/ -> {
                    val range = files.sublist(files.indexOf(lastSelected), files.indexOf(file))
                    val new = range - selected
                    val old = range - new
                    selected = selected - old + new
                }
            }
            lastSelected = file
        }
    }
    val selectionModifier = { file: HierarchyFile ->
        Modifier.composed {
            val isSelected = file in selected
            val isCurrent = file == lastSelected

            val color by animateColorAsState(
                when {
                    !isSelected -> Color.Transparent
                    !isCurrent -> MaterialTheme.colors.secondary.let {
                        if (asGrid) it else it.copy(0.4f)
                    }
                    else -> MaterialTheme.colors.secondaryVariant.let {
                        if (asGrid) it else it.copy(0.4f)
                    }
                }
            )
            val border by animateDpAsState(if (isSelected) 2.5.dp else 0.dp)

            val clickable = Modifier.combinedClickable(
                enabled = allowSelect(file),
                onDoubleClick = { onChooseOrOpen(file) },
                onClick = { onSelected(multiselectMode, reverseMode, file) }
            )
            val selection = if (asGrid) {
                Modifier.border(border, color, FileCellShape)
            } else {
                Modifier.background(color)
            }
            val hiddenMark = if (file.isHidden) Modifier.alpha(0.7f) else Modifier

            hiddenMark.then(selection).then(clickable)
        }
    }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier
            .focusRequester(focusRequester)
            .focusable()
            .onPointerEvent(Press) { focusRequester.requestFocus() }
            .keyboardNavigation(
                onCtrl = { reverseMode = it; true },
                onShift = { multiselectMode = it; true },
                onEnter = {
                    if (selected.isEmpty()) return@keyboardNavigation false

                    if (reverseMode /*ctrl*/ || selected.size != 1) {
                        onChosen(selected)
                    } else {
                        onChooseOrOpen(selected.single())
                    }

                    return@keyboardNavigation true
                },
                onDirection = {
                    if (lastSelected != null) {
                        it.action(files.indexOf(lastSelected)) { old, new ->
                            if (new in files.indices) {
                                onSelected(false, true, files[old])
                                onSelected(multiselectMode, reverseMode, files[new])
                                true
                            } else {
                                false
                            }
                        }
                    } else {
                        false
                    }
                },
            )
            .onPointerEvent(Scroll) {
                if (asGrid && reverseMode) {
                    scale = (scale - it.changes.first().scrollDelta.y / 10).coerceIn(0.5f, 1.5f)
                }
            }
    ) {
        if (asGrid) {
            FilesGrid(
                files = files,
                cellSize = normalCellSize * scale,
                elementModifier = selectionModifier
            )
        } else {
            FilesTable(
                files = files,
                onSort = onSort,
                elementModifier = selectionModifier
            )
        }
    }
}

private fun List<HierarchyFile>.sublist(
    first: Int,
    second: Int
) = subList(min(first, second), max(first, second) + 1)