package file.chooser.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

internal class ListRepresentationState {
    var asGrid by mutableStateOf(true)

    var scale by mutableStateOf(1f)

    companion object {
        val normalCellSize = 80.dp

        val minScale = 0.5f

        val maxScale = 2f
    }
}