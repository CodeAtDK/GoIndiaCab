package com.example.goindiacab.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.AppLogoIcon
import com.example.goindiacab.components.BrandTextLogo
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_splash_delhi_gate
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit = {}
) {
    // Auto-advance to next screen after 2.2 seconds
    LaunchedEffect(Unit) {
        delay(2200L)
        onNavigateNext()
    }

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
                ) { onNavigateNext() }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(24.dp))

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
            }

            Spacer(modifier = Modifier.height(36.dp))

            // India Gate Landmark Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp, max = 220.dp)
                        .aspectRatio(16f / 9f)
                        .background(Color(0xFF071F4A))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_splash_delhi_gate),
                        contentDescription = "India Gate Delhi",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Footer Partner Info
                Text(
                    text = "PREMIUM RIDE PARTNER",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 0.sp,
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(6.dp))

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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    GoIndiaCabTheme {
        SplashScreen()
    }
}
