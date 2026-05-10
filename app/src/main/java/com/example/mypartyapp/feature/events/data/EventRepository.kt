package com.example.mypartyapp.feature.events.data

import com.example.mypartyapp.core.network.supabaseClient
import com.example.mypartyapp.feature.events.domain.Event
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest

class EventRepository {

    suspend fun getFeed(): List<Event> {
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: return emptyList()
        return supabaseClient.postgrest.rpc(
            function = "get_feed",
            parameters = mapOf("p_user_id" to userId)
        ).decodeList<Event>()
    }
}
