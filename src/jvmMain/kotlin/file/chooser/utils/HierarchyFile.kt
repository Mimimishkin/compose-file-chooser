package file.chooser.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import file.chooser.ui.FilesTableColumn
import file.chooser.utils.HierarchyFile.FileType.Drive
import java.io.File

@Stable
internal data class HierarchyFile(private val file: File) {
    companion object {
        val explorerShortcuts = FileUtils.shortcuts.map { it.asHierarchy }

        val File.asHierarchy get() = HierarchyFile(this)

        internal fun FileComparator(
            column: FilesTableColumn = FilesTableColumn.Name,
            reverse: Boolean = false
        ): Comparator<HierarchyFile> = when (column) {
            FilesTableColumn.Name -> Comparator { first, second ->
                if (!reverse) {
                    IntuitiveComparator().compare(first.path, second.path)
                } else {
                    IntuitiveComparator().compare(second.path, first.path)
                }
            }
            FilesTableColumn.Type -> Comparator { first, second ->
                if (!reverse) {
                    IntuitiveComparator().compare(first.extension, second.extension)
                } else {
                    IntuitiveComparator().compare(second.extension, first.extension)
                }
            }
            FilesTableColumn.Size -> Comparator { first, second ->
                if (!reverse) {
                    first.asCommon.length().compareTo(second.asCommon.length())
                } else {
                    second.asCommon.length().compareTo(first.asCommon.length())
                }
            }
            FilesTableColumn.Date -> Comparator { first, second ->
                if (!reverse) {
                    first.asCommon.lastModified().compareTo(second.asCommon.lastModified())
                } else {
                    second.asCommon.lastModified().compareTo(first.asCommon.lastModified())
                }
            }
        }
    }

    val exists get() = file.exists()

    val info get() = FileInfo(file)

    val hasParent get() = file.parentFile != null

    val parent get() = file.parentFile.asHierarchy

    val allParents: List<HierarchyFile>
        get() {
            val list = mutableListOf<HierarchyFile>()
            var parent = this
            while (parent.hasParent && parent.type != Drive) {
                parent = parent.parent
                list += parent
            }
            return list.reversed()
        }

    val isFile get() = file.isFile

    val isLink get() = file.isLink

    val linkLocation get() = file.linkLocation.asHierarchy

    val isDirectory get() = file.isDirectory

    val isHidden get() = file.isHidden

    val isSystem get() = file.isSystem

    val children get() = file.listFiles()?.map { it.asHierarchy } ?: listOf()

    val subDirs get() = children.filter { it.isDirectory }

    val path get() = file.path

    val name get() = file.displayName

    fun icon(large: Boolean): ImageBitmap =
        (if (large) file.icon(64) else file.smallIcon).toBuffered().toComposeImageBitmap()

    fun child(name: String) = File(file, name).asHierarchy

    fun createDir() = file.mkdir()

    fun delete() = file.remove()

    fun moveTo(destination: HierarchyFile) {
        file.renameTo(destination.file)
    }

    fun copyTo(destination: HierarchyFile) {
        file.copyRecursively(destination.file, overwrite = true)
    }

    fun rename(name: String) = moveTo(parent.child(name))

    enum class FileType {
        Document,
        Image,
        Video,
        Audio,
        Shortcut,
        Archive,
        Drive,
        Directory,
        Other
    }

    val extension get() = file.extension

    val type get() = when {
        isFile -> when (extension) {
            "txt", "doc", "docx", "pdf" -> FileType.Document
            "jpg", "jp2", "jpeg", "webp", "png", "gif", "avif", "avifs", "jpe" -> FileType.Image
            "mp4", "gp3", "avi", "mov" -> FileType.Video
            "mp3", "opus" -> FileType.Audio
            "lnk" -> FileType.Shortcut
            "zip", "001", "z", "rar", "7z" -> FileType.Archive
            else -> FileType.Other
        }
        file.isDrive -> Drive
        else -> FileType.Directory
    }

    val asCommon get() = file
}