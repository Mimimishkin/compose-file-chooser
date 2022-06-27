package file.chooser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.key.Key.Companion.Enter
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyUp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction.Companion.Search
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.singleWindowApplication
import file.chooser.HierarchyFile.Companion.asHierarchy
import java.io.File
import java.lang.System.currentTimeMillis as currentTime

val LocalChooserDialogLastDirectory = compositionLocalOf { ChooserDialogState() }

class ChooserDialogState {
    companion object {
        val default = FileUtils.defaultDirectory
    }

    var dir by mutableStateOf(default)
}

@Composable
fun ChooserDialogContainer(content: @Composable () -> Unit) {
    val state = remember { ChooserDialogState() }

    CompositionLocalProvider(
        LocalChooserDialogLastDirectory provides state
    ) {
        content()
    }
}

@Composable
fun ChooserDialog(
    initialDirectory: File = LocalChooserDialogLastDirectory.current.dir,
    settings: ChooserSettings = ChooserSettings(),
    onChosen: (Set<File>) -> Unit = {}
) {
    val state = LocalChooserDialogLastDirectory.current

    Dialog(onCloseRequest = { onChosen(setOf()) }) {
        ChooserDialogContent(
            initialDirectory = initialDirectory.asHierarchy,
            settings = settings,
            onChosen = { onChosen(it.mapTo(mutableSetOf()) { file -> file.asCommon }) },
            onCurrentDirChanged = { state.dir = it.asCommon }
        )
    }
}

@Composable
internal fun ChooserDialogContent(
    initialDirectory: HierarchyFile,
    modifier: Modifier = Modifier,
    settings: ChooserSettings = ChooserSettings(),
    onChosen: (Set<HierarchyFile>) -> Unit = {},
    onCurrentDirChanged: (HierarchyFile) -> Unit = {}
) {
    val hierarchy = remember { ExplorerHierarchy(HierarchyFile.explorerShortcuts) }
    val history = rememberHistory(initialDirectory) { onCurrentDirChanged(it) }

    Column(modifier) {
        TopBar(Modifier.height(40.dp), history = history)

        Overview(
            explorerHierarchy = hierarchy,
            currentDir = history.current,
            onVisitDir = { history.visit(it) },
            settings = settings,
            onChosen = onChosen
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TopBar(
    modifier: Modifier = Modifier,
    history: FileTreeTravelHistory
) {
    Row(modifier, horizontalArrangement = spacedBy(4.dp)) {
        val color = MaterialTheme.colors.primary.copy(0.4f)
        val shape = CircleShape

        Box(
            contentAlignment = CenterStart,
            modifier = Modifier
                .fillMaxHeight()
                .padding(2.dp)
                .background(color, shape)
                .padding(horizontal = 8.dp)
                .padding(2.dp)
        ) {
            FileTreeTravelHistoryItem(
                history = history,
            )
        }

        var isEditing by remember { mutableStateOf(false) }
        var editingPath by remember { mutableStateOf("") }

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        val border by animateDpAsState(if (isEditing) 2.5.dp else 0.dp)
        val borderModifier = Modifier.border(width = border, color = color, shape = shape)
        val backgroundModifier = Modifier.background(color, shape)
        val clickable = Modifier.clip(shape).clickable { focusRequester.requestFocus() }

        Box(
            contentAlignment = CenterStart,
            modifier = Modifier
                .fillMaxHeight()
                .padding(2.dp)
                .then(if (isEditing) Modifier else clickable)
                .then(if (isEditing) borderModifier else backgroundModifier)
                .padding(horizontal = 8.dp)
                .padding(2.dp)
        ) {
            BasicTextField(
                value = editingPath,
//                enabled = isEditing,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusEvent {
                        if (isEditing != it.hasFocus) {
                            isEditing = it.hasFocus
                            editingPath = if (it.hasFocus) history.current.path else ""
                        }
                    }
                    .onPreviewKeyEvent {
                        if (isEditing && it.type == KeyUp && it.key == Enter) {
                            val file = File(editingPath)
                            if (file.exists()) {
                                history.visit(file.asHierarchy)
                            }
                            focusManager.clearFocus()
                            true
                        } else {
                            false
                        }
                    },
                onValueChange = { editingPath = it },
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                singleLine = true,
            )

            if (isEditing && editingPath.isEmpty()) {
                Text(
                    text = "Путь до папки",
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = Color.LightGray
                )
            }

            if (!isEditing) {
                CurrentPathElement(
                    file = history.list[history.currentIndex],
                    onSubPath = { history.visit(it) }
                )
            }
        }
    }
}

fun main() = singleWindowApplication {
    MaterialTheme {
        var isVisible by remember { mutableStateOf(true) }
        val choosen = remember { mutableListOf<File>().toMutableStateList() }

        if (isVisible) {
            ChooserDialog { choosen += it; isVisible = false }
        }

        Column {
            choosen.forEach {
                Text(it.path)
            }
        }
    }
}