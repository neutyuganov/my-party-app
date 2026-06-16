package com.example.mypartyapp.feature.events.data

import com.example.mypartyapp.core.network.retryingRead
import com.example.mypartyapp.core.network.supabaseClient
import com.example.mypartyapp.feature.events.domain.Event
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class EventRepository {

    // get_feed() сам берёт пользователя из auth.uid() на сервере — id передавать не нужно.
    // Идемпотентное чтение → оборачиваем в retryingRead (защита от зависаний +
    // короткий таймаут на попытку + повтор сетевых сбоев). См. core/network/NetworkRetry.
    suspend fun getFeed(): List<Event> = retryingRead("getFeed") {
        supabaseClient.postgrest.rpc(function = "get_feed").decodeList<Event>()
    }
}
