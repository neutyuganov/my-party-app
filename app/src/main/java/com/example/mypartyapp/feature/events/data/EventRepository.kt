package com.example.mypartyapp.feature.events.data

import com.example.mypartyapp.core.network.supabaseClient
import com.example.mypartyapp.feature.events.domain.Event
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.delay

class EventRepository {

    // get_feed() сам берёт пользователя из auth.uid() на сервере — id передавать не нужно.
    //
    // Автоповтор: изредка запрос подвисает на уровне сети (просадка Wi-Fi, протухшее
    // соединение, редкая задержка free-tier). Один такой блип не должен показывать
    // пользователю ошибку — пробуем ещё раз. Повторяем ТОЛЬКО сетевые сбои; ошибки
    // разбора данных и т.п. пробрасываем сразу, без повторов.
    suspend fun getFeed(): List<Event> {
        val maxAttempts = 3
        var lastError: HttpRequestException? = null
        repeat(maxAttempts) { attempt ->
            try {
                return supabaseClient.postgrest.rpc(function = "get_feed").decodeList<Event>()
            } catch (e: HttpRequestException) {
                lastError = e
                if (attempt < maxAttempts - 1) {
                    delay(700L * (attempt + 1)) // нарастающая пауза: 0.7s, затем 1.4s
                }
            }
        }
        throw lastError!!
    }
}
