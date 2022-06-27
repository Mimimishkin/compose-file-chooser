package file.chooser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import file.chooser.HierarchyFile.FileType
import file.chooser.icons.filled.AudioFile
import file.chooser.icons.filled.FileOpen
import file.chooser.icons.filled.FolderZip
import file.chooser.icons.filled.VideoFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

@Composable
internal fun FileIcon(file: HierarchyFile, modifier: Modifier = Modifier) {
    var large by remember { mutableStateOf(false) }
    val small = with(LocalDensity.current) { 32.dp.roundToPx() }

    Box(Modifier.onSizeChanged { large = min(it.height, it.width) > small }) {
        val icon by produceState<Painter?>(null, file, large) {
            value = withContext(Dispatchers.IO) { BitmapPainter(file.icon(large)) }
        }

        if (icon != null) {
            Image(
                painter = icon!!,
                contentDescription = null,
                modifier = modifier
            )
        } else {
            Icon(
                imageVector = when (file.type) {
                    FileType.Document -> Icons.Filled.Description
                    FileType.Image -> Icons.Filled.Image
                    FileType.Video -> Icons.Filled.VideoFile
                    FileType.Audio -> Icons.Filled.AudioFile
                    FileType.Shortcut -> Icons.Filled.FileOpen
                    FileType.Archive -> Icons.Filled.FolderZip
                    FileType.Other -> Icons.Filled.InsertDriveFile
                    FileType.Drive -> Icons.Filled.SnippetFolder
                    FileType.Directory -> Icons.Filled.Folder
                },
                contentDescription = null,
                modifier = modifier
            )
        }
    }
}