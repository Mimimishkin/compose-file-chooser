package file.chooser

import Vocabulary
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import kotlin.collections.Collection
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.first
import kotlin.collections.last
import kotlin.collections.single
import kotlin.collections.toList

@Composable
internal fun RenameFilesDialog(
    files: Collection<HierarchyFile>,
    onCancel: () -> Unit,
    onNameChosen: (String) -> Unit,
) {
    Dialog(
        onCloseRequest = onCancel,
        title = Vocabulary.rename_files,
        resizable = false,
        state = rememberDialogState(size = DpSize.Unspecified)
    ) {
        Column {
            var name by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                BoxWithConstraints(Modifier.size(72.dp)) {
                    val maxSize = min(maxWidth, maxHeight)

                    when (files.size) {
                        1 -> {
                            val file = files.single()

                            FileIcon(file, Modifier.size(maxSize))
                        }
                        2 -> {
                            val offset = maxSize / 6
                            val size = maxSize - offset
                            val first = files.first()
                            val second = files.last()

                            FileIcon(first, Modifier.size(size))
                            FileIcon(second, Modifier.padding(start = offset, top = offset).size(size))
                        }
                        else -> {
                            val offset = maxSize / 8
                            val size = maxSize - offset * 2
                            val (first, second, third) = files.toList()

                            FileIcon(first, Modifier.size(size))
                            FileIcon(second, Modifier.padding(start = offset, top = offset).size(size))
                            FileIcon(third, Modifier.padding(start = offset * 2, top = offset * 2).size(size))
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Vocabulary.new_name) },
                    singleLine = true,
                    shape = CircleShape,
                    modifier = Modifier.width(300.dp)
                )
            }

            Row {
                Spacer(Modifier.width(8.dp))

                Button(onClick = { onNameChosen(name) }, shape = CircleShape) {
                    Text(Vocabulary.confirm)
                }

                Spacer(Modifier.width(8.dp))

                OutlinedButton(onClick = onCancel, shape = CircleShape) {
                    Text(Vocabulary.cancel)
                }
            }
        }
    }
}