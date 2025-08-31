package com.aim.lifemate.data.models

import com.google.firebase.firestore.PropertyName
import java.util.*

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val skills: List<String> = emptyList(),
    val experience: String = "",
    val education: String = "",
    val resumeUrl: String = "",
    val user: String="",
    @PropertyName("wallet_balance") var walletBalance: Double = 0.0,
    val totalEarnings: Double = 0.0,
    val totalWithdrawals: Double = 0.0,
    val wellnessGoals: List<String> = emptyList(),
    val completedPractices: List<String> = emptyList(),
    val favoriteJobs: List<String> = emptyList(),
    val gameHighScore: Int = 0,
    val tournamentWins: Int = 0,
    val totalGamesPlayed: Int = 0,
    val referralCode: String = "",
    val referredBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val avatarUrl: String = "",
    val upiId: String = "",
    val bankAccount: String = "",
    val ifscCode: String = "",
    val isPremium: Boolean = false,
    val languagePreference: String = "en",
    val notificationEnabled: Boolean = true,
    val adRevenueShare: Double = 0.01 // 1% share
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "phone" to phone,
            "wallet_balance" to walletBalance,
            "totalEarnings" to totalEarnings,
            "totalWithdrawals" to totalWithdrawals,
            "wellnessGoals" to wellnessGoals,
            "completedPractices" to completedPractices,
            "favoriteJobs" to favoriteJobs,
            "gameHighScore" to gameHighScore,
            "tournamentWins" to tournamentWins,
            "totalGamesPlayed" to totalGamesPlayed,
            "referralCode" to referralCode,
            "referredBy" to referredBy,
            "createdAt" to createdAt,
            "lastLogin" to lastLogin,
            "avatarUrl" to avatarUrl,
            "upiId" to upiId,
            "bankAccount" to bankAccount,
            "ifscCode" to ifscCode,
            "isPremium" to isPremium,
            "adRevenueShare" to adRevenueShare
        )
    }
}

