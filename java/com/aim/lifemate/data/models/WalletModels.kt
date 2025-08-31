// WalletModels.kt
package com.aim.lifemate.data.models

data class Wallet(
    val id: String = "",
    val userId: String = "",
    val balance: Double = 0.0,
    val currency: String = "USD",
    val lastUpdated: Long = System.currentTimeMillis()
)

data class WithdrawalRequest(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val bankName: String = "",
    val accountNumber: String = "",
    val accountHolderName: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, COMPLETED
    val createdAt: Long = System.currentTimeMillis(),
    val processedAt: Long = 0
)

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // WITHDRAWAL
    val amount: Double = 0.0,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "", // PENDING, COMPLETED, FAILED
    val reference: String = "",
    val bankName: String = "" // Add this for better display

)