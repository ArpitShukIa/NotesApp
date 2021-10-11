package com.arpit.notes.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val colorArgb: Int
) : Parcelable {
    val color: Color
        get() = Color(colorArgb)
}
