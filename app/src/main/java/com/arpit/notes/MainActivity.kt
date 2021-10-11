package com.arpit.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.view.WindowCompat
import com.arpit.notes.ui.NotesNavGraph
import com.arpit.notes.ui.theme.NotesTheme
import com.google.accompanist.insets.ProvideWindowInsets

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideWindowInsets {
                NotesTheme {
                    NotesNavGraph()
                }
            }
        }
    }
}
