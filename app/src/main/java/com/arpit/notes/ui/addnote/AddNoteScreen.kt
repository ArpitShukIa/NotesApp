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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arpit.notes.R
import com.arpit.notes.ui.theme.noteColors
import kotlinx.coroutines.flow.collect

@ExperimentalAnimationApi
@Composable
fun AddNoteScreen(
    changeStatusBarColor: (Color) -> Unit,
    navigateBack: () -> Unit,
    viewModel: AddNoteViewModel = viewModel()
) {
    var isColorPickerOpen by rememberSaveable { mutableStateOf(false) }
    val currentNoteColor by viewModel.noteColor.collectAsState()

    LaunchedEffect(true) {
        viewModel.noteColor.collect {
            changeStatusBarColor(currentNoteColor)
        }
    }

    Surface(
        color = currentNoteColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = navigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                IconButton(onClick = { isColorPickerOpen = !isColorPickerOpen }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_palette),
                        contentDescription = "Choose note color"
                    )
                }
            }
            AnimatedVisibility(isColorPickerOpen) {
                NoteColorsRow(currentNoteColor, viewModel::updateNoteColor)
            }
        }
    }
}

@Composable
fun NoteColorsRow(currentNoteColor: Color, updateNoteColor: (Color) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
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
