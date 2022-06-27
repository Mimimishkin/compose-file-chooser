package file.chooser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun TableOrGridSwitcher(
    count: Int,
    modifier: Modifier = Modifier,
    onTable: () -> Unit = {},
    onGrid: () -> Unit = {},
) {
    val ButtonShape = RoundedCornerShape(4.dp)

    Box(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(8.dp))
            Text("Элементов: $count", fontSize = 12.sp)

            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = Icons.Outlined.TableRows,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f / 1f)
                    .fillMaxHeight()
                    .padding(2.dp)
                    .clip(ButtonShape)
                    .clickable(onClick = onTable)
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.GridView,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f / 1f)
                    .fillMaxHeight()
                    .padding(2.dp)
                    .clip(ButtonShape)
                    .clickable(onClick = onGrid)
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}