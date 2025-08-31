package com.aim.lifemate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aim.lifemate.data.models.*
import com.aim.lifemate.services.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var userListener: ListenerRegistration? = null
    private var balanceListener: ListenerRegistration? = null

    init {
        setupFirebaseListeners()
        loadInitialData()
    }

    private fun setupFirebaseListeners() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Listen to user data changes
            userListener = firestoreService.listenToUserProfile(currentUser.uid) { userProfile ->
                _state.value = _state.value.copy(userProfile = userProfile)
            }

            // Listen to wallet balance
            balanceListener = firestoreService.listenToWalletBalance(currentUser.uid) { balance ->
                _state.value = _state.value.copy(walletBalance = balance)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Load featured wellness practices
                val wellness = firestoreService.getWellnessRemedies()
                _state.value = _state.value.copy(featuredWellness = wellness.take(6))

                // Load job opportunities
                val govtJobs = firestoreService.getGovernmentJobs()
                val privateJobs = firestoreService.getPrivateJobs()
                val allJobs = govtJobs.take(3) + privateJobs.take(3)
                _state.value = _state.value.copy(jobOpportunities = allJobs)

                // Load tournament info
                val tournament = firestoreService.getCurrentTournament()
                _state.value = _state.value.copy(tournamentInfo = tournament)

                _state.value = _state.value.copy(isLoading = false)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to load data",
                    isLoading = false
                )
            }
        }
    }

    fun completeWellnessPractice(practiceId: String, reward: Int) {
        viewModelScope.launch {
            firestoreService.addCoins(reward, "Wellness practice completion: $practiceId")
        }
    }

    fun updateGameScore(score: Int, coinsEarned: Int) {
        viewModelScope.launch {
            firestoreService.addGameScore(
                GameScore(
                    score = score,
                    coinsEarned = coinsEarned,
                    duration = 0 // Will be updated by game logic
                )
            )
        }
    }

    fun participateInTournament(tournamentId: String) {
        viewModelScope.launch {
            val success = firestoreService.participateInTournament(tournamentId)
            if (success) {
                refreshTournamentData()
            } else {
                _state.value = _state.value.copy(
                    error = "Failed to join tournament. Please try again."
                )
            }
        }
    }

    fun requestWithdrawal(amount: Double, upiId: String) {
        viewModelScope.launch {
            val success = firestoreService.requestWithdrawal(amount, upiId)
            if (success) {
                _state.value = _state.value.copy(
                    error = "Withdrawal request submitted successfully!"
                )
            } else {
                _state.value = _state.value.copy(
                    error = "Withdrawal failed. Please check your balance and try again."
                )
            }
        }
    }

    private fun refreshTournamentData() {
        viewModelScope.launch {
            try {
                val tournament = firestoreService.getCurrentTournament()
                _state.value = _state.value.copy(tournamentInfo = tournament)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        balanceListener?.remove()
    }
}

data class AppState(
    val userProfile: UserProfile = UserProfile(),
    val walletBalance: Double = 0.0,
    val featuredWellness: List<WellnessRemedy> = emptyList(),
    val jobOpportunities: List<Any> = emptyList(), // Can be GovernmentJob or PrivateJob
    val tournamentInfo: Tournament? = null,
    val gameStats: GameStats = GameStats(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class GameStats(
    val highScore: Int = 0,
    val totalGames: Int = 0,
    val totalCoinsEarned: Int = 0
)