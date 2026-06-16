package com.example.mypartyapp.feature.auth.ui

// Переводит технические сообщения SDK в читаемые для пользователя строки.
// Сверяем по нижнему регистру и учитываем как человекочитаемый текст, так и
// коды ошибок Supabase (error_code) — формулировки SDK со временем меняются.
// Вынесено из AuthViewModel отдельной чистой функцией, чтобы покрыть юнит-тестами.
internal fun mapAuthError(errorText: String?): String {
    val t = errorText?.lowercase() ?: ""
    return when {
        t.contains("invalid login credentials") || t.contains("invalid_credentials") ->
            "Неверный email или пароль"
        t.contains("user already registered") || t.contains("user_already_exists") ->
            "Этот email уже зарегистрирован"
        t.contains("weak_password") || t.contains("password should be") ->
            "Слишком простой пароль"
        t.contains("over_email_send_rate_limit") || t.contains("rate limit") ->
            "Слишком много попыток. Подожди немного"
        t.contains("unable to resolve host") || t.contains("failed to connect") ||
            t.contains("timeout") || t.contains("network") ->
            "Нет подключения к интернету"
        else -> "Что-то пошло не так. Попробуй ещё раз"
    }
}
