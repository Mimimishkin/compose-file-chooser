package file.chooser.utils

import androidx.compose.runtime.Composable
import java.io.File
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.NoSuchFileException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.text.DateFormat
import java.text.DateFormat.SHORT
import java.text.NumberFormat
import java.util.*

internal class FileInfo(file: File) {
    private val size0: Long?
    private val typeDescription0: String?
    private val lastModified0: FileTime?
    private val creationTime0: FileTime?


    val size @Composable get() = size0?.let { formatSize(it) }
    val typeDescription @Composable get() = typeDescription0
    val lastModified @Composable get() = lastModified0?.let { formatDate(it) }
    val creationTime @Composable get() = creationTime0?.let { formatDate(it) }

    init {
        val attr = try {
            Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
        } catch (_: InvalidPathException) {
            GeneralFileAttributes(file)
        } catch (_: NoSuchFileException) {
            NoneFileAttributes()
        }

        size0 = if (!attr.isDirectory) attr.size() else null
        typeDescription0 = file.typeDescription
        lastModified0 = attr.lastModifiedTime()
        creationTime0 = attr.creationTime()
    }

    private fun NoneFileAttributes() = object : BasicFileAttributes {
        override fun lastModifiedTime() = FileTime.fromMillis(0)

        override fun lastAccessTime() = null

        override fun creationTime() = null

        override fun isRegularFile() = false

        override fun isDirectory() = false

        override fun isSymbolicLink() = false

        override fun isOther() = false

        override fun size() = 0L

        override fun fileKey() = null
    }

    private fun GeneralFileAttributes(file: File) = object : BasicFileAttributes {
        override fun lastModifiedTime() = FileTime.fromMillis(file.lastModified())

        override fun lastAccessTime() = null

        override fun creationTime() = null

        override fun isRegularFile() = false

        override fun isDirectory() = file.isDirectory

        override fun isSymbolicLink() = file.isLink

        override fun isOther() = true

        override fun size() = file.length()

        override fun fileKey() = null
    }

    @Composable
    fun infoToString(): String {
        val info = mutableListOf<String>()
        if (typeDescription != null) info += "${Vocabulary.type}: $typeDescription"
        if (creationTime != null) info += "${Vocabulary.creation_time}: $creationTime"
        if (lastModified != null) info += "${Vocabulary.edit_time}: $lastModified"
        if (size != null) info += "${Vocabulary.size}: $size"
        return info.joinToString("\n")
    }
}

private val dateFormatter = DateFormat.getDateTimeInstance(SHORT, SHORT)

private fun formatDate(date: FileTime): String {
    return dateFormatter.format(Date(date.toMillis()))
}

private enum class SizeType(val displayedName: @Composable () -> String, val toNext: (Double) -> Double = { it / 1024 }) {
    bit({ Vocabulary.bit }, { it / 8 }),
    Byte({ Vocabulary.byte }),
    KiloByte({ Vocabulary.k_byte }),
    MegaBite({ Vocabulary.m_byte }),
    GigaBite({ Vocabulary.g_bite }),
    TeraBite({ Vocabulary.t_bite });

    fun next(size: Double) = toNext(size) to when (this) {
        bit -> Byte
        Byte -> KiloByte
        KiloByte -> MegaBite
        MegaBite -> GigaBite
        GigaBite -> TeraBite
        TeraBite -> null
    }
}

private val sizeFormatter = NumberFormat.getCompactNumberInstance()

@Composable
private fun formatSize(size: Long): String {
    var curType = SizeType.bit
    var curSize = size.toDouble()

    while (true) {
        val (nextSize, nextType) = curType.next(curSize)
        if (nextSize < 1 || nextType == null) {
            break
        } else {
            curType = nextType
            curSize = nextSize
        }
    }

    return "${sizeFormatter.format(curSize)} ${curType.displayedName()}"
}
