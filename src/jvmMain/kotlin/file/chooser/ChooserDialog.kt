package file.chooser

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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import file.chooser.ChooserMode.*
import file.chooser.state.ListRepresentationState
import file.chooser.utils.HierarchyFile.Companion.asHierarchy
import file.chooser.state.LocalChooserDialogContainerState
import file.chooser.state.rememberFilesState
import file.chooser.ui.*
import file.chooser.utils.ExplorerHierarchy
import file.chooser.utils.HierarchyFile
import file.chooser.utils.Vocabulary
import file.chooser.utils.rememberHistory
import java.io.File

@Composable
fun ChooserDialog(
    initialDirectory: File,
    settings: ChooserSettings,
    title: String,
    icon: Painter?,
    onChosen: (Set<File>) -> Unit
) {
    val state = LocalChooserDialogContainerState.current

    Dialog(title = title, icon = icon, onCloseRequest = { onChosen(setOf()) }) {
        ChooserDialogContent(
            initialDirectory = initialDirectory.asHierarchy,
            settings = settings,
            onChosen = { onChosen(it.mapTo(mutableSetOf()) { file -> file.asCommon }) },
            onCurrentDirChanged = { state.lastDir = it.asCommon }
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
    val mayChoose: (Set<HierarchyFile>) -> Boolean = { it.all(predicate = settings.fullFilter.asHierarchy) }
    val hierarchy = remember { ExplorerHierarchy(HierarchyFile.explorerShortcuts) }
    val filesState = rememberFilesState(initialDirectory, settings.mode == OnlyDirs, settings.firstExtension)
    val history = rememberHistory(
        initial = initialDirectory,
        mayVisit = { it.isDirectory || (settings.mode != OnlyDirs && mayChoose(setOf(it))) },
        onVisit = {
            if (it.isDirectory) {
                filesState.dir = it
                onCurrentDirChanged(it)
            } else {
                onChosen(setOf(it))
            }
        }
    )
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
                    filesState = filesState,
                    extensions = settings.allowedExtensions,
                    modifier = Modifier.height(40.dp),
                    onChosen = { name -> history.current.children.find { it.name == name }?.let { history.visit(it) } }
                )

                Row {
                    Spacer(Modifier.weight(1f))

                    val actionOpen = lastSelected?.isDirectory == true
                    Button(
                        onClick = { lastSelected!!.let { if (it.isDirectory) history.visit(it) else onChosen(selected) } },
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selected.isNotEmpty() && (actionOpen || mayChoose(selected))
                    ) {
                        Text(if (actionOpen) Vocabulary.open else Vocabulary.confirm)
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