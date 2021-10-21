package com.arpit.notes.ui.addnote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.notes.data.Note
import com.arpit.notes.data.NotesDao
import com.arpit.notes.ui.Destinations
import com.arpit.notes.ui.theme.NoteColor0
import com.arpit.notes.util.ListWithHistory
import com.arpit.notes.util.randomString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val notesDao: NotesDao,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val noteIdArg = savedStateHandle.get<String>(Destinations.NOTE_ID_KEY)
    private val noteId = noteIdArg ?: randomString()
    private val listWithHistory = ListWithHistory()
    private var noteUpdated = false
    private var noteCreatedAt: Long? = null
    val newNote = noteIdArg == null

    var noteDesc by mutableStateOf(TextFieldValue(""))
        private set

    var noteTitle by mutableStateOf("")
        private set

    var noteColor by mutableStateOf(NoteColor0)
        private set

    val undoAvailable = listWithHistory.undoAvailable
    val redoAvailable = listWithHistory.redoAvailable

    init {
        viewModelScope.launch {
            if (!newNote) {
                val note = notesDao.getNote(noteId)
                noteTitle = note.title
                noteDesc = TextFieldValue(note.description)
                noteColor = note.color
                noteCreatedAt = note.createdAt
                listWithHistory.updateCurrentState(noteDesc)
            }
            while (true) {
                if (noteUpdated) {
                    noteUpdated = false
                    saveNote()
                }
                delay(1_000)
            }
        }
    }

    private suspend fun saveNote() {
        if (noteTitle.isBlank() && noteDesc.text.isBlank()) {
            notesDao.deleteNote(noteId)
        } else {
            val newNote = Note(
                id = noteId,
                title = noteTitle.trim(),
                description = noteDesc.text.trim(),
                colorArgb = noteColor.toArgb(),
                createdAt = noteCreatedAt ?: System.currentTimeMillis(),
                lastUpdatedAt = System.currentTimeMillis()
            )
            notesDao.insertNote(newNote)
        }
    }

    fun updateTitle(title: String) {
        noteTitle = title
        noteUpdated = true
    }

    fun updateDescription(description: TextFieldValue) {
        noteDesc = description
        if (listWithHistory.notifyChange(description))
            noteUpdated = true
    }

    fun updateNoteColor(color: Color) {
        noteColor = color
        noteUpdated = true
    }

    fun undo() {
        noteDesc = listWithHistory.undo()
        noteUpdated = true
    }

    fun redo() {
        noteDesc = listWithHistory.redo()
        noteUpdated = true
    }

}
