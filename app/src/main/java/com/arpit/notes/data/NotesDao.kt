package com.arpit.notes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM notes_table WHERE id = :noteId")
    suspend fun getNote(noteId: String): Note

    @Query("DELETE FROM notes_table WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)

    @Query("SELECT * FROM notes_table")
    fun observeAllNotes(): Flow<List<Note>>

}