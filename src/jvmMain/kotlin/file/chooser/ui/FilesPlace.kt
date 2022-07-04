package file.chooser.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
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
import file.chooser.utils.HierarchyFile.Companion.FileComparator
import file.chooser.state.FilesState
import file.chooser.state.RepresentationState
import file.chooser.state.RepresentationState.Companion.maxScale
import file.chooser.state.RepresentationState.Companion.minScale
import file.chooser.state.RepresentationState.Companion.normalCellSize
import file.chooser.utils.HierarchyFile

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun FilesPlace(
    filesState: FilesState,
    modifier: Modifier = Modifier,
    representationState: RepresentationState,
    allowSelect: (HierarchyFile) -> Boolean,
    onChosen: (Set<HierarchyFile>) -> Unit,
    onOpen: (dir: HierarchyFile) -> Unit,
    onSort: (Comparator<HierarchyFile>) -> Unit,
) = with(filesState) { with(representationState) {
    val onChooseOrOpen = { file: HierarchyFile ->
        if (file.isDirectory) {
            onOpen(if (file.isLink) file.linkLocation else file)
        } else {
            onChosen(setOf(file))
        }
    }

    var multiselectMode by remember { mutableStateOf(false) }
    var reverseMode by remember { mutableStateOf(false) }
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
                enabled = file.isDirectory || allowSelect(file),
                onDoubleClick = { onChooseOrOpen(file) },
                onClick = { select(multiselectMode, reverseMode, file) }
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
                    if (selected.isEmpty() || !selected.all(allowSelect)) return@keyboardNavigation false

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
                                select(multiselectMode, reverseMode, files[new])
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
                    scale = (scale - it.changes.first().scrollDelta.y / 10).coerceIn(minScale, maxScale)
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
                onSort = { column, reverse -> onSort(FileComparator(column, reverse)) },
                elementModifier = selectionModifier
            )
        }
    }
} }