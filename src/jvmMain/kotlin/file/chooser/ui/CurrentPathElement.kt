package file.chooser.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import file.chooser.utils.HierarchyFile.Companion.asHierarchy
import file.chooser.utils.HierarchyFile
import file.chooser.utils.Vocabulary
import java.io.File

@Composable
internal fun CurrentPathElement(
    file: HierarchyFile,
    onSubPath: (HierarchyFile) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    @Composable
    fun PathPart(dir: HierarchyFile) {
        Text(
            dir.name,
            Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onSubPath(dir) }
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

@Composable
internal fun EditableCurrentPathElement(
    dir: HierarchyFile,
    color: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
    onPath: (HierarchyFile) -> Unit = {},
) {
    var path by remember { mutableStateOf("") }

    SimpleOutlinedTextField(
        modifier = modifier,
        value = path,
        onValueChange = { path = it },
        onRestoreValue = { path = dir.path },
        onAction = {
            val file = File(path)
            if (file.exists()) onPath(file.asHierarchy)
            true
        },
        color = color,
        hint = { Text(Vocabulary.folder_path) },
        placeholder = { CurrentPathElement(dir, onPath) }
    )
}