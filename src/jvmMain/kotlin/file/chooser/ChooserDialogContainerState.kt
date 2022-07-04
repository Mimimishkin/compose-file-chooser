package file.chooser

import androidx.compose.runtime.*

class ChooserDialogContainerState {
    companion object {
        val default = FileUtils.defaultDirectory
    }

    var lastDir by mutableStateOf(default)
}

val LocalChooserDialogContainerState = compositionLocalOf { ChooserDialogContainerState() }

@Composable
fun ChooserDialogContainer(content: @Composable () -> Unit) {
    val state = remember { ChooserDialogContainerState() }

    CompositionLocalProvider(
        LocalChooserDialogContainerState provides state
    ) {
        content()
    }
}