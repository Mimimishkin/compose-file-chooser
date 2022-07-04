package file.chooser.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FileInfoArea(
    info: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (info.isNotEmpty()) {
        TooltipArea(
            tooltip = {
                Box(
                    Modifier
                        .shadow(6.dp, shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colors.surface)
                ) {
                    Text(
                        text = info,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            },
            modifier = modifier,
            content = content,
            delayMillis = 800
        )
    } else {
        Box(modifier) {
            content()
        }
    }
}