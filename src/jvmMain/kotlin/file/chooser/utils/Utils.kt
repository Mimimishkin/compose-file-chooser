package file.chooser.utils

import java.awt.Desktop
import java.awt.Desktop.Action.MOVE_TO_TRASH
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import javax.swing.filechooser.FileSystemView
import javax.swing.Icon as SwingIcon

private val view = FileSystemView.getFileSystemView()
private val desktop = Desktop.getDesktop()
private val trashSupported = desktop.isSupported(MOVE_TO_TRASH)

object FileUtils {
    val shortcuts: List<File>
        get() {
            val desktop = roots[0]
            val realDesktopContent = File(desktop.path).listFiles()!!.map { it.path }
            return desktop.listFiles()!!.filter { it.isDirectory && it.path !in realDesktopContent }
        }

    val roots: List<File> get() = view.roots.toList()

    val defaultDirectory: File get() = view.defaultDirectory
}

val File.isSystem: Boolean get() = path == name//view.isFileSystem(this)

val File.isDrive: Boolean get() = view.isDrive(this)

val File.isLink: Boolean get() = view.isLink(this)

val File.linkLocation: File get() = view.getLinkLocation(this)

val File.displayName: String get() = view.getSystemDisplayName(this.absoluteFile)

val File.typeDescription: String get() = view.getSystemTypeDescription(this)

fun File.icon(size: Int) = view.getSystemIcon(this, size, size)

val File.smallIcon get() = view.getSystemIcon(this)

fun File.remove() = if (trashSupported) desktop.moveToTrash(this) else deleteRecursively()

fun SwingIcon.toBuffered(): BufferedImage {
    return BufferedImage(
        iconWidth,
        iconHeight,
        TYPE_INT_ARGB
    ).also { paintIcon(null, it.graphics, 0, 0) }
}