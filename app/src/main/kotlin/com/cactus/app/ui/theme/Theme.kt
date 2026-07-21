package com.cactus.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/* PRD §5 — Warm Sand & Cactus Green. Dark mode is the default (§5.2, §12.5):
 * warm charcoal surfaces, never pure black, easy on the eyes before sleep.
 * "Surface" (#1A1A14) is the app background; "Background" (#121210) is the
 * deepest base layer. "Surface Container" (#25251E) is the elevated surface. */
private val DarkColorScheme = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onSurface,
    secondary = secondary,
    onSecondary = Color(0xFF3A2A12),
    tertiary = tertiary,
    onTertiary = Color(0xFF3A1E2C),
    background = background,
    onBackground = onSurface,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceContainer,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outline,
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
)

private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onSurface,
    secondary = secondary,
    onSecondary = Color(0xFF3A2A12),
    tertiary = tertiary,
    onTertiary = Color(0xFF3A1E2C),
    background = Color(0xFFF5F3EC),
    onBackground = Color(0xFF1A1A14),
    surface = Color(0xFFFBFAF5),
    onSurface = Color(0xFF1A1A14),
    surfaceVariant = Color(0xFFEDE9DD),
    onSurfaceVariant = Color(0xFF5A5848),
    outline = Color(0xFFC9C4B4),
    outlineVariant = Color(0xFFC9C4B4),
)

@Composable
fun CactusTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CactusTypography,
        shapes = CactusShapes,
        content = content,
    )
}
