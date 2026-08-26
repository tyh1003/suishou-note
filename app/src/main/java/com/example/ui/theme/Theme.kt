package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MinimalPrimary,
    onPrimary = Color.White,
    primaryContainer = MinimalSubtleBg,
    onPrimaryContainer = MinimalTextPrimary,
    secondary = MinimalTextSecondary,
    onSecondary = Color.White,
    secondaryContainer = MinimalSubtleBg,
    onSecondaryContainer = MinimalTextPrimary,
    tertiary = MinimalFabCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFDE8DD),
    onTertiaryContainer = MinimalTextPrimary,
    background = MinimalBg,
    onBackground = MinimalTextPrimary,
    surface = Color.White,
    onSurface = MinimalTextPrimary,
    surfaceVariant = MinimalSubtleBg,
    onSurfaceVariant = MinimalTextSecondary,
    outline = MinimalBorder,
    outlineVariant = MinimalBorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEAA07B),
    onPrimary = Color(0xFF4C210A),
    primaryContainer = Color(0xFF703417),
    onPrimaryContainer = Color(0xFFFFDBCB),
    secondary = Color(0xFFD8B092),
    onSecondary = Color(0xFF3F2716),
    secondaryContainer = Color(0xFF5A3D2A),
    onSecondaryContainer = Color(0xFFF7DDC7),
    tertiary = Color(0xFFFF9E79),
    onTertiary = Color(0xFF561D04),
    tertiaryContainer = Color(0xFF7D2E0B),
    onTertiaryContainer = Color(0xFFFFDBCF),
    background = Color(0xFF1E1A17),
    onBackground = Color(0xFFEDE0D7),
    surface = Color(0xFF28231F),
    onSurface = Color(0xFFEDE0D7),
    surfaceVariant = Color(0xFF3A322C),
    onSurfaceVariant = Color(0xFFD4C2B7),
    outline = Color(0xFF54473F),
    outlineVariant = Color(0xFF3A322C)
)

@Composable
fun SuiShouJiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
