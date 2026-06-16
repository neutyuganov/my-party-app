package com.example.mypartyapp.core.network

import com.example.mypartyapp.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

val supabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    // Общий таймаут запроса (плагин HttpTimeout). 15s — компромисс: достаточно
    // для медленной сети, но пользователь не ждёт слишком долго перед ошибкой.
    requestTimeout = 15.seconds

    // ВАЖНО: requestTimeout НЕ задаёт таймаут сокета — он живёт в движке OkHttp
    // и по умолчанию равен 10s. Задаём явно, согласованно с requestTimeout.
    httpEngine = OkHttp.create {
        config {
            connectTimeout(15, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
        }
    }

    install(Postgrest)
    install(Storage)
    install(Auth) {
        // SettingsSessionManager сохраняет токены в SharedPreferences.
        // Инициализируется автоматически через androidx.startup без Application-класса.
        sessionManager = SettingsSessionManager()
    }
}
