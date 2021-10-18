package com.arpit.notes.ui.addnote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.notes.data.Note
import com.arpit.notes.data.NotesDao
import com.arpit.notes.ui.theme.noteColors
import com.arpit.notes.util.ListWithHistory
import com.arpit.notes.util.observableStateOf
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

    private val note = savedStateHandle.get<Note>("note")
    private val initialTitle = note?.title ?: ""
    private val initialDesc = note?.description ?: ""
    private val initialColor = note?.color ?: noteColors.drop(1).random()
    private val noteId = note?.id ?: randomString()

    private var noteUpdated = false
    private var lastChangeWasUndoRedo = false
    private val listWithHistory = ListWithHistory(TextFieldValue(initialDesc))

    var noteColor by observableStateOf(initialColor) { noteUpdated = true }
    var noteTitle by observableStateOf(TextFieldValue(initialTitle)) { noteUpdated = true }
    var noteDesc by observableStateOf(TextFieldValue(initialDesc)) {
        if (!lastChangeWasUndoRedo)
            noteUpdated = listWithHistory.notifyChange(it)
        lastChangeWasUndoRedo = false
    }

    val undoAvailable = listWithHistory.undoAvailable
    val redoAvailable = listWithHistory.redoAvailable

    init {
        viewModelScope.launch {
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
        if (noteTitle.text.isBlank() && noteDesc.text.isBlank()) {
            notesDao.deleteNote(noteId)
        } else {
            val newNote = Note(
                id = noteId,
                title = noteTitle.text.trim(),
                description = noteDesc.text.trim(),
                colorArgb = noteColor.toArgb()
            )
            notesDao.insertNote(newNote)
        }
    }

    fun undo() {
        lastChangeWasUndoRedo = true
        noteDesc = listWithHistory.undo()
    }

    fun redo() {
        lastChangeWasUndoRedo = true
        noteDesc = listWithHistory.redo()
    }

}
