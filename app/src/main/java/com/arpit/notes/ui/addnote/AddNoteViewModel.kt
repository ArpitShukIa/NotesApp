package com.arpit.notes.ui.addnote

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.arpit.notes.ui.theme.NoteColor0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddNoteViewModel : ViewModel() {

    private val _noteColor = MutableStateFlow(NoteColor0)
    val noteColor: StateFlow<Color> = _noteColor

    fun updateNoteColor(color: Color) {
        _noteColor.value = color
    }

}