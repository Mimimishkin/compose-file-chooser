package file.chooser

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
import file.chooser.HierarchyFile.Companion.FileComparator
import file.chooser.HierarchyFile.Companion.asHierarchy
import file.chooser.ListRepresentationState.Companion.maxScale
import file.chooser.ListRepresentationState.Companion.minScale
import file.chooser.ListRepresentationState.Companion.normalCellSize
import java.io.File
import kotlin.math.max
import kotlin.math.min

internal class FilesState(initialDir: HierarchyFile) {
    var dir by mutableStateOf(initialDir)

    var comparator by mutableStateOf<Comparator<HierarchyFile>?>(null)

    val files by derivedStateOf { if (comparator == null) dir.children else dir.children.sortedWith(comparator!!) }

    var selected by mutableStateOf(setOf<HierarchyFile>())
        private set

    var lastSelected by mutableStateOf<HierarchyFile?>(null)
        private set

    fun select(multi: Boolean, reverse: Boolean, file: HierarchyFile) {
        if (lastSelected == null || lastSelected!!.parent != dir) {
            selected = setOf(file)
        } else {
            when {
                !multi && !reverse -> {
                    selected = setOf(file)
                }
                !multi && reverse -> {
                    selected = if (file in selected) selected - file else selected + file
                }
                multi && !reverse -> {
                    val range = with(files) { range(indexOf(lastSelected), indexOf(file)) }
                    selected = selected + range
                }
                else /*multi && reverse*/ -> {
                    val range = with(files) { range(indexOf(lastSelected), indexOf(file)) }
                    val (old, new) = range.partition { it in selected }
                    selected = selected - old + new
                }
            }
        }
        lastSelected = file
    }

    fun unselect() {
        selected = setOf()
        lastSelected = null
    }

    fun selectAll() {
        selected = dir.children.toSet()
        lastSelected = selected.lastOrNull()
    }

    fun invertSelection() {
        selected = (dir.children - selected).toSet()
        lastSelected = selected.lastOrNull()
    }

    fun refresh() {
        val oldDir = dir
        dir = File.listRoots()[0].asHierarchy
        dir = oldDir
    }
}

private fun <E> List<E>.range(first: Int, second: Int): List<E> =
    slice(min(first, second)..max(first, second))

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun FilesSite(
    filesState: FilesState,
    modifier: Modifier = Modifier,
    representationState: ListRepresentationState,
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
                enabled = allowSelect(file),
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