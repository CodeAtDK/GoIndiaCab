package com.example.goindiacab.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.OrDivider
import com.example.goindiacab.components.PrimaryButton
import com.example.goindiacab.components.SocialButton
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    onContinueClick: (phoneNumber: String) -> Unit = {},
    onSkipClick: () -> Unit = {},
    onReferralClick: () -> Unit = {},
    onTermsClick: () -> Unit = {}
) {
    var phoneNumber by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val isValidPhone = phoneNumber.length == 10

    AdaptiveContainer(backgroundColor = SurfaceGray) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenHeight = maxHeight
            val topSectionHeight = (screenHeight * 0.42f).coerceIn(300.dp, 380.dp)
            val bottomSectionHeight = screenHeight - topSectionHeight

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Section (Heritage Scenic Landscape + Promo)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topSectionHeight)
                ) {
                    // Background image
                    Image(
                        painter = painterResource(Res.drawable.bg_login_heritage),
                        contentDescription = "Heritage Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                // Dark Blue Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0052CC).copy(alpha = 0.70f),
                                    Color(0xFF091E42).copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Top Bar & Promo Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Bar (Brand Pill + Skip Button with accessible touch target)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Brand Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_cab),
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GoIndiaCab",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily()
                            )
                        }

                        // Right Skip Button with 48dp minimum accessible touch target
                        Box(
                            modifier = Modifier
                                .size(width = 68.dp, height = 48.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onSkipClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(50))
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Skip",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = outfitFontFamily()
                                )
                            }
                        }
                    }

                    // Welcome Promo Text
                    Text(
                        text = "Upto 30% OFF on your first booking as a Welcome Gift",
                        color = TextWhite,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        modifier = Modifier.padding(bottom = 20.dp, end = 24.dp)
                    )
                }
            }

            // Bottom Section (SurfaceGray with Card Centered)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = bottomSectionHeight)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Floating White Card (All 4 corners rounded 20.dp, 24.dp horizontal margin, soft shadow)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Heading
                    Text(
                        text = "Signup or Login",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter your mobile number to continue",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Phone Input Field with Indian Flag & +91
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (phoneNumber.isNotEmpty()) BrandBlue else BorderGray,
                                RoundedCornerShape(12.dp)
                            )
                            .background(BackgroundWhite)
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_india_flag),
                            contentDescription = "India",
                            modifier = Modifier
                                .width(24.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "+91",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(BorderGray)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        BasicTextField(
                            value = phoneNumber,
                            onValueChange = { input ->
                                val cleaned = input.filter { it.isDigit() }.let {
                                    if (it.startsWith("91") && it.length > 10) it.drop(2) else it
                                }.take(10)
                                phoneNumber = cleaned
                            },
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (isValidPhone) {
                                        focusManager.clearFocus()
                                        onContinueClick(phoneNumber)
                                    }
                                }
                            ),
                            cursorBrush = SolidColor(BrandBlue),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (phoneNumber.isEmpty()) {
                                    Text(
                                        text = "98765 43210",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = outfitFontFamily(),
                                        color = TextSubtle
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Continue Button
                    PrimaryButton(
                        text = "CONTINUE",
                        backgroundColor = BrandBlue,
                        enabled = isValidPhone || phoneNumber.isEmpty(),
                        onClick = {
                            focusManager.clearFocus()
                            onContinueClick(if (phoneNumber.isNotEmpty()) phoneNumber else "9876543210")
                        },
                        height = 52.dp,
                        cornerRadius = 12.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OrDivider()

                    Spacer(modifier = Modifier.height(18.dp))

                    // Social Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SocialButton(
                            icon = painterResource(Res.drawable.ic_social_google),
                            text = "Google",
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            height = 48.dp,
                            cornerRadius = 12.dp
                        )

                        SocialButton(
                            icon = painterResource(Res.drawable.ic_social_apple),
                            text = "Apple",
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            height = 48.dp,
                            cornerRadius = 12.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Referral Code Link (Left aligned)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onReferralClick() }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Have a Referral Code?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = outfitFontFamily(),
                            color = LinkBlue,
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Terms & Policy (Left aligned, clickable)
                    Text(
                        text = "By proceeding, you agree to GoIndiaCab's Privacy Policy, User Agreement, T&Cs",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = outfitFontFamily(),
                        color = TextSubtle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTermsClick() }
                            .padding(bottom = 6.dp)
                    )
                }
            }
        }
    }
}
}
}

@Preview
@Composable
fun LoginScreenPreview() {
    GoIndiaCabTheme {
        LoginScreen()
    }
}
