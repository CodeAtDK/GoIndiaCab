package com.example.goindiacab.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.AppLogoIcon
import com.example.goindiacab.components.BrandTextLogo
import com.example.goindiacab.theme.*
import kotlinx.coroutines.delay

@Composable
fun SessionCheckScreen(
    onSessionVerified: () -> Unit = {}
) {
    // Auto-advance to onboarding after checking session
    LaunchedEffect(Unit) {
        delay(1800L)
        onSessionVerified()
    }

    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    AdaptiveContainer(
        backgroundBrush = SplashGradient
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onSessionVerified() }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Brand Header Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppLogoIcon(
                    size = 96.dp,
                    cornerRadius = 26.dp,
                    iconSize = 50.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                BrandTextLogo(
                    isLightOnDark = true,
                    fontSize = 40.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your Ride, Your Way",
                    color = TextWhiteSecondary,
                    fontSize = 17.sp,
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Custom Two-Tone Spinner
                Canvas(modifier = Modifier.size(44.dp)) {
                    val strokeWidth = 4.dp.toPx()

                    drawCircle(
                        color = Color(0xFF1E3A8A).copy(alpha = 0.6f),
                        style = Stroke(width = strokeWidth)
                    )

                    drawArc(
                        color = BrandOrange,
                        startAngle = rotation,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Securing connection...",
                    color = Color(0xFFE6F0FF),
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 0.sp,
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Normal
                )
            }

            // Bottom Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "100% Verified Drivers • Safe & Secure",
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 0.sp,
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview
@Composable
fun SessionCheckScreenPreview() {
    GoIndiaCabTheme {
        SessionCheckScreen()
    }
}
