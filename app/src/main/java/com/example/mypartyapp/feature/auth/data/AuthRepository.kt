package com.example.mypartyapp.feature.auth.data

import com.example.mypartyapp.core.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
private data class ProfileInsert(val id: String, val username: String)

class AuthRepository {

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
        supabaseClient.postgrest.from("profiles").insert(ProfileInsert(userId, username))
    }

    suspend fun signIn(email: String, password: String) {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        supabaseClient.auth.signOut()
    }
}