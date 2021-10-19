package com.arpit.notes.util

import androidx.compose.ui.Modifier

fun Modifier.thenIf(condition: Boolean, operation: Modifier.() -> Modifier): Modifier {
    return if (condition) operation() else this
}