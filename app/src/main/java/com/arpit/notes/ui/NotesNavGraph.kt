package com.arpit.notes.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arpit.notes.ui.Destinations.ADD_NOTE_ROUTE
import com.arpit.notes.ui.Destinations.HOME_ROUTE
import com.arpit.notes.ui.Destinations.NOTE_ID_KEY
import com.arpit.notes.ui.addnote.AddNoteScreen
import com.arpit.notes.ui.home.HomeScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

object Destinations {
    const val HOME_ROUTE = "home"
    const val ADD_NOTE_ROUTE = "add_note"
    const val NOTE_ID_KEY = "note"
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun NotesNavGraph() {
    val systemUiController = rememberSystemUiController()
    val navController = rememberNavController()

    navController.addOnDestinationChangedListener { _, navDestination, _ ->
        val (statusBarBgAlpha, navBarBgAlpha) =
            when (navDestination.route) {
                HOME_ROUTE -> 0.8f to 0.9f
                ADD_NOTE_ROUTE -> 0f to 0.01f
                else -> 1f to 1f
            }

        systemUiController.setStatusBarColor(
            Color.White.copy(alpha = statusBarBgAlpha),
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(
            Color.White.copy(alpha = navBarBgAlpha),
            darkIcons = true
        )
    }

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            HomeScreen(hiltViewModel()) { noteId ->
                val routeArg = if (noteId == null) "" else "?$NOTE_ID_KEY=$noteId"
                navController.navigate(ADD_NOTE_ROUTE + routeArg)
            }
        }
        composable(
            route = "$ADD_NOTE_ROUTE?$NOTE_ID_KEY={$NOTE_ID_KEY}",
            arguments = listOf(
                navArgument(NOTE_ID_KEY) {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                },
            )
        ) {
            AddNoteScreen(hiltViewModel(), navController::navigateUp)
        }
    }
}