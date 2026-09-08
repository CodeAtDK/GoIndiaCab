package com.example.goindiacab.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.goindiacab.components.PillBadge
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.components.PrimaryButton
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_onboarding_driver
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

data class OnboardingSlide(
    val badge: String,
    val title: String,
    val description: String
)

private val onboardingSlides = listOf(
    OnboardingSlide(
        badge = "SAFE & RELIABLE",
        title = "Safe & Verified Drivers",
        description = "Every driver undergoes rigorous background checks and regular vehicle audits to ensure safety for you and your family."
    ),
    OnboardingSlide(
        badge = "FAST & CONVENIENT",
        title = "Quick Pickup & Live Tracking",
        description = "Get picked up in minutes with real-time GPS tracking and transparent route navigation every step of the way."
    ),
    OnboardingSlide(
        badge = "AFFORDABLE RIDES",
        title = "Transparent & Best Fares",
        description = "Enjoy premium cab rides across India with upfront pricing, zero hidden charges, and exclusive welcome discounts."
    )
)

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { onboardingSlides.size })
    val coroutineScope = rememberCoroutineScope()

    // Handle back button on Onboarding pages
    PlatformBackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    AdaptiveContainer(backgroundColor = BackgroundWhite) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isScrollable = maxHeight < 680.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .then(if (isScrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Skip Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onGetStartedClick() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted
                        )
                    }
                }

                // Top flexible space: lowers the image and centers the block
                if (isScrollable) {
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 16.dp)
                    )
                }

                // Swipeable Image Carousel (3 slides)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = Color.Black.copy(alpha = 0.06f),
                                spotColor = Color.Black.copy(alpha = 0.08f)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_onboarding_driver),
                            contentDescription = onboardingSlides[page].title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Exact gap: 32px between Image and Text content
                Spacer(modifier = Modifier.height(32.dp))

                // Text Content that transitions smoothly with the active slide
                AnimatedContent(
                    targetState = pagerState.currentPage,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { currentPage ->
                    val slide = onboardingSlides[currentPage]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PillBadge(text = slide.badge)

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = slide.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = slide.description,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                // Bottom flexible space balancing the top space to center content between top and button
                if (isScrollable) {
                    Spacer(modifier = Modifier.height(28.dp))
                } else {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 28.dp)
                    )
                }

                // Footer Section (Dots + 24px gap + Button + 24px bottom padding)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 3 Slide Indicators (Interactive, click or swipe)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(onboardingSlides.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            val dotWidth by animateDpAsState(
                                targetValue = if (isSelected) 24.dp else 8.dp,
                                animationSpec = tween(durationMillis = 250)
                            )
                            val dotColor = if (isSelected) BrandBlue else BorderGray

                            Box(
                                modifier = Modifier
                                    .width(dotWidth)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(dotColor)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                            )

                            if (index < onboardingSlides.size - 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    // Exact gap: 24px between Dots and Button
                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Action Button (width: 354, height: 54, rx: 14)
                    PrimaryButton(
                        text = if (pagerState.currentPage == onboardingSlides.lastIndex) "Get Started" else "Next",
                        backgroundColor = BrandBlue,
                        height = 54.dp,
                        cornerRadius = 14.dp,
                        onClick = {
                            if (pagerState.currentPage < onboardingSlides.lastIndex) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onGetStartedClick()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    GoIndiaCabTheme {
        OnboardingScreen()
    }
}
