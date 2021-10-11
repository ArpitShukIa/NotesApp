package com.arpit.notes.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

private class ObservableMutableState<T>(
    initialValue: T,
    private val onValueChange: (T) -> Unit
) : MutableState<T> {

    private val state = mutableStateOf(initialValue)

    override var value: T
        get() = state.value
        set(value) {
            state.value = value
            onValueChange(value)
        }

    override operator fun component1(): T = value

    override operator fun component2(): (T) -> Unit = { value = it }

}

fun <T> observableStateOf(value: T, onValueChange: (T) -> Unit = {}): MutableState<T> {
    return ObservableMutableState(value, onValueChange)
}
