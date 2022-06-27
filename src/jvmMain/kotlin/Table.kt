import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <ItemType, ColumnType> Table(
    columns: Array<ColumnType>,
    items: List<ItemType>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    onSort: ((column: ColumnType, reverse: Boolean) -> Unit)? = null,
    head: @Composable (column: ColumnType) -> Unit,
    row: @Composable (item: ItemType, content: @Composable () -> Unit) -> Unit,
    cell: @Composable (item: ItemType, column: ColumnType) -> Unit
) {
    val weights = remember { columns.map { Pair(it, 1f / columns.size) }.toMutableStateMap() }
    var sortedBy by remember { mutableStateOf<ColumnType?>(null) }
    var reversed by remember { mutableStateOf(false) }

    LazyColumn(state = state, modifier = modifier) {
        stickyHeader {
            Row(Modifier.height(IntrinsicSize.Max)) {
                columns.forEach { column ->
                    Surface(Modifier.fillMaxHeight().weight(weights[column]!!)) {
                        if (onSort != null) {
                            Row(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        if (sortedBy != column) {
                                            sortedBy = column
                                        } else {
                                            reversed = !reversed
                                        }

                                        onSort(sortedBy!!, reversed)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (sortedBy == column) {
                                    Spacer(Modifier.width(4.dp))

                                    Icon(
                                        imageVector = if (!reversed) {
                                            Icons.Default.KeyboardArrowUp
                                        } else {
                                            Icons.Default.KeyboardArrowDown
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.requiredSize(16.dp)
                                    )
                                }

                                Box(Modifier.padding(4.dp)) {
                                    head(column)
                                }
                            }
                        } else {
                            Box(Modifier.padding(4.dp), contentAlignment = CenterStart) {
                                head(column)
                            }
                        }
                    }
                }
            }
        }
        items(items) { item ->
            row(item) {
                Row(Modifier.height(IntrinsicSize.Max), verticalAlignment = Alignment.CenterVertically) {
                    columns.forEach { column ->
                        Box(Modifier.weight(weights[column]!!)) {
                            cell(item, column)
                        }
                    }
                }
            }
        }
    }
}