package com.example.goindiacab.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Core Colors
val BrandOrange = Color(0xFFFF6B00)
val BrandOrangeDark = Color(0xFFFF5500)
val BrandBlue = Color(0xFF0052CC)
val BrandBlueDark = Color(0xFF0041A3)

// Splash & Session Backgrounds: linear-gradient(180deg, #0052CC 0%, #091E42 100%)
val SplashDeepBlueTop = Color(0xFF0052CC)
val SplashDeepBlueBottom = Color(0xFF091E42)
val SplashGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0052CC), Color(0xFF091E42))
)

// Surfaces & Backgrounds (Strict Light Theme)
val BackgroundWhite = Color(0xFFFFFFFF)
val SurfaceGray = Color(0xFFF4F6F9)
val SurfaceInputBg = Color(0xFFF8FAFC)
val BorderGray = Color(0xFFE5E7EB)
val BorderGrayLight = Color(0xFFF1F5F9)

// Badges & Accents
val BadgePeachBg = Color(0xFFFFF0E6)
val BadgePeachBorder = Color(0xFFFFDFC8)
val BadgeOrangeText = Color(0xFFFF6B00)
val MarkerGreen = Color(0xFF10B981)
val MarkerOrange = Color(0xFFFF6B00)

// Feedback & Validation
val ErrorRed = Color(0xFFEF4444)
val ErrorRedDark = Color(0xFFDC2626)
val ErrorBgLight = Color(0xFFFEF2F2)
val ErrorBorder = Color(0xFFFCA5A5)

// Typography Text Colors
val TextDark = Color(0xFF111827)
val TextDarkSecondary = Color(0xFF374151)
val TextMuted = Color(0xFF6B7280)
val TextSubtle = Color(0xFF9CA3AF)
val TextWhite = Color(0xFFFFFFFF)
val TextWhiteSecondary = Color(0xFFE6F0FF)

// Links
val LinkBlue = Color(0xFF0052CC)
val LinkOrange = Color(0xFFFF6B00)
