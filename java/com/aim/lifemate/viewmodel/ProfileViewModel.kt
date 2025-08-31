// ProfileViewModel.kt
package com.aim.lifemate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aim.lifemate.data.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: com.aim.lifemate.data.models.User = com.aim.lifemate.data.models.User(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showLogoutDialog: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getUser().collect { user ->
                _state.value = _state.value.copy(
                    user = user,
                    isLoading = false
                )
            }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            repository.updateUserName(name)
            loadUserData() // Refresh data
        }
    }

    fun updateEmail(email: String) {
        viewModelScope.launch {
            repository.updateEmail(email)
            loadUserData()
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            repository.updateLanguagePreference(language)
            loadUserData()
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleNotifications(enabled)
            loadUserData()
        }
    }

    fun showLogoutDialog(show: Boolean) {
        _state.value = _state.value.copy(showLogoutDialog = show)
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            // In real app, you'd navigate to login screen here
            _state.value = _state.value.copy(showLogoutDialog = false)
        }
    }
}