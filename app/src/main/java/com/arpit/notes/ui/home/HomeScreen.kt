package com.arpit.notes.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arpit.notes.data.NoteItem
import com.arpit.notes.ui.theme.NoteColor0
import com.arpit.notes.util.thenIf
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToAddNoteScreen: (String?) -> Unit
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        backgroundColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToAddNoteScreen(null) },
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add note"
                )
            }
        }
    ) {
        val insets = LocalWindowInsets.current
        val navBarBottom = with(LocalDensity.current) { insets.navigationBars.bottom.toDp() }
        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp + navBarBottom)) {
            item {
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                )
            }
            items(notes) {
                NoteListItem(
                    noteItem = it,
                    onLongClick = { viewModel.onNoteLongClick(it) },
                    onClick = { viewModel.onNoteClick(it) }
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun NoteListItem(
    noteItem: NoteItem,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    val (note, selected) = noteItem
    Card(
        elevation = if (selected) 8.dp else 0.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(note.color)
                .thenIf(selected) {
                    border(
                        width = 3.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                .thenIf(note.color == NoteColor0 && !selected) {
                    border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                .combinedClickable(
                    onLongClick = onLongClick,
                    onClick = onClick
                )
                .padding(16.dp)
        ) {
            if (note.title.isNotEmpty()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
                )
            }
            if (note.title.isNotEmpty() && note.description.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            }
            if (note.description.isNotEmpty()) {
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
