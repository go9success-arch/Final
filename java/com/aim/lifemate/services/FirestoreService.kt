package com.aim.lifemate.services

import com.aim.lifemate.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Extension functions for converting data classes to maps
fun Transaction.toMap(): Map<String, Any> {
    return mapOf(
        "amount" to amount,
        "type" to type,
        "description" to description,
        "status" to status,
        "createdAt" to (createdAt ?: System.currentTimeMillis())
    )
}

fun GameScore.toMap(): Map<String, Any> {
    return mapOf(
        "gameId" to gameId,
        "gameName" to gameName,
        "score" to score,
        "coinsEarned" to coinsEarned,
        "level" to level,
        "duration" to duration
    )
}

fun Payout.toMap(): Map<String, Any> {
    return mapOf(
        "userId" to userId,
        "amount" to amount,
        "method" to method,
        "upiId" to upiId,
        "status" to status,
        "createdAt" to (createdAt ?: System.currentTimeMillis())
    )
}

@Singleton
class FirestoreService @Inject constructor() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun listenToUserProfile(userId: String, onUpdate: (UserProfile) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                snapshot?.toObject(UserProfile::class.java)?.let { userProfile ->
                    onUpdate(userProfile)
                }
            }
    }

    fun listenToWalletBalance(userId: String, onUpdate: (Double) -> Unit): ListenerRegistration {
        return db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                val balance = snapshot?.getDouble("wallet_balance") ?: 0.0
                onUpdate(balance)
            }
    }

    suspend fun getWellnessRemedies(): List<WellnessRemedy> {
        return try {
            val snapshot = db.collection("wellness_remedies")
                .whereEqualTo("isVerified", true)
                .limit(20)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(WellnessRemedy::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchWellnessRemedies(query: String, language: String = "en"): List<WellnessRemedy> {
        return try {
            val snapshot = db.collection("wellness_remedies")
                .whereEqualTo("isVerified", true)
                .get()
                .await()

            val allRemedies = snapshot.documents.mapNotNull {
                it.toObject(WellnessRemedy::class.java)
            }

            allRemedies.filter { remedy ->
                remedy.name.contains(query, true) ||
                        remedy.wellnessFocus.contains(query, true) ||
                        remedy.description.contains(query, true) ||
                        remedy.ingredients.any { it.contains(query, true) } ||
                        remedy.getName(language).contains(query, true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getGovernmentJobs(): List<GovernmentJob> {
        return try {
            val snapshot = db.collection("government_jobs")
                .whereEqualTo("isActive", true)
                .limit(50)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(GovernmentJob::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPrivateJobs(): List<PrivateJob> {
        return try {
            val snapshot = db.collection("private_jobs")
                .whereEqualTo("isActive", true)
                .limit(50)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(PrivateJob::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCurrentTournament(): Tournament? {
        return try {
            val snapshot = db.collection("tournaments")
                .whereEqualTo("isActive", true)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { document ->
                document.toObject(Tournament::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addCoins(amount: Int, reason: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            // Create transaction
            val transaction = Transaction(
                amount = amount.toDouble(),
                type = "reward",
                description = reason,
                status = "completed"
            )

            // Add transaction record
            db.collection("users").document(userId)
                .collection("transactions")
                .add(transaction.toMap())
                .await()

            // Update wallet balance (only positive amounts allowed)
            db.collection("users").document(userId)
                .update("wallet_balance", FieldValue.increment(amount.toDouble()))
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addGameScore(gameScore: GameScore): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            // Add game score record
            db.collection("game_scores")
                .add(gameScore.toMap().toMutableMap().apply {
                    put("userId", userId)
                    put("timestamp", System.currentTimeMillis())
                })
                .await()

            // Update user stats and add coins (only positive amounts)
            val updates = mapOf(
                "gameHighScore" to FieldValue.increment(gameScore.score.toLong()),
                "totalGamesPlayed" to FieldValue.increment(1),
                "wallet_balance" to FieldValue.increment(gameScore.coinsEarned.toDouble())
            )

            db.collection("users").document(userId)
                .update(updates)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun participateInTournament(tournamentId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            // Free tournament participation - no entry fee, just add user to participants
            db.runTransaction { transaction ->
                // Add to tournament participants
                transaction.update(
                    db.collection("tournaments").document(tournamentId),
                    "participants",
                    FieldValue.increment(1)
                )

                // Create participation record
                val participationData = mapOf(
                    "userId" to userId,
                    "tournamentId" to tournamentId,
                    "joinedAt" to System.currentTimeMillis()
                )

                transaction.set(
                    db.collection("tournament_participations").document(),
                    participationData
                )

                true
            }.await()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun requestWithdrawal(amount: Double, upiId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false

            db.runTransaction { transaction ->
                // Check user balance and minimum withdrawal amount
                val userDoc = transaction.get(db.collection("users").document(userId))
                val currentBalance = userDoc.getDouble("wallet_balance") ?: 0.0

                if (currentBalance >= amount && amount >= 100) {
                    // Deduct amount for withdrawal
                    transaction.update(
                        db.collection("users").document(userId),
                        "wallet_balance",
                        FieldValue.increment(-amount)
                    )

                    // Create withdrawal request (no fees)
                    val payout = Payout(
                        userId = userId,
                        amount = amount,
                        method = "UPI",
                        upiId = upiId,
                        status = "pending"
                    )

                    transaction.set(
                        db.collection("withdrawal_requests").document(),
                        payout.toMap()
                    )

                    true
                } else {
                    false
                }
            }.await()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserTransactions(userId: String): List<Transaction> {
        return try {
            val snapshot = db.collection("users").document(userId)
                .collection("transactions")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(Transaction::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Additional helper method to ensure wallet balance never goes negative
    suspend fun ensureNonNegativeBalance(userId: String): Boolean {
        return try {
            db.runTransaction { transaction ->
                val userDoc = transaction.get(db.collection("users").document(userId))
                val currentBalance = userDoc.getDouble("wallet_balance") ?: 0.0

                if (currentBalance < 0) {
                    transaction.update(
                        db.collection("users").document(userId),
                        "wallet_balance",
                        0.0
                    )
                }
                true
            }.await()
        } catch (e: Exception) {
            false
        }
    }
}