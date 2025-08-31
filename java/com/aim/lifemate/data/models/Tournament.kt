package com.aim.lifemate.data.models

import com.google.firebase.firestore.Exclude

data class Tournament(
    val id: String = "",
    val name: String = "",
    val type: String = "", // "weekly", "monthly", "special"
    val title: String ="",
    val entryFee: Int = 0,
    val prizePool: Int = 0,
    val participants: Int = 0,
    val maxParticipants: Int = 1000,
    val startTime: Long = 0,
    val endTime: Long = 0,
    val isActive: Boolean = true,
    val winners: Map<String, Int> = emptyMap(),
    val rules: List<String> = emptyList(),
    val minScore: Int = 0,
    val adRevenueShare: Double = 0.1,
    val totalAdRevenue: Double = 0.0,
) {
    fun getPrizeForPosition(position: Int): Int {
        return when (position) {
            1 -> (prizePool * 0.5).toInt()
            2 -> (prizePool * 0.3).toInt()
            3 -> (prizePool * 0.2).toInt()
            else -> 0
        }
    }
}

