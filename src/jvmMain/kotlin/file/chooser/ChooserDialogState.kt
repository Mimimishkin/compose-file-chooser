package file.chooser

import Vocabulary
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File

class ChooserDialogState() {
    var showDialog by mutableStateOf(false)
        private set

    private val channel = Channel<Set<File>>()

    suspend fun onChosen(files: Set<File>) = channel.send(files)

    suspend fun choose(): Set<File> {
        showDialog = true
        val files = channel.receive()
        showDialog = false
        return files
    }
}

@Composable
fun rememberChooserDialogState(
    initialDirectory: File = LocalChooserDialogContainerState.current.lastDir,
    settings: ChooserSettings = defaultChooserSettings(),
    title: String = when (settings.mode) {
        ChooserMode.OnlyFiles -> Vocabulary.choose_files
        ChooserMode.FilesAndDirs -> Vocabulary.choose_files_and_dirs
        ChooserMode.OnlyDirs -> Vocabulary.choose_dirs
    },
    icon: Painter? = painterResource("standard_file_chooser_icon.png")
): ChooserDialogState {
    val state = remember { ChooserDialogState() }
    val scope = rememberCoroutineScope()

    if (state.showDialog) {
        ChooserDialog(
            initialDirectory = initialDirectory,
            settings = settings,
            title = title,
            icon = icon,
            onChosen = { scope.launch { state.onChosen(it) } }
        )
    }

    return state
}