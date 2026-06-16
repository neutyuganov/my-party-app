package com.example.mypartyapp.core.network

import com.example.mypartyapp.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.seconds

val supabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    requestTimeout = 10.seconds

    install(Postgrest)
    install(Storage)
    install(Auth) {
        // SettingsSessionManager сохраняет токены в SharedPreferences.
        // Инициализируется автоматически через androidx.startup без Application-класса.
        sessionManager = SettingsSessionManager()
    }
}
