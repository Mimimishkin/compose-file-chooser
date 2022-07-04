package file.chooser.utils

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import file.chooser.state.FilesState
import file.chooser.ui.FilesGrid
import file.chooser.ui.RenameFilesDialog

internal data class StandardFileOperations(
    val onPaste: (Set<HierarchyFile>, wasCut: Boolean) -> Unit,
    val onDelete: (Set<HierarchyFile>) -> Unit,
    val onNewFolder: (containingDir: HierarchyFile) -> Unit,
    val onRename: (Set<HierarchyFile>) -> Unit,
)

private val freeName = { containingDir: HierarchyFile, name: String ->
    containingDir.child(name).let {
        if (!it.exists) {
            it
        } else {
            var index = 1
            val child = { containingDir.child("$name ($index)") }

            while (child().exists) {
                index++
            }

            child()
        }
    }
}

internal val defaultNewFolder = { containingDir: HierarchyFile, name: String ->
    freeName(containingDir, name).createDir()
}

internal val defaultDelete = { files: Set<HierarchyFile>, onFail: (List<HierarchyFile>) -> Unit ->
    val failed = files.filter { !it.delete() }
    if (failed.isNotEmpty()) onFail(failed)
}

internal val defaultPaste = { files: Set<HierarchyFile>, wasCut: Boolean, dir: HierarchyFile ->
    files.forEach {
        val old = it
        val new = freeName(dir, old.name)
        if (wasCut) old.moveTo(new) else old.copyTo(new)
    }
}

internal val defaultRename = { files: Set<HierarchyFile>, newName: String ->
    files.forEach {
        val freeName = freeName(it.parent, newName).name
        it.rename(freeName)
    }
}

@Composable
internal fun rememberStandardFileOperations(state: FilesState): StandardFileOperations {
    var showRenameDialog by remember { mutableStateOf(false) }
    var toRename by remember { mutableStateOf(setOf<HierarchyFile>()) }

    if (showRenameDialog) {
        RenameFilesDialog(toRename, onCancel = { showRenameDialog = false }) {
            defaultRename(toRename, it)
            showRenameDialog = false
        }
    }

    var showFailedDialog by remember { mutableStateOf(false) }
    var failedFiles by remember { mutableStateOf(listOf<HierarchyFile>()) }

    if (showFailedDialog) {
        Dialog(onCloseRequest = { showFailedDialog = false; failedFiles = listOf() }) {
            Column(horizontalAlignment = CenterHorizontally, verticalArrangement = spacedBy(8.dp)) {
                Icon(Icons.Rounded.Warning, null, Modifier.size(100.dp))

                Text(Vocabulary.deleting_failed, textAlign = TextAlign.Center)

                FilesGrid(
                    files = failedFiles,
                    cellSize = 40.dp
                )
            }
        }
    }

    val newFolder = Vocabulary.new_folder
    return remember {
        StandardFileOperations(
            onPaste = { files, wasCut ->
                defaultPaste(files, wasCut, state.dir)
            },
            onDelete = { files ->
                defaultDelete(files) { failed ->
                    failedFiles = failed
                    showFailedDialog = true
                }
            },
            onNewFolder = { containingDir ->
                defaultNewFolder(containingDir, newFolder)
            },
            onRename = { files ->
                toRename = files
                showRenameDialog = true
            }
        )
    }
}