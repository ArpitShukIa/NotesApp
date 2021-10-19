package com.arpit.notes.ui.home

import androidx.lifecycle.ViewModel
import com.arpit.notes.data.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    notesDao: NotesDao
) : ViewModel() {
    val notes = notesDao.observeAllNotes()
}