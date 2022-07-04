package file.chooser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun RowDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = DividerAlpha),
    thickness: Dp = 0.5.dp,
    padding: Dp = 0.dp
) {
    Box(
        modifier
            .fillMaxHeight()
            .padding(vertical = padding)
            .width(thickness)
            .background(color = color)
    )
}

private const val DividerAlpha = 0.12f