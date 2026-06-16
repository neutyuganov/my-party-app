package com.example.mypartyapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Светлая тема — основной дизайн СОНМ (роли из дизайн-системы).
private val LightColorScheme = lightColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = CrimsonContainer,
    onPrimaryContainer = OnCrimsonContainer,
    secondary = AccentPurple,            // акцент / постоплата
    onSecondary = Color.White,
    secondaryContainer = PurpleContainer,
    onSecondaryContainer = OnPurpleContainer,
    tertiary = Amber,                    // рейтинг / предупреждения
    onTertiary = NearBlack,
    background = Canvas,                  // фон-холст #F2EEE9
    onBackground = NearBlack,
    surface = SurfaceWhite,              // карточки / экраны
    onSurface = NearBlack,
    surfaceVariant = SurfaceWhite,
    onSurfaceVariant = Muted,            // вторичный текст
    outline = Border,                    // рамки инпутов / разделители
    outlineVariant = Border,
    error = Crimson
)

// Тёмная тема — производная. Дизайн рассчитан на светлую, но не ломаемся в тёмной.
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = AccentPurple,
    onSecondary = Color.White,
    tertiary = Amber,
    onTertiary = NearBlack,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun MyPartyAppTheme(
    // Следуем системной теме. Тёмная палитра пока производная (цвета подберём
    // позже) — но статус-бар не форсим, чтобы его иконки оставались согласованы
    // с системной темой телефона.
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}