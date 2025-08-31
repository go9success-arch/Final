// WellnessViewModel.kt
package com.aim.lifemate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aim.lifemate.data.models.WellnessRemedy
import com.aim.lifemate.data.repositories.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WellnessState(
    val remedies: List<WellnessRemedy> = emptyList(),
    val filteredRemedies: List<WellnessRemedy> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLanguage: String = "en"
)

@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val repository: WellnessRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WellnessState())
    val state: StateFlow<WellnessState> = _state.asStateFlow()

    init {
        loadRemedies()
    }

    private fun loadRemedies() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getRemedies().collect { remedies ->
                _state.value = _state.value.copy(
                    remedies = remedies,
                    filteredRemedies = remedies,
                    isLoading = false
                )
            }
        }
    }

    fun searchRemedies(query: String) {
        val currentState = _state.value
        val filtered = if (query.isEmpty()) {
            currentState.remedies
        } else {
            repository.searchRemedies(query, currentState.remedies, currentState.currentLanguage)
        }

        _state.value = currentState.copy(
            searchQuery = query,
            filteredRemedies = filtered
        )
    }

    fun setLanguage(language: String) {
        val currentState = _state.value
        // Re-filter with new language
        val filtered = if (currentState.searchQuery.isEmpty()) {
            currentState.remedies
        } else {
            repository.searchRemedies(currentState.searchQuery, currentState.remedies, language)
        }

        _state.value = currentState.copy(
            currentLanguage = language,
            filteredRemedies = filtered
        )
    }

    fun getCurrentLanguage(): String = _state.value.currentLanguage
}