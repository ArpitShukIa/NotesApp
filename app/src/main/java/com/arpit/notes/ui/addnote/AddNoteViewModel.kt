package com.arpit.notes.ui.addnote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.arpit.notes.ui.theme.noteColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddNoteViewModel : ViewModel() {

    private val _noteState = MutableStateFlow(NoteState())
    val noteState: StateFlow<NoteState> = _noteState

}

class NoteState {
    var noteColor by mutableStateOf(noteColors.drop(1).random())
    var noteTitle by mutableStateOf("")
    var noteDescription by mutableStateOf("")
}
