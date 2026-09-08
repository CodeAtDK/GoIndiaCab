package com.example.goindiacab.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.outfit_bold
import goindiacab.app.shared.generated.resources.outfit_extrabold
import goindiacab.app.shared.generated.resources.outfit_medium
import goindiacab.app.shared.generated.resources.outfit_regular
import goindiacab.app.shared.generated.resources.outfit_semibold
import goindiacab.app.shared.generated.resources.dmsans_regular
import org.jetbrains.compose.resources.Font

@Composable
fun outfitFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.outfit_regular, FontWeight.Normal),
        Font(Res.font.outfit_medium, FontWeight.Medium),
        Font(Res.font.outfit_semibold, FontWeight.SemiBold),
        Font(Res.font.outfit_bold, FontWeight.Bold),
        Font(Res.font.outfit_extrabold, FontWeight.ExtraBold),
    )
}

@Composable
fun dmSansFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.dmsans_regular, FontWeight.Normal),
        Font(Res.font.dmsans_regular, FontWeight.SemiBold),
        Font(Res.font.dmsans_regular, FontWeight.Bold)
    )
}

@Composable
fun getGoIndiaCabTypography(): Typography {
    val fontFamily = outfitFontFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.5).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 26.sp
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 20.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    )
}
