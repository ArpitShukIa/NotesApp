package com.arpit.notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.notes.data.NoteItem
import com.arpit.notes.data.NotesDao
 import com.arpit.notes.ui.home.HomeViewModel.HomeScreenEvents.OpenNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    notesDao: NotesDao
) : ViewModel() {

    private val homeScreenEventsChannel = Channel<HomeScreenEvents>()
    val homeScreenEvents = homeScreenEventsChannel.receiveAsFlow()

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

    fun onNoteClick(noteItem: NoteItem) {
        if (selectedNotesCount.value > 0) {
            toggleNoteSelection(noteItem)
        } else {
            viewModelScope.launch {
                homeScreenEventsChannel.send(OpenNote(noteItem.note.id))
            }
        }
    }

    fun onNoteLongClick(noteItem: NoteItem) {
        toggleNoteSelection(noteItem)
    }

    private fun toggleNoteSelection(noteItem: NoteItem) {
        _selectedNotesCount.value += if (noteItem.selected) -1 else 1
        _notes.value = notes.value.map {
            if (it == noteItem) it.copy(selected = !it.selected)
            else it
        }
    }

    sealed class HomeScreenEvents {
        data class OpenNote(val noteId: String) : HomeScreenEvents()
    }
}