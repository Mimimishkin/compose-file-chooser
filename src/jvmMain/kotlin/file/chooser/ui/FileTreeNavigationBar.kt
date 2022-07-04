package file.chooser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import file.chooser.ui.EditableCurrentPathElement
import file.chooser.ui.FileTreeTravelHistoryItem
import file.chooser.utils.FileTreeTravelHistory

@Composable
internal fun FileTreeNavigationBar(
    history: FileTreeTravelHistory,
    modifier: Modifier = Modifier
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        val color = MaterialTheme.colors.primary.copy(0.4f)

        FileTreeTravelHistoryItem(
            history = history,
            modifier = Modifier.fillMaxHeight()
        )

        EditableCurrentPathElement(
            dir = history.list[history.currentIndex],
            color = color,
            onPath = { history.visit(it) }
        )
    }
}