package file.chooser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import file.chooser.FilesTableColumn.*
import file.chooser.HierarchyFile.Companion.FileComparator
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import java.io.File

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
internal fun Overview(
    explorerHierarchy: ExplorerHierarchy,
    modifier: Modifier = Modifier,
    currentDir: HierarchyFile,
    onVisitDir: (HierarchyFile) -> Unit = {},
    settings: ChooserSettings = ChooserSettings(),
    onChosen: (Set<HierarchyFile>) -> Unit = {}
) {
    Column(modifier) {
        var asGrid by remember { mutableStateOf(true) }
        var comparator by remember { mutableStateOf(
            Comparator<HierarchyFile> { first, second ->
                IntuitiveComparator().compare(first.path, second.path)
            }
        ) }

        HorizontalSplitPane(Modifier.weight(1f)) {
            first(60.dp) {
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
                FilesSite(
                    files = currentDir.sortedChildren(comparator),
                    asGrid = asGrid,
                    allowSelect = { settings.fullFilter(it.asCommon) },
                    onChosen = onChosen,
                    onOpen = onVisitDir,
                    onSort = { column, reverse ->
                        comparator = FileComparator(column, reverse)
                    }
                )
            }
        }

        TableOrGridSwitcher(
            count = currentDir.count,
            onTable = { asGrid = false },
            onGrid = { asGrid = true },
            modifier = Modifier.height(24.dp).background(MaterialTheme.colors.secondary)
        )
    }
}
