package com.zybooks.basket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.zybooks.basket.data.User
import com.zybooks.basket.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { user ->
                _currentUser.value = user

                user?.uid?.let { uid ->
                    loadUserProfile(uid)
                }
            }
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        username: String
    ): Result<FirebaseUser> {
        _isLoading.value = true
        val result = authRepository.signUp(email, password, username)
        _isLoading.value = false
        return result
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        _isLoading.value = true
        val result = authRepository.signIn(email, password)
        _isLoading.value = false
        return result
    }

    fun signOut() {
        authRepository.signOut()
        _userProfile.value = null
    }

    private fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            val result = authRepository.getUserProfile(uid)
            result.onSuccess { user ->
                _userProfile.value = user
            }
        }
    }

    suspend fun updateUserProfile(user: User): Result<Unit> {
        return authRepository.updateUserProfile(user).also { result ->
            result.onSuccess {
                _userProfile.value = user
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.currentUser != null
    }
}