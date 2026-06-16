package com.example.mypartyapp.feature.events.data

import android.util.Log
import com.example.mypartyapp.core.network.supabaseClient
import com.example.mypartyapp.feature.events.domain.Event
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.random.Random

private const val TAG = "EventRepository"

class EventRepository {

    private companion object {
        // Лента — foreground-операция: пользователь ждёт и смотрит на экран.
        const val MAX_ATTEMPTS = 2          // один автоповтор: гасит разовый блип, но не держит долго
        const val ATTEMPT_TIMEOUT_MS = 5_000L // обрываем зависшую попытку рано (socket-таймаут движка — 15с)
        const val BASE_BACKOFF_MS = 400L
    }

    // get_feed() сам берёт пользователя из auth.uid() на сервере — id передавать не нужно.
    //
    // Стратегия: короткий таймаут на попытку + один автоповтор. Изредка соединение
    // протухает (пул соединений / просадка сети / free-tier) и запрос молча висит —
    // обрываем его через ATTEMPT_TIMEOUT_MS и пробуем заново на свежем соединении.
    // Повторяем ТОЛЬКО зависания и сетевые сбои; ошибки HTTP/разбора пробрасываем сразу.
    // Если сервер недоступен дольше бюджета — отдаём ошибку, дальше повтор инициирует
    // пользователь (кнопка «Повторить»). Пауза с джиттером — против синхронных повторов
    // при росте числа клиентов.
    suspend fun getFeed(): List<Event> {
        var lastError: Exception? = null
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                return withTimeout(ATTEMPT_TIMEOUT_MS) {
                    supabaseClient.postgrest.rpc(function = "get_feed").decodeList<Event>()
                }
            } catch (e: TimeoutCancellationException) {
                lastError = e
                Log.w(TAG, "getFeed: попытка ${attempt + 1}/$MAX_ATTEMPTS прервана по таймауту ($ATTEMPT_TIMEOUT_MS мс)")
            } catch (e: HttpRequestException) {
                lastError = e
                Log.w(TAG, "getFeed: попытка ${attempt + 1}/$MAX_ATTEMPTS — сетевой сбой: ${e.message}")
            }
            if (attempt < MAX_ATTEMPTS - 1) {
                val backoff = BASE_BACKOFF_MS shl attempt // экспоненциальная база
                delay(backoff + Random.nextLong(BASE_BACKOFF_MS)) // + джиттер
            }
        }
        Log.e(TAG, "getFeed: не удалось загрузить за $MAX_ATTEMPTS попыток", lastError)
        throw lastError!!
    }
}
