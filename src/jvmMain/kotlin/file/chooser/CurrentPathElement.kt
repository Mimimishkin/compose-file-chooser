package file.chooser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
internal fun CurrentPathElement(
    file: HierarchyFile,
    modifier: Modifier = Modifier,
    onSubPath: (HierarchyFile) -> Unit = {},
) {
    @Composable
    fun PathPart(file: HierarchyFile) {
        Text(
            file.name,
            Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onSubPath(file) }
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }

    LazyRow(
        modifier = modifier,
        verticalAlignment = CenterVertically
    ) {
        items(file.allParents) {
            PathPart(it)
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        item {
            PathPart(file)
        }
    }
}