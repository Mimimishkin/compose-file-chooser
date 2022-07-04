package file.chooser.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import file.chooser.*
import file.chooser.state.FilesState
import file.chooser.state.ListRepresentationState
import file.chooser.utils.ExplorerHierarchy
import file.chooser.utils.HierarchyFile
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi

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
//    if (explorerHierarchy != null) {
//        Column(modifier) {
//            HorizontalSplitPane(Modifier.weight(1f)) {
//                first(90.dp) {
//                    HierarchyPreview(
//                        directories = explorerHierarchy.files,
//                        isExpanded = { explorerHierarchy.hasChildren(it) },
//                        levelOf = { explorerHierarchy.levelOf(it) },
//                        onOpen = onVisitDir,
//                        onExpand = { explorerHierarchy.expand(it) },
//                        onShirk = { explorerHierarchy.shirk(it) },
//                    )
//                }
//                second(140.dp) {
//                    FilesPlace(
//                        filesState = filesState,
//                        representationState = representationState,
//                        allowSelect = settings.fullFilter.asHierarchy,
//                        onChosen = onChosen,
//                        onOpen = onVisitDir,
//                        onSort = { filesState.comparator = it },
//                        modifier = Modifier.padding(start = 4.dp)
//                    )
//                }
//            }
//        }
//    } else {
        FilesPlace(
            filesState = filesState,
            representationState = representationState,
            allowSelect = { settings.fullFilter(it.asCommon) },
            onChosen = onChosen,
            onOpen = onVisitDir,
            onSort = { filesState.comparator = it },
            modifier = modifier.padding(start = 4.dp)
        )
//    }
}
