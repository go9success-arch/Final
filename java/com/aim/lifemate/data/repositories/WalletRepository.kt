// WalletRepository.kt
package com.aim.lifemate.data.repositories

import com.aim.lifemate.data.models.Wallet
import com.aim.lifemate.data.models.WithdrawalRequest
import com.aim.lifemate.data.models.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val WALLETS_COLLECTION = "wallets"
        private const val WITHDRAWAL_REQUESTS_COLLECTION = "withdrawal_requests"
        private const val TRANSACTIONS_COLLECTION = "transactions"
    }

    suspend fun getWallet(userId: String): Wallet {
        return try {
            val document = firestore.collection(WALLETS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                document.toObject(Wallet::class.java) ?: createNewWallet(userId)
            } else {
                createNewWallet(userId)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch wallet: ${e.message}")
        }
    }

    fun observeWallet(userId: String): Flow<Wallet> = callbackFlow {
        val listener = firestore.collection(WALLETS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let { document ->
                    if (document.exists()) {
                        val wallet = document.toObject(Wallet::class.java)
                        wallet?.let { trySend(it) }
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun requestWithdrawal(
        userId: String,
        amount: Double,
        bankName: String,
        accountNumber: String,
        accountHolderName: String
    ): String {
        return try {
            // First verify wallet balance
            val wallet = getWallet(userId)

            if (wallet.balance < amount) {
                throw Exception("Insufficient balance. Available: $${wallet.balance}")
            }

            if (amount <= 0) {
                throw Exception("Withdrawal amount must be greater than zero")
            }

            // Create withdrawal request
            val withdrawalRequest = WithdrawalRequest(
                userId = userId,
                amount = amount,
                bankName = bankName,
                accountNumber = accountNumber,
                accountHolderName = accountHolderName,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )

            val documentRef = firestore.collection(WITHDRAWAL_REQUESTS_COLLECTION)
                .add(withdrawalRequest)
                .await()

            // Create transaction record
            val transaction = Transaction(
                userId = userId,
                type = "WITHDRAWAL_REQUEST",
                amount = amount,
                description = "Withdrawal request to $bankName",
                timestamp = System.currentTimeMillis(),
                status = "PENDING",
                reference = documentRef.id
                        bankName = bankName // Add this

            )

            firestore.collection(TRANSACTIONS_COLLECTION)
                .add(transaction)
                .await()

            documentRef.id
        } catch (e: Exception) {
            throw Exception("Withdrawal request failed: ${e.message}")
        }
    }

    suspend fun getWithdrawalRequests(userId: String): List<WithdrawalRequest> {
        return try {
            val snapshot = firestore.collection(WITHDRAWAL_REQUESTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(WithdrawalRequest::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch withdrawal requests: ${e.message}")
        }
    }

    suspend fun getTransactions(userId: String): List<Transaction> {
        return try {
            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(Transaction::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch transactions: ${e.message}")
        }
    }

    private suspend fun createNewWallet(userId: String): Wallet {
        val newWallet = Wallet(
            id = userId,
            userId = userId,
            balance = 0.0
        )

        firestore.collection(WALLETS_COLLECTION)
            .document(userId)
            .set(newWallet)
            .await()

        return newWallet
    }
}