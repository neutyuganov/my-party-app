package com.example.mypartyapp.feature.auth.data

import com.example.mypartyapp.core.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    // Ждёт полного завершения инициализации Auth: загрузка токена с диска +
    // сетевой refresh если токен протух. Только после этого currentSessionOrNull() стабилен.
    suspend fun awaitSessionInit() {
        supabaseClient.auth.awaitInitialization()
    }

    fun isSessionActive(): Boolean {
        return supabaseClient.auth.currentSessionOrNull() != null
    }

    // Профиль НЕ создаётся здесь вручную: строку в public.profiles создаёт серверный
    // триггер on_auth_user_created → handle_new_user(), читая username из метаданных.
    // Поэтому username передаём в data (raw_user_meta_data), а не отдельным INSERT.
    suspend fun signUp(email: String, password: String, username: String) {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("username", username)
            }
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
