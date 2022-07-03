package file.chooser

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ChooserActionsController(
    private val state: FilesState,
    private val operations: StandardFileOperations
) {
    private val someSelected by derivedStateOf { state.selected.firstOrNull()?.parent == state.dir }
    private val mayChange by derivedStateOf { !state.dir.isSystem }

    val mayNewFolder by derivedStateOf { mayChange }
    val mayCopy by derivedStateOf { someSelected }
    val mayPaste by derivedStateOf { mayChange && toPaste.isNotEmpty() }
    val mayCut by derivedStateOf { someSelected }
    val mayDelete by derivedStateOf { someSelected }
    val mayRename by derivedStateOf { someSelected }

    private var toPaste by mutableStateOf(setOf<HierarchyFile>())
    private var wasCut by mutableStateOf(false)

    fun newFolder() {
        operations.onNewFolder(state.dir)
        state.refresh()
    }

    fun copy() {
        toPaste = state.selected
        wasCut = false
    }

    fun cut() {
        toPaste = state.selected
        wasCut = true
    }

    fun paste() {
        operations.onPaste(toPaste, wasCut)
        toPaste = setOf()
        state.refresh()
    }

    fun delete() {
        operations.onDelete(state.selected)
        state.unselect()
        state.refresh()
    }

    fun rename() {
        operations.onRename(state.selected)
        state.refresh()
    }
}