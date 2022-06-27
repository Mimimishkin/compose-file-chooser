package file.chooser

import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.SCALE_FAST
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import javax.swing.filechooser.FileSystemView
import java.awt.Image as AwtImage
import javax.swing.Icon as SwingIcon

private val view = FileSystemView.getFileSystemView()

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

val File.isDrive: Boolean get() = view.isDrive(this)

val File.isLink: Boolean get() = view.isLink(this)

val File.linkLocation: File get() = view.getLinkLocation(this)

val File.displayName: String get() = view.getSystemDisplayName(this.absoluteFile)

val File.typeDescription: String get() = view.getSystemTypeDescription(this)

fun File.icon(size: Int) = view.getSystemIcon(this, size, size)

val File.smallIcon get() = view.getSystemIcon(this)

fun SwingIcon.toBuffered(): BufferedImage {
    return BufferedImage(
        iconWidth,
        iconHeight,
        TYPE_INT_ARGB
    ).also { paintIcon(null, it.graphics, 0, 0) }
}