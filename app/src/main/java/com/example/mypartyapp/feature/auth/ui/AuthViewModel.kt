package com.example.mypartyapp.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.example.mypartyapp.feature.auth.data.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAuthenticated by mutableStateOf(false)
        private set

    init {
        isAuthenticated = repository.isSessionActive()
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.signIn(email, password)
                isAuthenticated = true
            } catch (e: Exception) {
                errorCheck(e.message)
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.signUp(email, password)
                isAuthenticated = true
            } catch (e: Exception) {
                errorCheck(e.message)
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            isLoading = true
            try {
                repository.signOut()
            } finally {
                isAuthenticated = false
                isLoading = false
            }
        }
    }

    private fun errorCheck(errorText: String?) {
        errorMessage = when {
            errorText?.contains("Invalid login credentials") == true ->
                "Неверный email или пароль"
            errorText?.contains("Unable to resolve host") == true ->
                "Нет подключения к интернету"
            errorText?.contains("User already registered") == true ->
                "Этот email уже зарегистрирован"
            else -> "Что-то пошло не так. Попробуй ещё раз"
        }
    }
}
