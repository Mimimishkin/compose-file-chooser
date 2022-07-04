package file.chooser.utils

import androidx.compose.runtime.*
import kotlin.math.max
import kotlin.math.min

internal class FileTreeTravelHistory(
    initialDirectory: HierarchyFile,
    private val mayVisit: (HierarchyFile) -> Boolean = { true },
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
        if (mayVisit(directory)) {
            if (list[currentIndex] == directory)
                return
            _list.dropLast(list.lastIndex - currentIndex)
            _list.add(directory)
            currentIndex++
            onVisit(directory)
        }
    }

    fun undo() {
        currentIndex = max(0, currentIndex - 1)
        onVisit(list[currentIndex])
    }

    fun redo() {
        currentIndex = min(currentIndex + 1, list.lastIndex)
        onVisit(list[currentIndex])
    }

    fun moveTo(index: Int) {
        currentIndex = index
        onVisit(list[currentIndex])
    }
}

@Composable
internal fun rememberHistory(
    initial: HierarchyFile,
    mayVisit: (HierarchyFile) -> Boolean,
    onVisit: (HierarchyFile) -> Unit
) = remember {
    FileTreeTravelHistory(
        initialDirectory = initial,
        mayVisit = mayVisit,
        onVisit = onVisit
    )
}