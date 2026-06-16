package com.example.mypartyapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mypartyapp.R

// ─────────────────────────────────────────────────────────────
//  Дизайн-фундамент СОНМ — типографика
//  Шрифты: Unbounded (Display) + Manrope (всё остальное).
//  Файлы — вариативные (.ttf с осью веса), лежат в res/font.
//  Для каждой насыщенности задаём явный вес через FontVariation.weight,
//  чтобы вариативный шрифт отрисовывался ровно в нужной толщине.
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalTextApi::class)
private fun manrope(weight: Int) = Font(
    R.font.manrope_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

@OptIn(ExperimentalTextApi::class)
private fun unbounded(weight: Int) = Font(
    R.font.unbounded_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

private val BodyFont = FontFamily(
    manrope(400), manrope(500), manrope(600), manrope(700), manrope(800)
)

private val DisplayFont = FontFamily(
    unbounded(700), unbounded(800)
)

val Typography = Typography(
    // DISPLAY · 28sp/800 · Unbounded — онбординг, RSVP success
    displaySmall = TextStyle(
        fontFamily = DisplayFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    // H1 · 22sp/800 · Manrope — заголовки экранов
    headlineMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // H2 · 18sp/700 — заголовки секций
    titleLarge = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    // H3 · 15sp/700 — заголовки карточек
    titleMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    // BODY · 14sp/500 — основной текст
    bodyLarge = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // SMALL · 12.5sp/500 — мета-информация
    bodySmall = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.5f.sp,
        lineHeight = 17.sp
    ),
    // LABEL · 11sp/700 · трекинг 0.4 — метки, чипы, навигация
    labelLarge = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
)
