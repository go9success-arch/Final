package com.aim.lifemate.ui.screens.games

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tournaments") }
            )
        }
    ) { paddingValues ->
        // Tournament implementation
        Text(
            text = "Tournaments",
            modifier = Modifier.padding(paddingValues)
        )
    }
}