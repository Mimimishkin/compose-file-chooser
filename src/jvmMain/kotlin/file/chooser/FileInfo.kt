package file.chooser

import java.io.File
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.text.DateFormat
import java.text.DateFormat.SHORT
import java.text.NumberFormat
import java.util.*

internal class FileInfo(file: File) {
    val size: String?
    val typeDescription: String?
    val lastModified: String?
    val creationTime: String?

    init {
        val attr = try {
            Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
        } catch (_: InvalidPathException) {
            object : BasicFileAttributes {
                override fun lastModifiedTime() = FileTime.fromMillis(file.lastModified())

                override fun lastAccessTime() = null

                override fun creationTime() = null

                override fun isRegularFile() = false

                override fun isDirectory() = file.isDirectory

                override fun isSymbolicLink() = with(FileUtils) { file.isLink }

                override fun isOther() = true

                override fun size() = file.length()

                override fun fileKey() = null
            }
        }

        size = if (!attr.isDirectory) formatSize(attr.size()) else null
        typeDescription = file.typeDescription
        lastModified = attr.lastModifiedTime()?.let { formatDate(it) }
        creationTime = attr.creationTime()?.let { formatDate(it) }
    }

    override fun toString(): String {
        val info = mutableListOf<String>()
        if (typeDescription != null) info += "Тип: $typeDescription"
        if (creationTime != null) info += "Дата создания: $creationTime"
        if (lastModified != null) info += "Дата редактирования: $lastModified"
        if (size != null) info += "Размер: $size"
        return info.joinToString("\n")
    }
}

private val dateFormatter = DateFormat.getDateTimeInstance(SHORT, SHORT)

private fun formatDate(date: FileTime): String {
    return dateFormatter.format(Date(date.toMillis()))
}

private enum class SizeType(val displayedName: String, val toNext: (Double) -> Double = { it / 1024 }) {
        bit("бит", { it / 8 }),
        Byte("байт"),
        KiloByte("Кб"),
        MegaBite("Мб"),
        GigaBite("Гб"),
        TeraBite("Тб");

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

    return "${sizeFormatter.format(curSize)} ${curType.displayedName}"
}
