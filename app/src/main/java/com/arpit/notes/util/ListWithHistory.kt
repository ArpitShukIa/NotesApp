package com.arpit.notes.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListWithHistory(initialTextFieldValue: TextFieldValue) {

    private val undoList = mutableListOf(initialTextFieldValue)
    private val redoList = mutableListOf<TextFieldValue>()
    private var lastTextFieldValue = initialTextFieldValue
    private var lastCursorPosWhenTextChanged = 0
    private var lastStateWhenTextChanged = initialTextFieldValue

    private val _undoAvailable = MutableStateFlow(false)
    val undoAvailable: StateFlow<Boolean> = _undoAvailable

    private val _redoAvailable = MutableStateFlow(false)
    val redoAvailable: StateFlow<Boolean> = _redoAvailable

    /**
     * Updates [lastTextFieldValue] and adds it to the [undoList] if text has changed
     * @return `true` if some text has changed, `false` if only selection changed
     */
    fun notifyChange(value: TextFieldValue): Boolean {
        val textChanged = value.text != lastStateWhenTextChanged.text
        if (textChanged) {
            redoList.clear()
            undoList.add(lastTextFieldValue)
            _undoAvailable.value = true
            _redoAvailable.value = false
            lastCursorPosWhenTextChanged = value.selection.min
            lastStateWhenTextChanged = value
        }
        lastTextFieldValue = value
        return textChanged
    }

    fun undo(): TextFieldValue {
        check(undoAvailable.value) { "Undo not available" }
        redoList.add(lastStateWhenTextChanged)
        lastStateWhenTextChanged = undoList.removeLast().selectionCleared()
        _undoAvailable.value = undoList.isNotEmpty()
        _redoAvailable.value = true
        return lastStateWhenTextChanged
    }

    fun redo(): TextFieldValue {
        check(redoAvailable.value) { "Redo not available" }
        undoList.add(lastStateWhenTextChanged)
        lastStateWhenTextChanged = redoList.removeLast().selectionCleared()
        _redoAvailable.value = redoList.isNotEmpty()
        _undoAvailable.value = true
        return lastStateWhenTextChanged
    }

    private fun TextFieldValue.selectionCleared(): TextFieldValue {
        return TextFieldValue(text, TextRange(selection.max), composition)
    }

}
