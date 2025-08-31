package com.aim.lifemate.data.models

data class TournamentParticipation(
    val id: String = "",
    val userId: String = "",
    val tournamentId: String = "",
    val joinedAt: Long = System.currentTimeMillis(),
    val userScore: Int = 0,
    val userRank: Int = 0,
    val coinsWon: Double = 0.0
)