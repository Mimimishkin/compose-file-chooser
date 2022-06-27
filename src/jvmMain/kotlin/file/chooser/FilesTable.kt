package file.chooser

import Table
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import file.chooser.FilesTableColumn.*

internal enum class FilesTableColumn {
    Name,
    Type,
    Size,
    Date,
}

@Composable
internal fun FilesTable(
    files: List<HierarchyFile>,
    modifier: Modifier = Modifier,
    onSort: (FilesTableColumn, reverse: Boolean) -> Unit,
    elementModifier: (HierarchyFile) -> Modifier = { Modifier }
) {
    val scrollState = rememberLazyListState()
    var autoscrollSpeed by remember { mutableStateOf(0f) }

    LaunchedEffect(autoscrollSpeed) {
        autoscroll(autoscrollSpeed) { scrollState.scrollBy(it) }
    }

    Row(modifier.autoscrollAtBounds(Vertical) { autoscrollSpeed = it }) {
        Table(
            onSort = onSort,
            modifier = Modifier.weight(1f),
            state = scrollState,
            items = files,
            columns = FilesTableColumn.values(),
            row = { file, content ->
                FileInfoArea(
                    info = file.info.toString(),
                    modifier = elementModifier(file),
                    content = content
                )
            },
            head = { column ->
                Text(
                    text = when (column) {
                        Name -> "Имя"
                        Type -> "Тип"
                        Size -> "Размер"
                        Date -> "Дата изменения"
                    },
                    maxLines = 1,
                    fontSize = 14.sp
                )
            },
            cell = { file, column ->
                when (column) {
                    Name -> {
                        Row {
                            FileIcon(file = file, modifier = Modifier.size(20.dp).padding(2.dp))

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = file.name,
                                modifier = Modifier.align(CenterVertically),
                                maxLines = 1,
                                fontSize = 12.sp,
                            )
                        }
                    }
                    Type -> {
                        file.info.typeDescription?.let {
                            Text(
                                text = it,
                                maxLines = 1,
                                fontSize = 12.sp,
                            )
                        }
                    }
                    Size -> {
                        file.info.size?.let {
                            Text(
                                text = it,
                                maxLines = 1,
                                fontSize = 12.sp,
                            )
                        }
                    }
                    Date -> {
                        file.info.lastModified?.let {
                            Text(
                                text = it,
                                maxLines = 1,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        )

        Spacer(Modifier.width(4.dp))

        VerticalScrollbar(rememberScrollbarAdapter(scrollState))
    }
}