package com.example.mypartyapp.core.network

import com.example.mypartyapp.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.ConnectionPool
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

val supabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    // Общий таймаут запроса (плагин HttpTimeout). 15s — компромисс: достаточно
    // для медленной сети, но пользователь не ждёт слишком долго перед ошибкой.
    requestTimeout = 15.seconds

    // ВАЖНО: requestTimeout НЕ задаёт таймаут сокета — он живёт в движке OkHttp.
    httpEngine = OkHttp.create {
        config {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)

            // Главный фикс «бесконечных failed» на мобильной сети.
            // Роутер/сеть тихо роняет простаивающее keep-alive соединение (half-open),
            // OkHttp этого не видит и переиспользует мёртвое соединение — запрос висит
            // до readTimeout. Активные HTTP/2-пинги проверяют живость: мёртвое соединение
            // обнаруживается за ~5с и выбрасывается, а retryOnConnectionFailure
            // переустанавливает запрос на свежем соединении.
            retryOnConnectionFailure(true)
            pingInterval(5, TimeUnit.SECONDS)

            // Не держим простаивающие соединения долго — меньше шанс наткнуться на протухшее.
            connectionPool(ConnectionPool(5, 30, TimeUnit.SECONDS))
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
