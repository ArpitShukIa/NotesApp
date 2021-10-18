package com.arpit.notes.util

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.repeatingClickable(
    enabled: Boolean,
    onClick: () -> Unit,
    initialDelayMillis: Long,
    delayMillis: Long,
    interactionSource: MutableInteractionSource,
    rippleRadius: Dp = Dp.Unspecified
): Modifier = composed {

    val currentClickListener by rememberUpdatedState(onClick)
    var downOffset by remember { mutableStateOf<Offset?>(null) }

    pointerInput(interactionSource, enabled) {
        if (!enabled) {
            // Hide ripple
            downOffset?.let { offset ->
                interactionSource.emit(PressInteraction.Release(PressInteraction.Press(offset)))
            }
            return@pointerInput
        }
        forEachGesture {
            coroutineScope {
                awaitPointerEventScope {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    downOffset = down.position
                    // Create a down press interaction
                    val downPress = PressInteraction.Press(down.position)
                    val heldButtonJob = launch {
                        // Send the press through the interaction source
                        interactionSource.emit(downPress)
                        currentClickListener()
                        delay(initialDelayMillis)
                        while (true) {
                            currentClickListener()
                            delay(delayMillis)
                        }
                    }
                    val up = waitForUpOrCancellation()
                    heldButtonJob.cancel()
                    // Determine whether a cancel or release occurred, and create the interaction
                    val releaseOrCancel = when (up) {
                        null -> PressInteraction.Cancel(downPress)
                        else -> PressInteraction.Release(downPress)
                    }
                    launch {
                        // Send the result through the interaction source
                        interactionSource.emit(releaseOrCancel)
                    }
                }
            }
        }
    }.indication(interactionSource, rememberRipple(bounded = false, radius = rippleRadius))
}