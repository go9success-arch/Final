package com.aim.lifemate.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aim.lifemate.services.AIService
import com.aim.lifemate.services.VoiceRecognitionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiService: AIService,
    private val voiceRecognitionService: VoiceRecognitionService
) : ViewModel() {

    var state by mutableStateOf(AIState())
        private set

    init {
        // Listen for voice recognition results
        viewModelScope.launch {
            voiceRecognitionService.recognizedText.collectLatest { text ->
                if (text.isNotEmpty() && state.isListeningToVoice) {
                    sendMessage(text)
                    state = state.copy(isListeningToVoice = false)
                }
            }
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        val userMessage = AIMessage(text = message, isUser = true)
        state = state.copy(
            messages = state.messages + userMessage,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val context = when (state.activeTab) {
                    AITab.CHAT -> "general"
                    AITab.CAREER -> "career"
                    AITab.STOCKS -> "stocks"
                }
                val response = aiService.getAIResponse(message, context)
                val aiMessage = AIMessage(text = response, isUser = false)
                state = state.copy(
                    messages = state.messages + aiMessage,
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Failed to get AI response",
                    isLoading = false
                )
            }
        }
    }

    fun startVoiceInput() {
        viewModelScope.launch {
            state = state.copy(isListeningToVoice = true)
            voiceRecognitionService.startListening()
        }
    }

    fun stopVoiceInput() {
        voiceRecognitionService.stopListening()
        state = state.copy(isListeningToVoice = false)
    }

    fun clearConversation() {
        state = state.copy(
            messages = emptyList(),
            error = null
        )
    }

    fun setActiveTab(tab: AITab) {
        if (state.activeTab != tab) {
            state = state.copy(
                activeTab = tab,
                stockInsights = "", // Clear stock insights when switching tabs
                error = null
            )
        }
    }

    fun getStockInsights(symbol: String) {
        if (symbol.isBlank()) {
            state = state.copy(error = "Please enter a stock symbol")
            return
        }

        viewModelScope.launch {
            state = state.copy(isLoadingStocks = true, error = null)
            try {
                val insights = aiService.getStockInsights(symbol)
                state = state.copy(
                    stockInsights = insights,
                    isLoadingStocks = false
                )
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Failed to get stock insights",
                    isLoadingStocks = false
                )
            }
        }
    }

    fun clearError() {
        state = state.copy(error = null)
    }

    fun updateStockSymbol(symbol: String) {
        state = state.copy(stockSymbol = symbol)
    }
}

data class AIState(
    val messages: List<AIMessage> = emptyList(),
    val activeTab: AITab = AITab.CHAT,
    val stockInsights: String = "",
    val stockSymbol: String = "",
    val isLoading: Boolean = false,
    val isLoadingStocks: Boolean = false,
    val isListeningToVoice: Boolean = false,
    val error: String? = null
)

data class AIMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AITab {
    CHAT, CAREER, STOCKS
}