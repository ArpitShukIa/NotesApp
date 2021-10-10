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
import com.arpit.notes.R
import com.arpit.notes.ui.theme.NoteColor0
import com.arpit.notes.ui.theme.noteColors

@ExperimentalAnimationApi
@Composable
fun AddNoteScreen(changeStatusBarColor: (Color) -> Unit, navigateBack: () -> Unit) {
    var isColorPickerOpen by rememberSaveable { mutableStateOf(false) }
    var currentNoteColor by remember { mutableStateOf(NoteColor0) }
    val updateNoteColor: (Color) -> Unit = {
        changeStatusBarColor(it)
        currentNoteColor = it
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { isColorPickerOpen = !isColorPickerOpen }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_palette),
                            contentDescription = "Choose note color"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "Save note")
                    }
                }
            }
            AnimatedVisibility(isColorPickerOpen) {
                NoteColorsRow(updateNoteColor)
            }
        }
    }
}

@Composable
fun NoteColorsRow(changeStatusBarColor: (Color) -> Unit) {
    var selectedColor by remember {
        mutableStateOf(NoteColor0)
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        items(noteColors) {
            NoteColorButton(isSelected = selectedColor == it, color = it) {
                selectedColor = it
                changeStatusBarColor(it)
            }
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
