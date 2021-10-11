package com.arpit.notes.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arpit.notes.ui.addnote.AddNoteScreen
import com.arpit.notes.ui.home.HomeScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

object Destinations {
    const val HOME_ROUTE = "home"
    const val ADD_NOTE_ROUTE = "add_note"
}

@ExperimentalAnimationApi
@Composable
fun NotesNavGraph() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(Color.Transparent)

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.HOME_ROUTE
    ) {
        composable(Destinations.HOME_ROUTE) {
            HomeScreen {
                navController.navigate(Destinations.ADD_NOTE_ROUTE)
            }
        }
        composable(Destinations.ADD_NOTE_ROUTE) {
            AddNoteScreen(navController::navigateUp)
        }
    }
}