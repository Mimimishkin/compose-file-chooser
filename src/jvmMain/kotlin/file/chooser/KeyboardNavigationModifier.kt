package file.chooser

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.key.Key.Companion.CtrlLeft
import androidx.compose.ui.input.key.Key.Companion.CtrlRight
import androidx.compose.ui.input.key.Key.Companion.DirectionDown
import androidx.compose.ui.input.key.Key.Companion.DirectionLeft
import androidx.compose.ui.input.key.Key.Companion.DirectionRight
import androidx.compose.ui.input.key.Key.Companion.DirectionUp
import androidx.compose.ui.input.key.Key.Companion.Enter
import androidx.compose.ui.input.key.Key.Companion.ShiftLeft
import androidx.compose.ui.input.key.Key.Companion.ShiftRight
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyDown
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyUp

internal enum class Direction {
    Up, Down, Left, Right;

    fun action(index: Int, action: (old: Int, new: Int) -> Boolean) = action(
        /*old = */ index,
        /*new = */ when(this) {
            Up, Left -> index - 1
            Down, Right -> index + 1
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
internal fun Modifier.keyboardNavigation(
    onCtrl: (Boolean) -> Boolean = { false },
    onShift: (Boolean) -> Boolean = { false },
    onEnter: () -> Boolean = { false },
    onDirection: (Direction) -> Boolean = { false },
) = onKeyEvent {
    when (it.key) {
        ShiftLeft, ShiftRight -> onShift(it.type == KeyDown)
        CtrlLeft, CtrlRight -> onCtrl(it.type == KeyDown)
        Enter -> if (it.type == KeyUp) onEnter() else false
        DirectionLeft -> if (it.type == KeyDown) onDirection(Direction.Left) else false
        DirectionRight -> if (it.type == KeyDown) onDirection(Direction.Right) else false
        DirectionUp -> if (it.type == KeyDown) onDirection(Direction.Up) else false
        DirectionDown -> if (it.type == KeyDown) onDirection(Direction.Down) else false
        else -> false
    }
}
