package file.chooser.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class RepresentationState {
    var asGrid by mutableStateOf(true)

    var scale by mutableStateOf(1f)

    var showHierarchy by mutableStateOf(true)

    companion object {
        val normalCellSize = 80.dp

        val minScale = 0.5f

        val maxScale = 2f
    }
}