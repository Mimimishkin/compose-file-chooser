package file.chooser.ui

import SplintedBox
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import file.chooser.ChooserSettings
import file.chooser.asHierarchy
import file.chooser.defaultChooserSettings
import file.chooser.state.FilesState
import file.chooser.state.LocalChooserDialogContainerState
import file.chooser.utils.ExplorerHierarchy
import file.chooser.utils.HierarchyFile

@Composable
internal fun Overview(
    explorerHierarchy: ExplorerHierarchy,
    modifier: Modifier = Modifier,
    filesState: FilesState,
    onVisitDir: (HierarchyFile) -> Unit = {},
    settings: ChooserSettings = defaultChooserSettings(),
    onChosen: (Set<HierarchyFile>) -> Unit = {},
) {
    val representationState = LocalChooserDialogContainerState.current.representationState

    HorizontalSplitPane(
        modifier = modifier,
        firstVisible = representationState.showHierarchy,
        firstMinSize = 70.dp,
        first = {
            HierarchyPreview(hierarchy = explorerHierarchy, onOpen = onVisitDir)
        },
        secondMinSize = 120.dp,
        second = {
            FilesPlace(
                filesState = filesState,
                representationState = representationState,
                allowSelect = settings.fullFilter.asHierarchy,
                onChosen = onChosen,
                onOpen = onVisitDir,
                onSort = { column, reverse -> filesState.sortParams = Pair(column, reverse) },
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    )
}

@Composable
private fun HorizontalSplitPane(
    modifier: Modifier = Modifier,
    firstVisible: Boolean = true,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    firstMinSize: Dp,
    secondMinSize: Dp,
) = SplintedBox(
    orientation = Orientation.Horizontal,
    modifier = modifier,
    components = listOfNotNull((firstMinSize to first).takeIf { firstVisible }, secondMinSize to second)
)
