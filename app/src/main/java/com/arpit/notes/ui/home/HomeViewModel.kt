package com.arpit.notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.notes.data.NoteItem
import com.arpit.notes.data.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    notesDao: NotesDao
) : ViewModel() {

    private val _selectedNotesCount = MutableStateFlow(0)
    val selectedNotesCount: StateFlow<Int> = _selectedNotesCount

    private val _notes = MutableStateFlow(emptyList<NoteItem>())
    val notes: StateFlow<List<NoteItem>> = _notes

    init {
        viewModelScope.launch {
            notesDao.observeAllNotes().collect { notes ->
                _notes.value = notes
                    .sortedByDescending { it.lastUpdatedAt }
                    .map { NoteItem(it, false) }
            }
        }
    }

    fun onNoteClick(note: NoteItem) {
        if (selectedNotesCount.value > 0) {
            toggleNoteSelection(note)
        } else {
            // Open note
        }
    }

    fun onNoteLongClick(note: NoteItem) {
        toggleNoteSelection(note)
    }

    private fun toggleNoteSelection(note: NoteItem) {
        _selectedNotesCount.value += if (note.selected) -1 else 1
        _notes.value = notes.value.map {
            if (it == note) it.copy(selected = !it.selected)
            else it
        }
    }
}