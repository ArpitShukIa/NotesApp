package com.arpit.notes.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import com.arpit.notes.ui.theme.NotesTheme
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@AndroidEntryPoint
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
