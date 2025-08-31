// ProfileRepository.kt
package com.aim.lifemate.data.repositories

import com.aim.lifemate.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor() {

    private var currentUser = UserProfile() // Use UserProfile instead

    fun getUser(): Flow<UserProfile> = flow { // Return UserProfile flow
        emit(currentUser)
    }

    suspend fun updateUserName(name: String) {
        currentUser = currentUser.copy(name = name)
    }

    suspend fun updateEmail(email: String) {
        currentUser = currentUser.copy(email = email)
    }

    suspend fun updateLanguagePreference(language: String) {
        currentUser = currentUser.copy(languagePreference = language)
    }

    suspend fun toggleNotifications(enabled: Boolean) {
        currentUser = currentUser.copy(notificationEnabled = enabled)
    }

    suspend fun logout() {
        currentUser = UserProfile() // Reset to default UserProfile
    }
}