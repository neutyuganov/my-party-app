package com.example.mypartyapp.feature.auth.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthErrorMapperTest {

    @Test
    fun `неверные данные входа — человекочитаемый текст`() {
        assertEquals("Неверный email или пароль", mapAuthError("Invalid login credentials"))
        assertEquals("Неверный email или пароль", mapAuthError("error: invalid_credentials"))
    }

    @Test
    fun `email уже зарегистрирован`() {
        assertEquals("Этот email уже зарегистрирован", mapAuthError("User already registered"))
        assertEquals("Этот email уже зарегистрирован", mapAuthError("user_already_exists"))
    }

    @Test
    fun `слабый пароль`() {
        assertEquals("Слишком простой пароль", mapAuthError("weak_password"))
        assertEquals("Слишком простой пароль", mapAuthError("Password should be at least 6 characters"))
    }

    @Test
    fun `превышен лимит запросов`() {
        assertEquals("Слишком много попыток. Подожди немного", mapAuthError("over_email_send_rate_limit"))
        assertEquals("Слишком много попыток. Подожди немного", mapAuthError("Email rate limit exceeded"))
    }

    @Test
    fun `сетевые ошибки сводятся к одному сообщению`() {
        assertEquals("Нет подключения к интернету", mapAuthError("Unable to resolve host"))
        assertEquals("Нет подключения к интернету", mapAuthError("Failed to connect to db"))
        assertEquals("Нет подключения к интернету", mapAuthError("Request timeout"))
    }

    @Test
    fun `сверка регистронезависима`() {
        assertEquals("Неверный email или пароль", mapAuthError("INVALID LOGIN CREDENTIALS"))
    }

    @Test
    fun `неизвестная ошибка и null — общий текст`() {
        assertEquals("Что-то пошло не так. Попробуй ещё раз", mapAuthError("какая-то непонятная ошибка"))
        assertEquals("Что-то пошло не так. Попробуй ещё раз", mapAuthError(null))
    }
}
