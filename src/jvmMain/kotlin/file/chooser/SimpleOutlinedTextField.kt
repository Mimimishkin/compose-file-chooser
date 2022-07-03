package file.chooser

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.input.key.Key.Companion.Enter
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyUp
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SimpleOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onRestoreValue: () -> Unit,
    onAction: () -> Boolean,
    color: Color = MaterialTheme.colors.primary,
    editingWidth: Dp = 2.5.dp,
    fontSize: TextUnit = 14.sp,
    placeholder: @Composable () -> Unit = {},
    hint: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        var isEditing by remember { mutableStateOf(false) }

        val width by animateDpAsState(if (isEditing) editingWidth else maxWidth / 2)
        val color = color
        val shape = RoundedCornerShape(8.dp)

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        Box(contentAlignment = CenterStart) {
            BasicTextField(
                value = if (isEditing) value else "",
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = fontSize),
                decorationBox = { internalTextField ->
                    Box {
                        if (isEditing && value.isEmpty()) {
                            CompositionLocalProvider(
                                LocalContentColor provides LightGray
                            ) {
                                hint()
                            }
                        }

                        internalTextField()
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(editingWidth)
                    .focusRequester(focusRequester)
                    .onFocusEvent {
                        if (isEditing != it.hasFocus) {
                            isEditing = it.hasFocus
                            if (isEditing) onRestoreValue()
                        }
                    }
                    .onKeyEvent {
                        if (it.key == Enter && it.type == KeyUp && isEditing && onAction()) {
                            focusManager.clearFocus()
                            true
                        } else {
                            false
                        }
                    }
            )

            Box(Modifier.fillMaxSize().clip(shape).border(width, color, shape))

            if (!isEditing) {
                Box(Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .clickable { focusRequester.requestFocus() }
                ) {
                    Box(Modifier.fillMaxSize().padding(horizontal = 8.dp).padding(editingWidth), contentAlignment = CenterStart) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colors.onPrimary
                        ) {
                            placeholder()
                        }
                    }
                }
            }
        }
    }
}