package file.chooser.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.Circle: ImageVector by lazy {
    materialIcon(name = "Filled.Circle") {
        materialPath {
            moveTo(12.0f, 2.0f)
            curveTo(6.47f, 2.0f, 2.0f, 6.47f, 2.0f, 12.0f)
            reflectiveCurveToRelative(4.47f, 10.0f, 10.0f, 10.0f)
            reflectiveCurveToRelative(10.0f, -4.47f, 10.0f, -10.0f)
            reflectiveCurveTo(17.53f, 2.0f, 12.0f, 2.0f)
            close()
        }
    }
}