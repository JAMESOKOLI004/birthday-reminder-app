package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkContainer,
    onPrimaryContainer = OnPinkContainer,
    secondary = GoldAccent,
    onSecondary = Color(0xFF78350F),
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = Color(0xFFF472B6),
    onTertiary = Color.White,
    background = SoftPinkBg,
    onBackground = DarkText,
    surface = SoftPinkSurface,
    onSurface = DarkText,
    surfaceVariant = SoftPinkSurfaceVariant,
    onSurfaceVariant = SubtitleText,
    outline = LightBorder,
    outlineVariant = Color(0xFFFEE2E2)
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF831843),
    onPrimaryContainer = PinkContainer,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = GoldContainer,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun CelebrateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CelebrateTypography,
        content = content
    )
}
