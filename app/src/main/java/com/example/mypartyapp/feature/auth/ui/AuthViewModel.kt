package com.example.mypartyapp.feature.auth.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypartyapp.feature.auth.data.AuthRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "AuthViewModel"

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
                // awaitInitialization зависнет и splash останется навсегда.
                // 20s > requestTimeout (15s) — HTTP таймаут должен сработать раньше.
                withTimeout(20_000L) { repository.awaitSessionInit() }
            } catch (_: TimeoutCancellationException) {
                // Инициализация не завершилась вовремя — считаем пользователя не авторизованным
            }
            isAuthenticated = repository.isSessionActive()
            isCheckingSession = false
        }
    }

    fun signIn(email: String, password: String) {
        if (isLoading) return  // защита от двойного запуска (кнопка + клавиша "Done")
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.signIn(email, password)
                isAuthenticated = true
            } catch (e: Exception) {
                Log.e(TAG, "signIn failed", e)
                parseError(e.message)
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(email: String, password: String, username: String) {
        if (isLoading) return  // защита от двойного запуска
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.signUp(email, password, username)
                // Если в Supabase включено подтверждение email, сессии после signUp нет —
                // выставляем флаг из реальной сессии, а не безусловно true.
                isAuthenticated = repository.isSessionActive()
                if (!isAuthenticated) {
                    errorMessage = "Мы отправили письмо для подтверждения. Проверь почту"
                }
            } catch (e: Exception) {
                Log.e(TAG, "signUp failed", e)
                parseError(e.message)
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        // Сбрасываем синхронно ДО навигации — иначе LoginScreen увидит isAuthenticated=true
        // и сразу вернёт пользователя назад через LaunchedEffect.
        isAuthenticated = false
        // НЕ трогаем isLoading: этот флаг показывает крутилку на кнопке "Войти".
        // Выход уходит в фон, экран входа уже открыт — крутилка там не нужна.
        // Заодно чистим прошлую ошибку, чтобы она не висела на экране входа.
        errorMessage = null
        isLoading = false
        viewModelScope.launch {
            try {
                repository.signOut()
            } catch (e: Exception) {
                // Серверный выход упал (сеть недоступна) — не критично:
                // локальная сессия уже сброшена выше.
                Log.w(TAG, "signOut: серверный выход не удался (сессия сброшена локально)", e)
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }

    // Маппинг технических ошибок в текст для пользователя вынесен в mapAuthError
    // (AuthErrorMapper.kt) — чистая функция, покрыта тестами.
    private fun parseError(errorText: String?) {
        errorMessage = mapAuthError(errorText)
    }
}
