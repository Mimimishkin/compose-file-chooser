package file.chooser.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.rounded.Delete
import file.chooser.icons.*
import file.chooser.state.FilesState
import file.chooser.state.LocalChooserDialogContainerState
import file.chooser.state.RepresentationState.Companion.maxScale
import file.chooser.state.RepresentationState.Companion.minScale
import file.chooser.utils.ChooserActionsController
import file.chooser.utils.StandardFileOperations
import file.chooser.utils.Vocabulary
import file.chooser.utils.rememberStandardFileOperations
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.mapOf

@Composable
internal fun ActionsBar(
    filesState: FilesState,
    operations: StandardFileOperations = rememberStandardFileOperations(filesState),
    modifier: Modifier = Modifier
) {
    @Composable
    fun SimpleButton(onClick: () -> Unit, enabled: Boolean = true, content: @Composable () -> Unit) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = modifier
                .alpha(if (enabled) 1f else 0.3f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick, enabled = enabled)
                .padding(4.dp)
        ) {
            content()
        }
    }

    @Composable
    fun SimpleQuadButton(onClick: () -> Unit, enabled: Boolean = true, content: @Composable () -> Unit) {
        SimpleButton(onClick, enabled) {
            Box(Modifier.aspectRatio(1f / 1f), contentAlignment = Center) {
                content()
            }
        }
    }

    @Composable
    fun SimpleIcon(icon: ImageVector) {
        Icon(icon, null, Modifier.requiredSize(20.dp))
    }

    val controller = remember { ChooserActionsController(filesState, operations) }

    val representationState = LocalChooserDialogContainerState.current.representationState
    with(controller) { with(representationState) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SimpleButton(onClick = ::newFolder, mayNewFolder) {
                SimpleIcon(Icons.Rounded.CreateNewFolder)
                Spacer(Modifier.width(4.dp))
                Text(Vocabulary.new_folder)
            }

            HDivider(padding = 4.dp)

            SimpleQuadButton(onClick = ::copy, enabled = mayCopy) {
                SimpleIcon(Icons.Rounded.FileCopy)
            }

            SimpleQuadButton(onClick = ::paste, enabled = mayPaste) {
                SimpleIcon(Icons.Rounded.ContentPaste)
            }

            SimpleQuadButton(onClick = ::cut, enabled = mayCut) {
                SimpleIcon(Icons.Rounded.ContentCut)
            }

            SimpleQuadButton(onClick = ::delete, enabled = mayDelete) {
                SimpleIcon(Icons.Rounded.Delete)
            }

            SimpleQuadButton(onClick = ::rename, enabled = mayRename) {
                SimpleIcon(Icons.Rounded.DriveFileRenameOutline)
            }

            HDivider(padding = 4.dp)

            SimpleQuadButton(onClick = filesState::selectAll) {
                SimpleIcon(Icons.Rounded.SelectAll)
            }

            SimpleQuadButton(onClick = filesState::invertSelection) {
                SimpleIcon(Icons.Rounded.InverseSelection)
            }

            SimpleQuadButton(onClick = filesState::unselect) {
                SimpleIcon(Icons.Rounded.Deselect)
            }

            HDivider(padding = 4.dp)

            SimpleQuadButton(onClick = { showHierarchy = !showHierarchy }) {
                SimpleIcon(
                    if (showHierarchy) {
                        Icons.Rounded.VerticalSplit
                    } else {
                        Icons.Rounded.Rectangle
                    }
                )
            }

            Box {
                var expanded by remember { mutableStateOf(false) }
                val icon = { scale: Float ->
                    when(scale) {
                        -1f -> Icons.Rounded.TableRows
                        else -> Icons.Rounded.GridView
                    }
                }
                val scales = mapOf(
                    -1f to Vocabulary.table,
                    minScale to Vocabulary.tiny_icons,
                    0.7f to Vocabulary.small_icons,
                    1f to Vocabulary.normal_icons,
                    1.3f to Vocabulary.increased_icons,
                    1.55f to Vocabulary.big_icons,
                    maxScale to Vocabulary.large_icons,
                )

                SimpleQuadButton(onClick = { expanded = true }) {
                    SimpleIcon(icon(if (asGrid) scale else -1f))
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    scales.forEach { (newScale, name) ->
                        Row(Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                if (newScale != -1f) {
                                    asGrid = true
                                    scale = newScale
                                } else {
                                    asGrid = false
                                }
                                expanded = false
                            }
                            .padding(2.dp)
                        ) {
                            SimpleIcon(icon(newScale))
                            Spacer(Modifier.width(4.dp))
                            Text(name)
                        }
                    }
                }
            }
        }
    } }
}
