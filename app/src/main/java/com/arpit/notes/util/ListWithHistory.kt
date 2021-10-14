package com.arpit.notes.util

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class ListWithHistory(initialTextFieldValue: TextFieldValue) {

    /**
     * Represents a change in [TextFieldValue]
     * @param text text that was replaced
     * @param selection range denoting the indices of replacement text
     */
    private data class Change(
        val text: String,
        val selection: IntRange
    )

    private var undoList = ArrayDeque<Change>()
    private val redoList = ArrayDeque<Change>()
    private var lastTextFieldValue = initialTextFieldValue

    private val _undoAvailable = MutableStateFlow(false)
    val undoAvailable: StateFlow<Boolean> = _undoAvailable

    private val _redoAvailable = MutableStateFlow(false)
    val redoAvailable: StateFlow<Boolean> = _redoAvailable

    /**
     * Updates [lastTextFieldValue] and adds it to the [undoList] if text has changed
     * @return `true` if some text has changed, `false` if only selection changed
     */
    fun notifyChange(value: TextFieldValue): Boolean {
        Log.d("TAG", value.toString())
        val hasTextChanged = value.text != lastTextFieldValue.text
        if (hasTextChanged) {
            redoList.clear()
            with(lastTextFieldValue) {
                val currentCursorPos = value.selection.min
                val oldCursorPos = selection.min
                val originalText = when {
                    !selection.collapsed -> getSelectedText().text
                    oldCursorPos == currentCursorPos + 1 -> text[currentCursorPos].toString()
                    else -> ""
                }
                val start = if (currentCursorPos < oldCursorPos) currentCursorPos else oldCursorPos
                val change = Change(
                    text = originalText,
                    selection = start until currentCursorPos
                )
                updateUndoList(change, value.text)
            }
            _undoAvailable.value = true
            _redoAvailable.value = false
        }
        lastTextFieldValue = value
        return hasTextChanged
    }

    private fun updateUndoList(change: Change, currentString: String) {
        val lastChange = undoList.peek()
        if (lastChange == null) {
            undoList.push(change)
            return
        }
        val a = lastChange.selection.first
        val b = lastChange.selection.last
        val c = change.selection.first
        val d = change.selection.last
        if ((change.text + lastChange.text).isEmpty() && b == c - 1 && c == d && currentString[c] != ' ') {
            undoList.pop()
            undoList.push(Change("", a..d))
        } else if (a == b + 1 && b == c && c == d + 1 && change.text != " ") {
            undoList.pop()
            undoList.push(Change(change.text + lastChange.text, change.selection))
        } else {
            undoList.push(change)
        }
    }

    fun undo(): TextFieldValue {
        check(undoAvailable.value) { "Undo not available" }
        undoRedo(undo = true)
        _undoAvailable.value = undoList.isNotEmpty()
        _redoAvailable.value = true
        return lastTextFieldValue
    }

    fun redo(): TextFieldValue {
        check(redoAvailable.value) { "Redo not available" }
        undoRedo(undo = false)
        _redoAvailable.value = redoList.isNotEmpty()
        _undoAvailable.value = true
        return lastTextFieldValue
    }

    private fun undoRedo(undo: Boolean) {
        val change = if (undo) undoList.pop() else redoList.pop()
        with(change) {
            val newChange = Change(
                text = lastTextFieldValue.text.substring(selection),
                selection = selection.first until (selection.first + text.length)
            )
            (if (undo) redoList else undoList).push(newChange)
            lastTextFieldValue = TextFieldValue(
                text = lastTextFieldValue.text.replaceRange(selection, text),
                selection = TextRange(selection.first + text.length)
            )
        }
    }

}
