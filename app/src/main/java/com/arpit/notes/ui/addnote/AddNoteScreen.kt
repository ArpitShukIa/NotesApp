package com.arpit.notes.ui.addnote

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arpit.notes.R
import com.arpit.notes.ui.theme.noteColors
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalAnimationApi
@Composable
fun AddNoteScreen(
    navigateBack: () -> Unit,
    viewModel: AddNoteViewModel
) {
    var isColorPickerOpen by rememberSaveable { mutableStateOf(false) }

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
                IconButton(onClick = navigateBack) {
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
                NoteColorsRow(viewModel.noteColor) { viewModel.noteColor = it }
            }

            TitleDescriptionSection(
                title = viewModel.noteTitle,
                onTitleChange = { viewModel.noteTitle = it },
                description = viewModel.noteDesc,
                onDescriptionChange = { viewModel.noteDesc = it },
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
    modifier: Modifier = Modifier,
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit
) {
    val noteFocusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        noteFocusRequester.requestFocus()
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            text = title,
            onTextChange = onTitleChange,
            placeholderText = "Title",
            fontSize = 24.sp,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            text = description,
            onTextChange = onDescriptionChange,
            placeholderText = "Note",
            fontSize = 18.sp,
            singleLine = false,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(noteFocusRequester)
        )
    }
}

@Composable
fun CustomTextField(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    placeholderText: String,
    fontSize: TextUnit,
    singleLine: Boolean,
    modifier: Modifier
) {

    Box {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = singleLine,
            textStyle = TextStyle(fontSize = fontSize),
            modifier = modifier
        )
        if (text.text.isEmpty())
            Text(
                text = placeholderText,
                style = TextStyle(fontSize = fontSize),
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
        IconButton(
            enabled = undoAvailable,
            onClick = onUndo
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_undo),
                contentDescription = "Undo"
            )
        }
        IconButton(
            enabled = redoAvailable,
            onClick = onRedo
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_redo),
                contentDescription = "Redo"
            )
        }
    }
}