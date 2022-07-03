package file.chooser

import androidx.compose.runtime.*
import file.chooser.HierarchyFile.Companion.asHierarchy
import java.io.File
import java.security.cert.Extension
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun rememberFilesState(dir: HierarchyFile, firstExtension: String?) = remember {
    FilesState(dir, extensionFilter(setOfNotNull(firstExtension)).asHierarchy)
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