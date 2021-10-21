package com.arpit.notes.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.thenIf(
    condition: Boolean,
    operation: @Composable Modifier.() -> Modifier
): Modifier = composed {
    if (condition) operation() else this
}