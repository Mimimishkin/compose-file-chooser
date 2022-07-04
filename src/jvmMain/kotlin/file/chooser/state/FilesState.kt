package file.chooser.state

import androidx.compose.runtime.*
import file.chooser.utils.HierarchyFile
import file.chooser.utils.HierarchyFile.Companion.asHierarchy
import file.chooser.asHierarchy
import file.chooser.extensionFilter
import java.io.File
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun rememberFilesState(dir: HierarchyFile, onlyDirs: Boolean, firstExtension: String?) = remember {
    FilesState(dir, if (onlyDirs) { _ -> false } else extensionFilter(setOfNotNull(firstExtension)).asHierarchy)
}

internal class FilesState(initialDir: HierarchyFile, initialFilter: (HierarchyFile) -> Boolean) {
    var dir by mutableStateOf(initialDir)

    var comparator by mutableStateOf<Comparator<HierarchyFile>?>(null)

    var filter by mutableStateOf(initialFilter)

    val files by derivedStateOf {
        var children = dir.children.filter { it.isDirectory || filter(it) }
        if (comparator != null)
            children = children.sortedWith(comparator!!)
        children
    }

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

    private fun <E> List<E>.range(first: Int, second: Int): List<E> =
        slice(min(first, second)..max(first, second))

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