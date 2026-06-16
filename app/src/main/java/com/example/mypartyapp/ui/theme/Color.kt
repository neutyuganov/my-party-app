package com.example.mypartyapp.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────
//  Дизайн-фундамент СОНМ — палитра (роли как в дизайн-системе)
// ─────────────────────────────────────────────────────────────

val Crimson = Color(0xFFBF0043)      // Primary
val Teal = Color(0xFF00C896)         // Success / бесплатные ивенты
val AccentPurple = Color(0xFF7C5CFC) // Accent / постоплата
val Amber = Color(0xFFF59E0B)        // Rating / Warning
val NearBlack = Color(0xFF14111A)    // onBackground / фон навбара
val SurfaceWhite = Color(0xFFFFFFFF) // Card / фон экрана
val Canvas = Color(0xFFF2EEE9)       // Scaffold / фон-холст
val Border = Color(0xFFEDEAE6)       // Divider / рамка инпута
val Muted = Color(0xFF6E6875)        // Вторичный текст

/** Чистый фирменный цвет логотипа (= Crimson). */
val BrandCrimson = Crimson

// Мягкие подложки под акценты (для контейнеров Material)
val CrimsonContainer = Color(0xFFFFD9DF)
val OnCrimsonContainer = Color(0xFF400012)
val PurpleContainer = Color(0xFFE7DEFF)
val OnPurpleContainer = Color(0xFF21005D)

// ── Тёмная тема (производная, дизайн рассчитан на светлую) ────
val DarkPrimary = Color(0xFFFFB2BD)
val DarkOnPrimary = Color(0xFF660021)
val DarkBackground = Color(0xFF14111A)
val DarkSurface = Color(0xFF1E1A24)
val DarkOnSurface = Color(0xFFF2EEE9)
val DarkOnSurfaceVariant = Color(0xFFB5AEBB)
val DarkOutline = Color(0xFF3A3540)
