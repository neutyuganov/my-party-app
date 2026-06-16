package com.example.mypartyapp.feature.auth.data

import com.example.mypartyapp.core.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
private data class ProfileInsert(val id: String, val username: String)

class AuthRepository {

    // Ждёт полного завершения инициализации Auth: загрузка токена с диска +
    // сетевой refresh если токен протух. Только после этого currentSessionOrNull() стабилен.
    suspend fun awaitSessionInit() {
        supabaseClient.auth.awaitInitialization()
    }

    fun isSessionActive(): Boolean {
        return supabaseClient.auth.currentSessionOrNull() != null
    }

    suspend fun signUp(email: String, password: String, username: String) {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: throw Exception("Не удалось получить ID пользователя")
        try {
            supabaseClient.postgrest.from("profiles").insert(ProfileInsert(userId, username))
        } catch (e: Exception) {
            // Профиль не создался — сбрасываем сессию, чтобы пользователь не оказался
            // авторизован без профиля. Запись в auth.users остаётся — admin key нужен для удаления.
            // Постоянное решение: PostgreSQL-триггер on INSERT to auth.users создаёт профиль сам.
            runCatching { supabaseClient.auth.signOut() }
            throw e
        }
    }

    suspend fun signIn(email: String, password: String) {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    // Выход сбрасывает локальную сессию даже при недоступном сервере
    suspend fun signOut() {
        supabaseClient.auth.signOut()
    }
}
