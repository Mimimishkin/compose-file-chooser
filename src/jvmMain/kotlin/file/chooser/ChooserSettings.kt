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
    multiSelection: Boolean = true,
    extensions: Map<String, String> = mapOf("exe" to "Исполняемый файл"),
//    extensions: Map<String, String> = mapOf("*" to Vocabulary.all_files),
    filter: (File) -> Boolean = { true }
) = ChooserSettings(
    mode = mode,
    allowMultiSelection = multiSelection,
    allowedExtensions = if (mode == OnlyDirs) mapOf() else extensions,
    filter = filter
)

data class ChooserSettings(
    val mode: ChooserMode,
    val allowMultiSelection: Boolean,
    val allowedExtensions: Map<String, String> ,
    val filter: (File) -> Boolean
) {
    val firstExtension = allowedExtensions.entries.firstOrNull()?.key

    val extensionsFilter get() = { file: File -> file.isDirectory || extensionFilter(allowedExtensions.keys)(file) }

    val modeFilter get() = { file: File ->
         when(mode) {
             OnlyFiles -> file.isFile
             OnlyDirs -> file.isDirectory
             FilesAndDirs -> true
         }
    }

    val fullFilter get() = { file: File ->
        modeFilter(file) && extensionsFilter(file) && filter(file)
    }
}

fun extensionFilter(extensions: Set<String>) = { file: File ->
    when {
        extensions.isEmpty() -> true
        "*" in extensions -> true
        file.extension in extensions -> true
        else -> false
    }
}

internal val ((File) -> Boolean).asHierarchy get() = { file: HierarchyFile -> this(file.asCommon) }