package file.chooser

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane

class ListRepresentationState {
    var asGrid by mutableStateOf(true)

    var scale by mutableStateOf(1f)

    companion object {
        val normalCellSize = 80.dp

        val minScale = 0.5f

        val maxScale = 2f
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
internal fun Overview(
    explorerHierarchy: ExplorerHierarchy?,
    modifier: Modifier = Modifier,
    filesState: FilesState,
    representationState: ListRepresentationState,
    onVisitDir: (HierarchyFile) -> Unit = {},
    settings: ChooserSettings = defaultChooserSettings(),
    onChosen: (Set<HierarchyFile>) -> Unit = {},
) {
    if (explorerHierarchy != null) {
        Column(modifier) {
            HorizontalSplitPane(Modifier.weight(1f)) {
                first(90.dp) {
                    HierarchyPreview(
                        directories = explorerHierarchy.files,
                        isExpanded = { explorerHierarchy.hasChildren(it) },
                        levelOf = { explorerHierarchy.levelOf(it) },
                        onOpen = onVisitDir,
                        onExpand = { explorerHierarchy.expand(it) },
                        onShirk = { explorerHierarchy.shirk(it) },
                    )
                }
                second(140.dp) {
                    FilesPlace(
                        filesState = filesState,
                        representationState = representationState,
                        allowSelect = settings.fullFilter.asHierarchy,
                        onChosen = onChosen,
                        onOpen = onVisitDir,
                        onSort = { filesState.comparator = it }
                    )
                }
            }
        }
    } else {
        FilesPlace(
            filesState = filesState,
            representationState = representationState,
            allowSelect = { settings.fullFilter(it.asCommon) },
            onChosen = onChosen,
            onOpen = onVisitDir,
            onSort = { filesState.comparator = it },
            modifier = modifier
        )
    }
}
