package com.example.mypartyapp.feature.auth.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypartyapp.feature.auth.data.AuthRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAuthenticated by mutableStateOf(false)
        private set

    // true пока Auth SDK загружает токен с диска и при необходимости обновляет его по сети.
    // MainActivity держит системный splash screen видимым пока этот флаг true.
    var isCheckingSession by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            try {
                // Таймаут нужен: если сеть упала во время refresh токена,
                // awaitInitialization зависнет и splash screen останется навсегда.
                // 12s > requestTimeout (10s) — HTTP таймаут должен сработать раньше.
                withTimeout(12_000L) { repository.awaitSessionInit() }
            } catch (_: TimeoutCancellationException) {
                // Инициализация не завершилась вовремя — считаем пользователя не авторизованным
            }
            isAuthenticated = repository.isSessionActive()
            isCheckingSession = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.signIn(email, password)
                isAuthenticated = true
            } catch (e: Exception) {
                parseError(e.message)
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.signUp(email, password, username)
                isAuthenticated = true
            } catch (e: Exception) {
                parseError(e.message)
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        // Сбрасываем синхронно ДО навигации — иначе LoginScreen увидит isAuthenticated=true
        // и сразу вернёт пользователя назад через LaunchedEffect
        isAuthenticated = false
        viewModelScope.launch {
            isLoading = true
            try {
                repository.signOut()
            } catch (e: Exception) {
                // Серверный выход упал (сеть недоступна) — не критично:
                // локальная сессия уже сброшена выше
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }

    // Переводит технические сообщения SDK в читаемые для пользователя строки
    private fun parseError(errorText: String?) {
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
