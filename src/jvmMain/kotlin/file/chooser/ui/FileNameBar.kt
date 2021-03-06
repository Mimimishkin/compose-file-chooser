package file.chooser.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import file.chooser.ui.SimpleOutlinedTextField
import file.chooser.asHierarchy
import file.chooser.extensionFilter
import file.chooser.state.FilesState
import file.chooser.utils.Vocabulary

@Composable
internal fun FileNameBar(
    filesState: FilesState,
    extensions: Map<String, String>,
    modifier: Modifier = Modifier,
    onChosen: (name: String) -> Unit = {}
) = with(filesState) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        val color = MaterialTheme.colors.primary.copy(0.4f)
        var name by remember { mutableStateOf("") }

        SimpleOutlinedTextField(
            value = name,
            onValueChange = { name = it },
            onRestoreValue = { name = lastSelected?.name ?: "" },
            onAction = { onChosen(name); true; },
            color = color,
            hint = { Text(Vocabulary.file_name, maxLines = 1) },
            placeholder = { Text(lastSelected?.name ?: "", fontWeight = FontWeight.Bold, maxLines = 1) },
            modifier = Modifier.weight(1f)
        )

        if (extensions.isNotEmpty()) {
            var extension by remember { mutableStateOf(extensions.entries.first()) }
            var expanded by remember { mutableStateOf(false) }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(4.dp)
            ) {
                Text("${extension.value} (.${extension.key})")

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    extensions.forEach {
                        Text(
                            text = "${it.value} (.${it.key})",
                            Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    extension = it
                                    expanded = false
                                    filter = extensionFilter(setOf(it.key)).asHierarchy
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}