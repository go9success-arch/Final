// User.kt
package com.aim.lifemate.data.models

data class UserWal(
    val uid: String = "", // Changed from 'id' to 'uid' to match Firebase
    val name: String = "",
    val email: String = "",
    val walletId: String = "", // Reference to wallet document
    val languagePreference: String = "en",
    val notificationEnabled: Boolean = true
)