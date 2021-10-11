package com.arpit.notes.ui.addnote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.arpit.notes.ui.theme.noteColors
import com.arpit.notes.util.observableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val noteId = savedStateHandle.get<String>("noteId")

    var noteColor by observableStateOf(noteColors.drop(1).random()) { saveNote() }
    var noteTitle by observableStateOf("") { saveNote() }
    var noteDescription by observableStateOf("") { saveNote() }

    private fun saveNote() {

    }

}
