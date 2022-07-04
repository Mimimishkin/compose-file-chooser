package file.chooser.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import file.chooser.autoscroll
import file.chooser.autoscrollAtBounds
import file.chooser.utils.HierarchyFile

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FilesGrid(
    files: List<HierarchyFile>,
    modifier: Modifier = Modifier,
    cellSize: Dp = 80.dp,
    elementModifier: (HierarchyFile) -> Modifier = { Modifier }
) {
    val scrollState = rememberLazyListState()
    var autoscrollSpeed by remember { mutableStateOf(0f) }

    LaunchedEffect(autoscrollSpeed) {
        autoscroll(autoscrollSpeed) { scrollState.scrollBy(it) }
    }

    Row(modifier.autoscrollAtBounds(Vertical) { autoscrollSpeed = it }) {
        LazyVerticalGrid(
            state = scrollState,
            cells = GridCells.Adaptive(cellSize),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(files) { file ->
                Box {
                    FileCell(
                        file = file,
                        modifier = Modifier.requiredWidth(cellSize).then(elementModifier(file))
                    )
                }
            }
        }

        Spacer(Modifier.width(4.dp))

        VerticalScrollbar(rememberScrollbarAdapter(scrollState))
    }
}

val FileCellShape = RoundedCornerShape(10)

@Composable
private fun FileCell(
    file: HierarchyFile,
    modifier: Modifier = Modifier,
) {
    FileInfoArea(
        info = file.info.infoToString(),
        modifier = Modifier.clip(FileCellShape).then(modifier)
    ) {
        BoxWithConstraints {
            val maxWidth = maxWidth

            Column {
                FileIcon(
                    file = file,
                    modifier = Modifier
                        .aspectRatio(1f / 1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Text(
                    text = file.name,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).padding(bottom = 8.dp),
                    maxLines = 3,
                    fontSize = (maxWidth / 6.dp).coerceIn(8f..24f).sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
