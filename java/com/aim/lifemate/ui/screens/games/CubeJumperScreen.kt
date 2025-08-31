package com.aim.lifemate.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CubeJumperScreen(navController: NavHostController) {
    val viewModel: GameViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isGameRunning) {
        if (state.isGameRunning) {
            while (true) {
                delay(16) // 60 FPS
                if (state.isGameRunning) {
                    viewModel.updateGameState()
                } else {
                    break
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cube Jumper") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Game Canvas
            GameCanvas(state, viewModel::jump)

            // Game UI Overlay
            GameUI(state, viewModel::startGame, viewModel::endGame)

            // Score Display
            ScoreDisplay(state.currentScore, state.highScore)
        }
    }
}

@Composable
fun GameCanvas(state: GameState, onJump: () -> Unit) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clickable { if (state.isGameRunning) onJump() }
    ) {
        // Draw background
        drawRect(Color(0xFF1A1A1A))

        // Draw player (cube)
        val playerY = if (state.isGameRunning) state.playerY else size.height * 0.7f
        drawRect(
            color = Color(0xFF00DDFF),
            topLeft = Offset(size.width * 0.2f, playerY),
            size = androidx.compose.ui.geometry.Size(50f, 50f)
        )

        // Draw obstacles
        state.obstacles.forEach { obstacle ->
            drawRect(
                color = Color(0xFFFF0080),
                topLeft = Offset(obstacle.x, obstacle.y),
                size = androidx.compose.ui.geometry.Size(obstacle.width, obstacle.height)
            )
        }

        // Draw ground
        drawRect(
            color = Color(0xFF39FF14),
            topLeft = Offset(0f, size.height * 0.8f),
            size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.2f)
        )
    }
}

@Composable
fun GameUI(state: GameState, onStart: () -> Unit, onEnd: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!state.isGameRunning) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.currentScore > 0) {
                    Text(
                        "Game Over!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        "Score: ${state.currentScore}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        "High Score: ${state.highScore}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        "Coins Earned: +${state.currentScore / 10}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Yellow
                    )
                } else {
                    Text(
                        "Cube Jumper",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        "Tap to jump over obstacles!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        if (state.isGameRunning) onEnd() else onStart()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00DDFF),
                        contentColor = Color.Black
                    )
                ) {
                    Text(if (state.isGameRunning) "End Game" else "Start Game")
                }
            }
        }
    }
}

@Composable
fun ScoreDisplay(currentScore: Int, highScore: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "Score: $currentScore",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                "High: $highScore",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}