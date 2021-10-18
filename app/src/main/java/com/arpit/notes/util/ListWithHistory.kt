package com.arpit.notes.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListWithHistory(initialTextFieldValue: TextFieldValue) {

    /**
     * Represents a state similar to [TextFieldValue]
     *
     * @param text text stored in [TextFieldValue]
     * @param cursorPos end index of [TextFieldValue.selection]
     * @param onlyCursorChange whether this and the previous state differ only in [TextFieldValue.selection]
     */
    private data class State(
        val text: String,
        val cursorPos: Int,
        val onlyCursorChange: Boolean = false
    )

    private val undoList = mutableListOf<State>()
    private val redoList = mutableListOf<State>()
    private var currentState = initialTextFieldValue.toState()

    private val _undoAvailable = MutableStateFlow(false)
    val undoAvailable: StateFlow<Boolean> = _undoAvailable

    private val _redoAvailable = MutableStateFlow(false)
    val redoAvailable: StateFlow<Boolean> = _redoAvailable

    fun notifyChange(newValue: TextFieldValue): Boolean {
        val hasTextChanged = currentState.text != newValue.text
        if (hasTextChanged) {
            redoList.clear()
            _redoAvailable.value = false
        }
        undoList.add(currentState)
        currentState = newValue.toState().copy(onlyCursorChange = !hasTextChanged)
        _undoAvailable.value = undoList.any { it.text != currentState.text }
        return hasTextChanged
    }

    fun undo(): TextFieldValue {
        check(undoAvailable.value) { "Undo not available" }
        if (currentState.onlyCursorChange) {
            while (undoList.last().onlyCursorChange)
                undoList.removeLast()
            redoList.add(undoList.removeLast())
        } else {
            redoList.add(currentState)
        }
        currentState = undoList.removeLast()
        _undoAvailable.value = undoList.any { it.text != currentState.text }
        _redoAvailable.value = true
        return currentState.toTextFieldValue()
    }

    fun redo(): TextFieldValue {
        check(redoAvailable.value) { "Redo not available" }
        undoList.add(currentState)
        currentState = redoList.removeLast()
        _undoAvailable.value = true
        _redoAvailable.value = redoList.isNotEmpty()
        return currentState.toTextFieldValue()
    }

    private fun TextFieldValue.toState() = State(text, selection.max, false)

    private fun State.toTextFieldValue() = TextFieldValue(text, TextRange(cursorPos))
}
