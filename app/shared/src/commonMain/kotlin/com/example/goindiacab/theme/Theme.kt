package com.example.goindiacab.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = TextWhite,
    primaryContainer = SurfaceGray,
    onPrimaryContainer = TextDark,
    secondary = BrandOrange,
    onSecondary = TextWhite,
    background = BackgroundWhite,
    onBackground = TextDark,
    surface = BackgroundWhite,
    onSurface = TextDark,
    error = ErrorRed,
    onError = TextWhite,
    outline = BorderGray
)

@Composable
fun GoIndiaCabTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = getGoIndiaCabTypography(),
        content = content
    )
}
