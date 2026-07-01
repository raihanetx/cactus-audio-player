package com.cactus.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = White,
    primaryContainer = Blue500.copy(alpha = 0.12f),
    secondary = Neutral700,
    background = Neutral50,
    onBackground = Neutral900,
    surface = White,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral500,
    outline = Neutral200,
    outlineVariant = Neutral100,
    error = Red500,
    onError = White,
)

@Composable
fun CactusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = CactusTypography,
        content = content
    )
}
