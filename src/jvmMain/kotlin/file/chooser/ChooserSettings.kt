package file.chooser

import Vocabulary
import androidx.compose.runtime.Composable
import file.chooser.ChooserMode.*
import java.io.File

enum class ChooserMode {
    OnlyFiles,
    FilesAndDirs,
    OnlyDirs
}

@Composable
fun defaultChooserSettings(
    mode: ChooserMode = FilesAndDirs,
    allowMultiSelection: Boolean = true,
    allowedExtensions: Map<String, String> = mapOf("*" to Vocabulary.all_files),
    filter: (File) -> Boolean = { true }
) = ChooserSettings(mode, allowMultiSelection, allowedExtensions, filter)

data class ChooserSettings(
    val mode: ChooserMode,
    val allowMultiSelection: Boolean,
    val allowedExtensions: Map<String, String> ,
    val filter: (File) -> Boolean
) {
    val fullFilter = { file: File ->
        val allowByExtension = when {
            allowedExtensions.isEmpty() -> true
            allowedExtensions.containsKey("*") -> true
            allowedExtensions.containsKey(file.extension) -> true
            else -> false
        }

        val allow = when (mode) {
            OnlyFiles -> file.isFile && allowByExtension
            OnlyDirs -> file.isDirectory
            FilesAndDirs -> allowByExtension
        }

        allow && filter(file)
    }
}