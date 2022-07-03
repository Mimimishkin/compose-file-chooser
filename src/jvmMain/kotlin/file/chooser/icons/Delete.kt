package file.chooser.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Rounded.Delete: ImageVector by lazy {
    materialIcon(name = "Rounded.Delete") {
        materialPath {
            moveTo(6.0f, 19.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(8.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(9.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            horizontalLineTo(8.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(10.0f)
            close()
            moveTo(18.0f, 4.0f)
            horizontalLineToRelative(-2.5f)
            lineToRelative(-0.71f, -0.71f)
            curveToRelative(-0.18f, -0.18f, -0.44f, -0.29f, -0.7f, -0.29f)
            horizontalLineTo(9.91f)
            curveToRelative(-0.26f, 0.0f, -0.52f, 0.11f, -0.7f, 0.29f)
            lineTo(8.5f, 4.0f)
            horizontalLineTo(6.0f)
            curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
            reflectiveCurveToRelative(0.45f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(12.0f)
            curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
            reflectiveCurveToRelative(-0.45f, -1.0f, -1.0f, -1.0f)
            close()
        }
    }
}