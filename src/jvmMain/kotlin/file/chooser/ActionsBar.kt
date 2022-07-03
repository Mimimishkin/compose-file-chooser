package file.chooser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ViewSidebar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import file.chooser.ListRepresentationState.Companion.maxScale
import file.chooser.ListRepresentationState.Companion.minScale
import Vocabulary
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.mapOf

@Composable
internal fun ActionsBar(
    isHierarchyVisible: Boolean,
    onSwitchHierarchyVisibility: () -> Unit,
    listRepresentationState: ListRepresentationState,
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

    with(controller) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SimpleButton(onClick = ::newFolder, mayNewFolder) {
                SimpleIcon(Icons.Default.CreateNewFolder)
                Spacer(Modifier.width(4.dp))
                Text(Vocabulary.new_folder)
            }

            RowDivider(padding = 4.dp)

            SimpleQuadButton(onClick = ::copy, enabled = mayCopy) {
                SimpleIcon(Icons.Default.FileCopy)
            }

            SimpleQuadButton(onClick = ::paste, enabled = mayPaste) {
                SimpleIcon(Icons.Default.ContentPaste)
            }

            SimpleQuadButton(onClick = ::cut, enabled = mayCut) {
                SimpleIcon(Icons.Default.ContentCut)
            }

            SimpleQuadButton(onClick = ::delete, enabled = mayDelete) {
                SimpleIcon(Icons.Default.Delete)
            }

            SimpleQuadButton(onClick = ::rename, enabled = mayRename) {
                SimpleIcon(Icons.Default.DriveFileRenameOutline)
            }

            RowDivider(padding = 4.dp)

            SimpleQuadButton(onClick = filesState::selectAll) {
                SimpleIcon(Icons.Default.CheckBox)
            }

            SimpleQuadButton(onClick = filesState::invertSelection) {
                SimpleIcon(Icons.Default.IndeterminateCheckBox)
            }

            SimpleQuadButton(onClick = filesState::unselect) {
                SimpleIcon(Icons.Default.CheckBoxOutlineBlank)
            }

            RowDivider(padding = 4.dp)

            SimpleQuadButton(onClick = onSwitchHierarchyVisibility) {
                SimpleIcon(
                    if (isHierarchyVisible) {
                        Icons.Outlined.ViewSidebar
                    } else {
                        Icons.Filled.ViewSidebar
                    }
                )
            }

            Box {
                with(listRepresentationState) {
                    var expanded by remember { mutableStateOf(false) }
                    val icon = { scale: Float ->
                        when(scale) {
                            -1f -> Icons.Default.TableView
                            else -> Icons.Default.GridView
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
                        scales.forEach { (scale, name) ->
                            Row(Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    if (scale != -1f) {
                                        this@with.asGrid = true
                                        this@with.scale = scale
                                    } else {
                                        this@with.asGrid = false
                                    }
                                    expanded = false
                                }
                                .padding(2.dp)
                            ) {
                                SimpleIcon(icon(scale))
                                Spacer(Modifier.width(4.dp))
                                Text(name)
                            }
                        }
                    }
                }
            }
        }
    }
}
