package com.example.mypartyapp.core.network

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkRetryTest {

    @Test
    fun `возвращает результат и вызывает блок один раз при успехе`() = runBlocking {
        var calls = 0
        val result = retryingRead("test") { calls++; "ok" }
        assertEquals("ok", result)
        assertEquals(1, calls)
    }

    @Test
    fun `неидемпотентная ошибка пробрасывается сразу, без повторов`() = runBlocking {
        var calls = 0
        val outcome = runCatching {
            retryingRead<Int>("test") {
                calls++
                throw IllegalStateException("разбор данных") // не сетевая ошибка
            }
        }
        assertTrue(outcome.isFailure)
        assertTrue(outcome.exceptionOrNull() is IllegalStateException)
        assertEquals(1, calls) // ровно одна попытка — не сетевые ошибки не повторяем
    }
}
