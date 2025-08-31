package com.aim.lifemate.data.models


data class GameScore(
    val gameId: String = "",
    val userId: String = "",
    val gameName: String = "Cube Jumper",
    val score: Int = 0,
    val coinsEarned: Int = 0,
    val duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isHighScore: Boolean = false,
    val deviceId: String = "",
    val level: Int = 1,
    val version: String = "1.0",
    val platform: String = "Android"
)

{
    companion object {
        const val COINS_PER_SCORE = 0.1
        const val BASE_COINS = 5
    }

    fun calculateCoins(): Int {
        return BASE_COINS + (score * COINS_PER_SCORE).toInt()
    }
}
