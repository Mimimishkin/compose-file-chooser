package file.chooser.utils

import androidx.compose.runtime.toMutableStateList
import file.chooser.utils.HierarchyFile.Companion.FileComparator
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.associateWith
import kotlin.collections.filter
import kotlin.collections.isNotEmpty
import kotlin.collections.minusAssign
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.onEach
import kotlin.collections.plus
import kotlin.collections.plusAssign
import kotlin.collections.sortedWith

private class Hierarchy<T> {
    private val map = mutableMapOf<T, T>()

    fun children(parent: T) = map.keys.filter { map[it] == parent }

    fun expand(parent: T, children: List<T>) {
        map += children.associateWith { parent }
    }

    fun shirk(parent: T, list: MutableList<T> = mutableListOf()): List<T> {
        return list + children(parent).onEach {
            shirk(it, list)
            map -= it
        }
    }

    fun levelOf(parent: T): Int {
        var level = 0

        var parent: T? = parent
        while (parent != null) {
            parent = map[parent]
            level++
        }

        return level
    }
}

internal class ExplorerHierarchy(root: List<HierarchyFile>) {
    private val hierarchy = Hierarchy<HierarchyFile>()
    private val _files = root.toMutableStateList()
    val files: List<HierarchyFile> get() = _files

    fun expand(file: HierarchyFile) {
        val subDirs = file.subDirs.sortedWith(FileComparator())
        hierarchy.expand(file, subDirs)
        _files.addAll(_files.indexOf(file) + 1, subDirs)
    }

    fun shirk(file: HierarchyFile) {
        val allChildren = hierarchy.shirk(file)
        val fromIndex = _files.indexOf(file) + 1
        _files.removeRange(fromIndex, fromIndex + allChildren.size)
    }

    fun hasChildren(file: HierarchyFile) = hierarchy.children(file).isNotEmpty()

    fun levelOf(file: HierarchyFile) = hierarchy.levelOf(file)
}