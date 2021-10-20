package com.arpit.notes.ui.addnote

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arpit.notes.R
import com.arpit.notes.ui.theme.noteColors
import com.arpit.notes.util.repeatingClickable
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun AddNoteScreen(
    viewModel: AddNoteViewModel,
    navigateBack: () -> Unit
) {
    var isColorPickerOpen by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = viewModel.noteColor,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxHeight()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    keyboardController?.hide()
                    navigateBack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                IconButton(onClick = { isColorPickerOpen = !isColorPickerOpen }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_palette),
                        contentDescription = "Choose note color"
                    )
                }
            }

            AnimatedVisibility(isColorPickerOpen) {
                NoteColorsRow(viewModel.noteColor, viewModel::updateNoteColor)
            }

            TitleDescriptionSection(
                title = viewModel.noteTitle,
                onTitleChange = viewModel::updateTitle,
                description = viewModel.noteDesc,
                onDescriptionChange = viewModel::updateDescription,
                newNote = viewModel.newNote,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            )

            Divider()

            val undoAvailable by viewModel.undoAvailable.collectAsState()
            val redoAvailable by viewModel.redoAvailable.collectAsState()
            UndoRedoSection(undoAvailable, viewModel::undo, redoAvailable, viewModel::redo)
        }
    }
}

@Composable
fun NoteColorsRow(currentNoteColor: Color, updateNoteColor: (Color) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(noteColors) {
            NoteColorButton(
                isSelected = currentNoteColor == it,
                color = it,
                onClick = { updateNoteColor(it) }
            )
        }
    }
}

@Composable
fun NoteColorButton(isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(8.dp)
            .size(30.dp)
            .background(color = color, shape = CircleShape)
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = Color.Black,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Chosen color",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TitleDescriptionSection(
    title: String,
    onTitleChange: (String) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit,
    newNote: Boolean,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            NoteTitle(
                title = title,
                onTitleChange = onTitleChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            NoteDescription(
                description = description,
                onDescriptionChange = onDescriptionChange,
                newNote = newNote
            )
        }
    }
}

@Composable
fun NoteTitle(
    title: String,
    onTitleChange: (String) -> Unit
) {
    Box {
        BasicTextField(
            value = title,
            onValueChange = onTitleChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.h6,
            modifier = Modifier.fillMaxWidth()
        )
        if (title.isEmpty())
            Text(
                text = "Title",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.alpha(0.5f),
            )
    }
}

@Composable
fun NoteDescription(
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit,
    newNote: Boolean
) {
    val noteFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (newNote)
            noteFocusRequester.requestFocus()
    }

    Box {
        BasicTextField(
            value = description,
            onValueChange = onDescriptionChange,
            textStyle = MaterialTheme.typography.body1.copy(fontSize = 18.sp, lineHeight = 28.sp),
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(noteFocusRequester)
        )
        if (description.text.isEmpty())
            Text(
                text = "Note",
                style = MaterialTheme.typography.body1.copy(fontSize = 18.sp, lineHeight = 28.sp),
                modifier = Modifier.alpha(0.5f),
            )
    }
}

@Composable
fun UndoRedoSection(
    undoAvailable: Boolean,
    onUndo: () -> Unit,
    redoAvailable: Boolean,
    onRedo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsWithImePadding(),
        horizontalArrangement = Arrangement.Center
    ) {
        UndoRedoButton(
            painter = painterResource(id = R.drawable.ic_undo),
            contentDescription = "Undo",
            enabled = undoAvailable,
            onClick = onUndo
        )
        UndoRedoButton(
            painter = painterResource(id = R.drawable.ic_redo),
            contentDescription = "Redo",
            enabled = redoAvailable,
            onClick = onRedo
        )
    }
}

@Composable
fun UndoRedoButton(
    painter: Painter,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .repeatingClickable(
                enabled = enabled,
                onClick = onClick,
                initialDelayMillis = 500,
                delayMillis = 100,
                interactionSource = remember { MutableInteractionSource() },
                rippleRadius = 24.dp
            )
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
            Icon(
                painter = painter,
                contentDescription = contentDescription
            )
        }
    }
}