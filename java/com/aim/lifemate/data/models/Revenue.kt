package com.aim.lifemate.data.models

data class AdRevenue(
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val totalRevenue: Double = 0.0,
    val userShare: Double = 0.0,
    val platformShare: Double = 0.0,
    val adsWatched: Int = 0,
    val interstitialCount: Int = 0,
    val rewardedCount: Int = 0,
    val bannerImpressions: Int = 0,
    val estimatedEarnings: Double = 0.0,
    val calculatedAt: Long = System.currentTimeMillis(),
    val paidOut: Boolean = false,
    val payoutDate: Long? = null
)



data class UsageStats(
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val totalTime: Long = 0,
    val jobSearchTime: Long = 0,
    val remedySearchTime: Long = 0,
    val gamePlayTime: Long = 0,
    val aiChatTime: Long = 0,
    val screensViewed: Int = 0,
    val adsClicked: Int = 0,
    val sessions: Int = 0,
    val lastActive: Long = System.currentTimeMillis(),
    val devices: List<String> = emptyList()
)