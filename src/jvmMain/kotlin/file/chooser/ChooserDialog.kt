package file.chooser

import Vocabulary
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.singleWindowApplication
import file.chooser.HierarchyFile.Companion.asHierarchy
import java.io.File

val LocalChooserDialogLastDirectory = compositionLocalOf { ChooserDialogState() }

class ChooserDialogState {
    companion object {
        val default = FileUtils.defaultDirectory
    }

    var dir by mutableStateOf(default)
}

@Composable
fun ChooserDialogContainer(content: @Composable () -> Unit) {
    val state = remember { ChooserDialogState() }

    CompositionLocalProvider(
        LocalChooserDialogLastDirectory provides state
    ) {
        content()
    }
}

@Composable
fun ChooserDialog(
    initialDirectory: File = LocalChooserDialogLastDirectory.current.dir,
    settings: ChooserSettings = defaultChooserSettings(),
    onChosen: (Set<File>) -> Unit = {}
) {
    val state = LocalChooserDialogLastDirectory.current

    Dialog(onCloseRequest = { onChosen(setOf()) }) {
        ChooserDialogContent(
            initialDirectory = initialDirectory.asHierarchy,
            settings = settings,
            onChosen = { onChosen(it.mapTo(mutableSetOf()) { file -> file.asCommon }) },
            onCurrentDirChanged = { state.dir = it.asCommon }
        )
    }
}

@Composable
internal fun ChooserDialogContent(
    initialDirectory: HierarchyFile,
    modifier: Modifier = Modifier,
    settings: ChooserSettings = defaultChooserSettings(),
    onChosen: (Set<HierarchyFile>) -> Unit = {},
    onCurrentDirChanged: (HierarchyFile) -> Unit = {}
) {
    val hierarchy = remember { ExplorerHierarchy(HierarchyFile.explorerShortcuts) }
    val filesState = remember { FilesState(initialDirectory) }
    val history = rememberHistory(initialDirectory) {
        if (it.isDirectory) {
            filesState.dir = it
            onCurrentDirChanged(it)
        } else {
            onChosen(setOf(it))
        }
    }
    val representationState = remember { ListRepresentationState() }
    var isHierarchyVisible by remember { mutableStateOf(true) }

    with(filesState) {
        Column(modifier) {
            val background = MaterialTheme.colors.primaryVariant.copy(0.1f)

            Column(
                verticalArrangement = spacedBy(4.dp),
                modifier = Modifier.background(background).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                ActionsBar(
                    isHierarchyVisible = isHierarchyVisible,
                    onSwitchHierarchyVisibility = { isHierarchyVisible = !isHierarchyVisible },
                    listRepresentationState = representationState,
                    filesState = filesState,
                    modifier = Modifier.height(40.dp)
                )

                FileTreeNavigationBar(history = history, Modifier.height(40.dp))
            }

            Overview(
                explorerHierarchy = if (isHierarchyVisible) hierarchy else null,
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                filesState = filesState,
                onVisitDir = { history.visit(it) },
                settings = settings,
                onChosen = onChosen,
                representationState = representationState
            )

            Column(
                verticalArrangement = spacedBy(4.dp),
                modifier = Modifier.background(background).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                FileNameBar(
                    selectedFile = lastSelected,
                    extensions = settings.allowedExtensions,
                    modifier = Modifier.height(40.dp),
                    onChosen = { name -> history.current.children.find { it.name == name }?.let { history.visit(it) } }
                )

                Row {
                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = { lastSelected!!.let { if (it.isDirectory) history.visit(it) else onChosen(selected) } },
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selected.isNotEmpty()
                    ) {
                        Text(if (lastSelected?.isDirectory == true) Vocabulary.open else Vocabulary.confirm)
                    }

                    Spacer(Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { onChosen(setOf()) },
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(Vocabulary.cancel)
                    }
                }
            }
        }
    }
}

private fun main() = singleWindowApplication {
    MaterialTheme {
            var isVisible by remember { mutableStateOf(true) }
            val chosen = remember { mutableListOf<File>().toMutableStateList() }

            if (isVisible) {
                ChooserDialog { chosen += it; isVisible = false }
            }

            Column {
                chosen.forEach {
                    Text(it.path)
                }
            }
    }
}