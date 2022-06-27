package file.chooser

import file.chooser.ChooserMode.*
import java.io.File

enum class ChooserMode {
    OnlyFiles,
    FilesAndDirs,
    OnlyDirs
}

data class ChooserSettings(
    val mode: ChooserMode = FilesAndDirs,
    val allowMultiSelection: Boolean = true,
    val allowedExtensions: Map<String, String> = mapOf("*" to "Все файлы"),
    val filter: (File) -> Boolean = { true }
) {
    val fullFilter: (File) -> Boolean = {
        val allowByExtension = when {
            allowedExtensions.containsKey("*") -> true
            allowedExtensions.containsKey(it.extension) -> true
            else -> false
        }

        val allow = when (mode) {
            OnlyFiles -> it.isFile && allowByExtension
            OnlyDirs -> it.isDirectory
            FilesAndDirs -> allowByExtension
        }

        allow && filter(it)
    }
}