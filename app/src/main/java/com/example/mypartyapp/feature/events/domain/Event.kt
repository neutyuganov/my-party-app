package com.example.mypartyapp.feature.events.domain

import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// Значения строк должны точно совпадать с CHECK-ограничениями в БД (см. DATABASE.md):
//   events.status        ∈ ('active','cancelled','finished')
//   events.payment_type  ∈ ('free','prepaid','postpaid')
@Serializable(with = EventStatus.Serializer::class)
enum class EventStatus(val value: String) {
    ACTIVE("active"),
    CANCELLED("cancelled"),
    FINISHED("finished");

    // Неизвестное значение из БД не крашит приложение — возвращаем ACTIVE как дефолт,
    // но логируем: это почти всегда сигнал о рассинхроне модели и схемы БД.
    internal object Serializer : KSerializer<EventStatus> {
        override val descriptor = PrimitiveSerialDescriptor("EventStatus", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: EventStatus) = encoder.encodeString(value.value)
        override fun deserialize(decoder: Decoder): EventStatus {
            val raw = decoder.decodeString()
            return entries.find { it.value == raw } ?: run {
                Log.w("EventStatus", "Неизвестный status из БД: '$raw' → fallback ACTIVE")
                ACTIVE
            }
        }
    }
}

@Serializable(with = PaymentType.Serializer::class)
enum class PaymentType(val value: String) {
    FREE("free"),
    PREPAID("prepaid"),
    POSTPAID("postpaid");

    internal object Serializer : KSerializer<PaymentType> {
        override val descriptor = PrimitiveSerialDescriptor("PaymentType", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: PaymentType) = encoder.encodeString(value.value)
        override fun deserialize(decoder: Decoder): PaymentType {
            val raw = decoder.decodeString()
            return entries.find { it.value == raw } ?: run {
                Log.w("PaymentType", "Неизвестный payment_type из БД: '$raw' → fallback FREE")
                FREE
            }
        }
    }
}

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
    val status: EventStatus = EventStatus.ACTIVE,
    @SerialName("cover_image_url") val coverImageUrl: String? = null,
    @SerialName("telegram_chat_url") val telegramChatUrl: String? = null,
    @SerialName("payment_type") val paymentType: PaymentType = PaymentType.FREE,
    val price: Int = 0,
    @SerialName("commission_pct") val commissionPct: Int = 10,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("deleted_at") val deletedAt: String? = null
)
