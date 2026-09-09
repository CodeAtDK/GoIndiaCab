package com.example.goindiacab.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import goindiacab.app.shared.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private val SplashDarkBg = Color(0xFF06101E)
private val AccentOrange = Color(0xFFF97316)
private val TextMuted = Color(0xFF94A3B8)

/**
 * Screen 1: Popular Getaways Splash Screen (splash-screen.svg) with Zoom-In Transition.
 *
 * Features:
 * - 4 Destination showcase cards (Goa, Manali, Jaipur, Kerala).
 * - Central GoIndiaCab emblem with orange brand accent.
 * - Popular Getaways headline with tagline.
 * - Trust badge footer ("100% Verified Drivers • Safe & Secure").
 * - Cinematic 3D Zoom-In transition on exit towards Onboarding.
 */
@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit = {}
) {
    var isExiting by remember { mutableStateOf(false) }
    val zoomScale = remember { Animatable(1f) }
    val centerScale = remember { Animatable(1f) }
    val contentAlpha = remember { Animatable(1f) }
    val cardsSeparation = remember { Animatable(0f) }

    fun triggerExit() {
        if (!isExiting) {
            isExiting = true
        }
    }

    // Auto-advance trigger
    LaunchedEffect(Unit) {
        delay(2300L)
        triggerExit()
    }

    // Cinematic Zoom-In Animation upon exit
    LaunchedEffect(isExiting) {
        if (isExiting) {
            launch {
                zoomScale.animateTo(
                    targetValue = 1.85f,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
            launch {
                centerScale.animateTo(
                    targetValue = 2.4f,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
            launch {
                cardsSeparation.animateTo(
                    targetValue = 40f,
                    animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
                )
            }
            launch {
                contentAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 500, delayMillis = 100, easing = LinearEasing)
                )
            }
            delay(580L)
            onNavigateNext()
        }
    }

    AdaptiveContainer(
        backgroundColor = SplashDarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { triggerExit() }
                .graphicsLayer {
                    scaleX = zoomScale.value
                    scaleY = zoomScale.value
                    alpha = contentAlpha.value
                }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Section: First 2 Destination Cards (Goa & Manali)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = -cardsSeparation.value
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DestinationCard(
                        drawable = Res.drawable.img_card_goa,
                        contentDescription = "Goa Destination",
                        modifier = Modifier.weight(1f)
                    )
                    DestinationCard(
                        drawable = Res.drawable.img_card_manali,
                        contentDescription = "Manali Destination",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Center Section: Brand Identity Emblem & Popular Getaways (Dramatic focal zoom)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .graphicsLayer {
                        val focal = centerScale.value / zoomScale.value
                        scaleX = focal
                        scaleY = focal
                    }
            ) {
                AppLogoIcon(
                    size = 80.dp,
                    cornerRadius = 22.dp,
                    iconSize = 44.dp
                )

                Spacer(modifier = Modifier.height(18.dp))

                BrandTextLogo(
                    isLightOnDark = true,
                    fontSize = 38.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Orange Accent Line
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AccentOrange)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "POPULAR GETAWAYS",
                    color = AccentOrange,
                    fontSize = 11.sp,
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Top Destinations to Explore",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Bottom Destination Cards: Jaipur & Kerala
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = cardsSeparation.value
                    },
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DestinationCard(
                    drawable = Res.drawable.img_card_jaipur,
                    contentDescription = "Jaipur Destination",
                    modifier = Modifier.weight(1f)
                )
                DestinationCard(
                    drawable = Res.drawable.img_card_kerala,
                    contentDescription = "Kerala Destination",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Footer Section: Tagline & Trust Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your Ride, Your Way",
                    color = Color(0xFFE2E8F0),
                    fontSize = 16.sp,
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "100% Verified Drivers • Safe & Secure",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DestinationCard(
    drawable: org.jetbrains.compose.resources.DrawableResource,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.55f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.6f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(
                    Color(0xFF334155).copy(alpha = 0.6f),
                    Color(0xFF1E293B).copy(alpha = 0.4f)
                )
            )
        )
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    GoIndiaCabTheme {
        SplashScreen()
    }
}
