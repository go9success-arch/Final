package com.aim.lifemate.data.repositories

// AuthRepository.kt

import com.aim.lifemate.data.models.UserWal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun getCurrentUserProfile(): UserWal? {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            firebaseUser?.let {
                UserWal(
                    uid = it.uid,
                    name = it.displayName ?: "",
                    email = it.email ?: ""
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun observeAuthState(): Flow<FirebaseUser?> = flow {
        firebaseAuth.addAuthStateListener { auth ->
            emit(auth.currentUser)
        }
    }
}