package com.example.mypartyapp.feature.events.domain

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class EventSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    // ── EventStatus ──────────────────────────────────────────────

    @Test
    fun `статус — известные значения совпадают с CHECK в БД`() {
        assertEquals(EventStatus.ACTIVE, json.decodeFromString<EventStatus>("\"active\""))
        assertEquals(EventStatus.CANCELLED, json.decodeFromString<EventStatus>("\"cancelled\""))
        assertEquals(EventStatus.FINISHED, json.decodeFromString<EventStatus>("\"finished\""))
    }

    @Test
    fun `статус — незнакомое значение из БД не крашит, а откатывается к ACTIVE`() {
        assertEquals(EventStatus.ACTIVE, json.decodeFromString<EventStatus>("\"completed\""))
        assertEquals(EventStatus.ACTIVE, json.decodeFromString<EventStatus>("\"что-угодно\""))
    }

    @Test
    fun `статус сериализуется обратно в строку БД`() {
        assertEquals("\"finished\"", json.encodeToString(EventStatus.FINISHED))
    }

    // ── PaymentType ──────────────────────────────────────────────

    @Test
    fun `тип оплаты — известные значения`() {
        assertEquals(PaymentType.FREE, json.decodeFromString<PaymentType>("\"free\""))
        assertEquals(PaymentType.PREPAID, json.decodeFromString<PaymentType>("\"prepaid\""))
        assertEquals(PaymentType.POSTPAID, json.decodeFromString<PaymentType>("\"postpaid\""))
    }

    @Test
    fun `тип оплаты — незнакомое значение откатывается к FREE`() {
        assertEquals(PaymentType.FREE, json.decodeFromString<PaymentType>("\"paid\""))
    }

    // ── Event (контракт с get_feed) ──────────────────────────────

    @Test
    fun `минимальная строка из БД декодируется с дефолтами`() {
        val raw = """
            {
              "id": "e1",
              "host_id": "h1",
              "title": "Вечеринка",
              "starts_at": "2026-07-01T20:00:00+00:00",
              "invite_code": "abc12345"
            }
        """.trimIndent()

        val event = json.decodeFromString<Event>(raw)

        assertEquals("e1", event.id)
        assertEquals("h1", event.hostId)
        assertEquals("Вечеринка", event.title)
        assertEquals("abc12345", event.inviteCode)
        // дефолты
        assertEquals(EventStatus.ACTIVE, event.status)
        assertEquals(PaymentType.FREE, event.paymentType)
        assertEquals(0, event.price)
        assertEquals(1, event.minGuests)
        assertEquals(false, event.isPrivate)
    }

    @Test
    fun `полная строка маппит snake_case поля`() {
        val raw = """
            {
              "id": "e2",
              "host_id": "h2",
              "title": "Барбекю",
              "starts_at": "2026-08-10T18:00:00+00:00",
              "invite_code": "xyz98765",
              "max_guests": 20,
              "is_private": true,
              "payment_type": "prepaid",
              "price": 1500,
              "status": "active"
            }
        """.trimIndent()

        val event = json.decodeFromString<Event>(raw)

        assertEquals(20, event.maxGuests)
        assertEquals(true, event.isPrivate)
        assertEquals(PaymentType.PREPAID, event.paymentType)
        assertEquals(1500, event.price)
    }
}
