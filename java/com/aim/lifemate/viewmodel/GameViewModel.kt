package com.aim.lifemate.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aim.lifemate.data.models.GameScore
import com.aim.lifemate.services.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {

    var state by mutableStateOf(GameState())
        private set

    private var gameSpeed = 5f
    private var gravity = 0.8f
    private var jumpVelocity = -15f
    private var playerVelocity = 0f

    fun startGame() {
        state = state.copy(
            isGameRunning = true,
            currentScore = 0,
            playerY = 500f,
            obstacles = emptyList(),
            gameTime = 0
        )
        playerVelocity = 0f
        gameSpeed = 5f

        viewModelScope.launch {
            while (state.isGameRunning) {
                delay(1000)
                state = state.copy(gameTime = state.gameTime + 1)
                gameSpeed += 0.2f // Increase difficulty over time
            }
        }

        generateObstacles()
    }

    fun endGame() {
        state = state.copy(isGameRunning = false)
        if (state.currentScore > 0) {
            saveGameScore()
        }
    }

    fun jump() {
        if (state.isGameRunning && state.playerY >= 500f) {
            playerVelocity = jumpVelocity
        }
    }

    fun updateGameState() {
        if (!state.isGameRunning) return

        // Update player position
        playerVelocity += gravity
        var newPlayerY = state.playerY + playerVelocity
        if (newPlayerY > 500f) {
            newPlayerY = 500f
            playerVelocity = 0f
        }

        // Update obstacles
        val updatedObstacles = state.obstacles.map { obstacle ->
            obstacle.copy(x = obstacle.x - gameSpeed)
        }.filter { it.x + it.width > 0 }

        // Check collisions
        val collision = updatedObstacles.any { obstacle ->
            val playerRight = 200f + 50f
            val playerBottom = newPlayerY + 50f
            val obstacleRight = obstacle.x + obstacle.width
            val obstacleBottom = obstacle.y + obstacle.height

            playerRight > obstacle.x && 200f < obstacleRight &&
                    playerBottom > obstacle.y && newPlayerY < obstacleBottom
        }

        if (collision) {
            endGame()
            return
        }

        // Update score
        val newScore = state.currentScore + (gameSpeed * 0.1f).toInt()

        state = state.copy(
            playerY = newPlayerY,
            obstacles = updatedObstacles,
            currentScore = newScore
        )
    }

    private fun generateObstacles() {
        viewModelScope.launch {
            while (state.isGameRunning) {
                delay(Random.nextLong(1500, 3000)) // Random interval between obstacles

                if (state.isGameRunning) {
                    val height = Random.nextFloat() * 100f + 50f
                    val obstacle = Obstacle(
                        x = 1000f,
                        y = 500f - height,
                        width = 30f,
                        height = height
                    )
                    state = state.copy(obstacles = state.obstacles + obstacle)
                }
            }
        }
    }

    private fun saveGameScore() {
        viewModelScope.launch {
            val coinsEarned = state.currentScore / 10
            val gameScore = GameScore(
                score = state.currentScore,
                coinsEarned = coinsEarned,
                duration = state.gameTime
            )

            try {
                firestoreService.addGameScore(gameScore)
                if (state.currentScore > state.highScore) {
                    state = state.copy(highScore = state.currentScore)
                }
                state = state.copy(totalCoinsEarned = state.totalCoinsEarned + coinsEarned)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class GameState(
    val isGameRunning: Boolean = false,
    val currentScore: Int = 0,
    val highScore: Int = 0,
    val totalCoinsEarned: Int = 0,
    val gamesPlayed: Int = 0,
    val playerY: Float = 500f,
    val obstacles: List<Obstacle> = emptyList(),
    val gameTime: Int = 0,
    val error: String? = null
)

data class Obstacle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)