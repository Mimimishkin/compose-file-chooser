package file.chooser.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.ArrowUpward: ImageVector by lazy {
    materialIcon(name = "Filled.ArrowUpward") {
        materialPath {
            moveTo(4.0f, 12.0f)
            lineToRelative(1.41f, 1.41f)
            lineTo(11.0f, 7.83f)
            verticalLineTo(20.0f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(7.83f)
            lineToRelative(5.58f, 5.59f)
            lineTo(20.0f, 12.0f)
            lineToRelative(-8.0f, -8.0f)
            lineToRelative(-8.0f, 8.0f)
            close()
        }
    }
}