import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import file.chooser.ui.HDivider
import org.jetbrains.skia.FontWeight
import java.awt.Container
import kotlin.math.max
import kotlin.math.min

class SplitPaneState {

}

@Composable
fun rememberSplitPaneState() = remember { SplitPaneState() }

@Composable
fun SplintedBox(
    orientation: Orientation,
    components: List<Pair<Dp, @Composable () -> Unit>>,
    modifier: Modifier = Modifier,
    divider: @Composable () -> Unit = { if (orientation == Vertical) Divider() else HDivider() },
    handler: @Composable () -> Unit = { Box(if (orientation == Vertical) Modifier.fillMaxWidth().height(12.dp) else Modifier.fillMaxHeight().width(12.dp)) },
) {
    abstract class ContainerScope {
        abstract fun Modifier.weight(weight: Float): Modifier
    }

    @Composable
    fun Container(modifier: Modifier = Modifier, content: @Composable ContainerScope.() -> Unit) {
        if (orientation == Vertical) {
            Column(modifier) {
                val scope = this
                content(object : ContainerScope() {
                    override fun Modifier.weight(weight: Float): Modifier {
                        return with(scope) { weight(weight) }
                    }
                })
            }
        } else {
            Row(modifier) {
                val scope = this
                content(object : ContainerScope() {
                    override fun Modifier.weight(weight: Float): Modifier {
                        return with(scope) { weight(weight) }
                    }
                })
            }
        }
    }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val weights = remember { List(components.size) { 1f / components.size }.toMutableStateList() }
        val size = with(density) { (if (orientation == Vertical) maxHeight else maxWidth).toPx() }
        fun Float.pxToWeight() = this / size

        Container(Modifier.fillMaxSize()) {
            for (index in components.indices) {
                Box(Modifier.weight(weights[index])) {
                    components[index].second()
                }
                if (index != components.lastIndex) {
                    divider()
                }
            }
        }

        Container(Modifier.fillMaxSize()) {
            for (index in components.indices) {
                Spacer(Modifier.weight(weights[index]))
                if (index != components.lastIndex) {
                    Box(
                        contentAlignment = Center,
                        modifier = Modifier.draggable(
                            orientation = orientation,
                            state = rememberDraggableState { delta ->
                                val weight = delta.pxToWeight()

                                val firstMinSize = with(density) { components[index].first.toPx() }
                                val secondMinSize = with(density) { components[index + 1].first.toPx() }

                                if (
                                    weights[index] + weight >= firstMinSize.pxToWeight() &&
                                    weights[index + 1] - weight >= secondMinSize.pxToWeight()
                                ) {
                                    weights[index] += weight
                                    weights[index + 1] -= weight
                                }
                            },
                            startDragImmediately = true,
                        )
                    ) {
                        handler()
                    }
                }
            }
        }
    }
}

