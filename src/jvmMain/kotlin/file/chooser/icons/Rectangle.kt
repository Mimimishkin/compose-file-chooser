package file.chooser.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Rounded.Rectangle: ImageVector by lazy {
    materialIcon(name = "Rounded.Rectangle") {
        materialPath {
            moveTo(2f, 6f)
            verticalLineToRelative(12f)
            curveToRelative(0f,1.1f, 0.9f,2f, 2f,2f)
            horizontalLineToRelative(16f)
            curveToRelative(1.1f,0f, 2f,-0.9f, 2f,-2f)
            verticalLineTo(6f)
            curveToRelative(0f,-1.1f, -0.9f,-2f, -2f,-2f)
            horizontalLineTo(4f)
            curveTo(2.9f,4f, 2f,4.9f, 2f,6f)
            close()
        }
    }
}