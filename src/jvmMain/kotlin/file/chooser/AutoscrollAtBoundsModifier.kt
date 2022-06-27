package file.chooser

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.autoscrollAtBounds(
    orientation: Orientation,
    boundsOffset: Dp = 40.dp,
    maxDelta: Dp = boundsOffset * 3,
    onSpeedChanged: (speedInPx: Float) -> Unit,
) = composed {
    val isHorizontal = orientation == Orientation.Horizontal
    val density = LocalDensity.current
    val boundsOffsetPx = with(density) { boundsOffset.roundToPx() }
    val maxDeltaPx = with(density) { maxDelta.toPx() }

    var isMoving by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(0) }
    val onStop = { onSpeedChanged(0f) }

    Modifier
        .onSizeChanged { size = if (isHorizontal) it.width else it.height }
        .onPointerEvent(PointerEventType.Press) { isMoving = true }
        .onPointerEvent(PointerEventType.Release) { isMoving = false; onStop() }
        .onPointerEvent(PointerEventType.Move) { event ->
            if (isMoving) {
                val position = event.changes.first().position
                val offset = if (isHorizontal) position.x else position.y
                val delta = when {
                    offset < boundsOffsetPx -> offset - boundsOffsetPx
                    offset > size - boundsOffsetPx -> offset - size + boundsOffsetPx
                    else -> 0f
                }.let { (it * it.sign).coerceAtMost(maxDeltaPx) * it.sign }

                val speed = delta / 6
                onSpeedChanged(speed)
            }
        }
}

tailrec suspend fun autoscroll(
    scrollSpeed: Float = 0f,
    delay: Long = 8L,
    onScroll: suspend (Float) -> Unit
) {
    if (scrollSpeed == 0f)
        return

    onScroll(scrollSpeed)

    delay(delay)

    autoscroll(scrollSpeed, delay, onScroll)
}