package com.arpit.notes.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    changeStatusBarColor: (Color) -> Unit,
    navigateToAddNoteScreen: () -> Unit
) {
    LaunchedEffect(true) {
        changeStatusBarColor(Color.White)
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddNoteScreen
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add note"
                )
            }
        }
    ) {
        Text(
            text = "Notes",
            style = TextStyle(fontSize = 24.sp),
            modifier = Modifier.padding(16.dp)
        )
    }
}