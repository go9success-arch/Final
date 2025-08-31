// WalletViewModel.kt
package com.aim.lifemate.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aim.lifemate.data.models.Wallet
import com.aim.lifemate.data.models.WithdrawalRequest
import com.aim.lifemate.data.models.Transaction
import com.aim.lifemate.data.repositories.WalletRepository
import com.aim.lifemate.data.repositories.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _walletState = MutableStateFlow(WalletState())
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _withdrawalRequests = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val withdrawalRequests: StateFlow<List<WithdrawalRequest>> = _withdrawalRequests.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            val currentUser: FirebaseUser? = authRepository.getCurrentUser()
            currentUser?.let { user ->
                loadInitialData(user.uid)
                observeWallet(user.uid)
            } ?: run {
                _walletState.value = _walletState.value.copy(
                    isLoading = false,
                    error = "User not authenticated"
                )
            }
        }
    }

    private suspend fun loadInitialData(userId: String) {
        try {
            _walletState.value = _walletState.value.copy(isLoading = true)

            val wallet = walletRepository.getWallet(userId)
            val requests = walletRepository.getWithdrawalRequests(userId)
            val userTransactions = walletRepository.getTransactions(userId)

            _walletState.value = _walletState.value.copy(
                wallet = wallet,
                isLoading = false,
                error = null
            )
            _withdrawalRequests.value = requests
            _transactions.value = userTransactions
        } catch (e: Exception) {
            _walletState.value = _walletState.value.copy(
                isLoading = false,
                error = "Failed to load wallet data: ${e.message}"
            )
        }
    }

    private fun observeWallet(userId: String) {
        viewModelScope.launch {
            walletRepository.observeWallet(userId).collect { wallet ->
                _walletState.value = _walletState.value.copy(wallet = wallet)
            }
        }
    }

    fun requestWithdrawal(
        amount: String,
        bankName: String,
        accountNumber: String,
        accountHolderName: String
    ) {
        viewModelScope.launch {
            try {
                _walletState.value = _walletState.value.copy(
                    isProcessing = true,
                    error = null,
                    successMessage = null
                )

                val currentUser: FirebaseUser? = authRepository.getCurrentUser()
                if (currentUser == null) {
                    throw Exception("User not authenticated")
                }

                val withdrawalAmount = amount.toDoubleOrNull() ?: throw Exception("Invalid amount")

                // Validations
                if (withdrawalAmount <= 0) {
                    throw Exception("Amount must be greater than zero")
                }

                if (bankName.isBlank()) {
                    throw Exception("Please enter bank name")
                }

                if (accountNumber.isBlank()) {
                    throw Exception("Please enter account number")
                }

                if (accountHolderName.isBlank()) {
                    throw Exception("Please enter account holder name")
                }

                val requestId = walletRepository.requestWithdrawal(
                    userId = currentUser.uid,
                    amount = withdrawalAmount,
                    bankName = bankName,
                    accountNumber = accountNumber,
                    accountHolderName = accountHolderName
                )

                _walletState.value = _walletState.value.copy(
                    isProcessing = false,
                    successMessage = "Withdrawal request submitted successfully. Reference: ${requestId.take(8)}",
                    error = null
                )

                // Refresh data
                loadInitialData(currentUser.uid)

            } catch (e: Exception) {
                _walletState.value = _walletState.value.copy(
                    isProcessing = false,
                    error = "Withdrawal failed: ${e.message}",
                    successMessage = null
                )
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val currentUser: FirebaseUser? = authRepository.getCurrentUser()
            currentUser?.let { user ->
                try {
                    _walletState.value = _walletState.value.copy(isLoading = true)
                    loadInitialData(user.uid)
                } catch (e: Exception) {
                    _walletState.value = _walletState.value.copy(
                        isLoading = false,
                        error = "Failed to refresh: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _walletState.value = _walletState.value.copy(
            error = null,
            successMessage = null
        )
    }
}

data class WalletState(
    val wallet: Wallet? = null,
    val isLoading: Boolean = true,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)