package com.example.mypartyapp.feature.auth.data

import com.example.mypartyapp.core.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    fun isSessionActive(): Boolean {
        return supabaseClient.auth.currentSessionOrNull() != null
    }

    suspend fun signUp(email: String, password: String) {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
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