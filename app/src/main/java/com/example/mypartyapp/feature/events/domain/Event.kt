package com.example.mypartyapp.feature.events.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    @SerialName("host_id") val hostId: String,
    val title: String,
    val description: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String? = null,
    @SerialName("max_guests") val maxGuests: Int? = null,
    @SerialName("min_guests") val minGuests: Int = 1,
    @SerialName("is_private") val isPrivate: Boolean = false,
    @SerialName("invite_code") val inviteCode: String,
    val status: String = "active",
    @SerialName("cover_image_url") val coverImageUrl: String? = null,
    @SerialName("telegram_chat_url") val telegramChatUrl: String? = null,
    @SerialName("payment_type") val paymentType: String = "free",
    val price: Int = 0,
    @SerialName("commission_pct") val commissionPct: Int = 10,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("deleted_at") val deletedAt: String? = null
)
