package com.aim.lifemate.data.models

data class LeaderboardEntry(
    val userId: String = "",
    val username: String = "",
    val score: Int = 0,
    val rank: Int = 0,
    val avatar: String = "",
    val country: String = "IN",
    val lastUpdated: Long = System.currentTimeMillis()
)