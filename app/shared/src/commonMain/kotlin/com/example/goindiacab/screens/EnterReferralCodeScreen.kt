package com.example.goindiacab.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val NavyDark = Color(0xFF0A1A3A)
private val ScreenBg = Color(0xFFF4F6F9)
private val BrandOrange = Color(0xFFFF6B00)
private val BrandBlue = Color(0xFF0052CC)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

/**
 * Screen allowing passengers to input and claim an invite/referral code.
 * Accessible from LoginScreen ("Have a Referral Code?"), ReferEarnScreen, and Profile.
 */
@Composable
fun EnterReferralCodeScreen(
    onBackClick: () -> Unit = {},
    onCodeAppliedSuccess: (code: String, rewardAmount: Int) -> Unit = { _, _ -> },
    onSkipClick: () -> Unit = {}
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    var referralCode by remember { mutableStateOf("") }
    var validationState by remember { mutableStateOf<ReferralValidationState>(ReferralValidationState.Idle) }
    var isApplying by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val validDemoCodes = listOf("GOINDIA250", "DHRUVA500", "WELCOME50", "TRAVEL100", "CAB250")

    fun validateAndApply() {
        val trimmed = referralCode.trim().uppercase()
        if (trimmed.isBlank()) {
            validationState = ReferralValidationState.Error("Please enter a referral code")
            return
        }

        isApplying = true
        focusManager.clearFocus()

        // Simulating validation
        if (trimmed.length >= 4 && (validDemoCodes.contains(trimmed) || trimmed.startsWith("GIC") || trimmed.endsWith("500") || trimmed.endsWith("250"))) {
            val amount = if (trimmed.contains("500")) 500 else 250
            validationState = ReferralValidationState.Success(
                message = "₹$amount Referral Bonus Unlocked!",
                rewardAmount = amount
            )
            isApplying = false
        } else {
            validationState = ReferralValidationState.Error("Invalid or expired referral code. Please check and try again.")
            isApplying = false
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        ChevronRightIcon(
                            size = 18.dp,
                            color = Color.White,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Enter Referral Code",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onSkipClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Hero Graphic Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(BrandOrange.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_gift),
                    contentDescription = null,
                    tint = BrandOrange,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Have an Invite Code?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter the 6-10 character code shared by your friend to get up to ₹500 discount on your first outstation ride.",
                fontSize = 13.5.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Input Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "REFERRAL / PROMO CODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.5.dp,
                                color = when (validationState) {
                                    is ReferralValidationState.Success -> SuccessGreen
                                    is ReferralValidationState.Error -> ErrorRed
                                    else -> if (referralCode.isNotEmpty()) BrandBlue else BorderColor
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color(0xFFFAFAFA))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = referralCode,
                            onValueChange = { input ->
                                referralCode = input.uppercase().filter { it.isLetterOrDigit() }.take(12)
                                if (validationState !is ReferralValidationState.Idle) {
                                    validationState = ReferralValidationState.Idle
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark,
                                letterSpacing = 2.sp
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(BrandBlue),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { validateAndApply() }
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (referralCode.isEmpty()) {
                                    Text(
                                        text = "e.g. GOINDIA250",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = outfitFontFamily(),
                                        color = Color(0xFFA0AEC0),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                innerTextField()
                            }
                        )

                        if (referralCode.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    referralCode = ""
                                    validationState = ReferralValidationState.Idle
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                CloseIcon(size = 16.dp, color = TextMuted)
                            }
                        }
                    }

                    // Validation Feedback Banner
                    AnimatedVisibility(visible = validationState !is ReferralValidationState.Idle) {
                        when (val state = validationState) {
                            is ReferralValidationState.Success -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFECFDF5),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = "✓", color = SuccessGreen, fontWeight = FontWeight.ExtraBold)
                                        Text(
                                            text = "${state.message} Claim now to apply to your wallet.",
                                            fontSize = 12.5.sp,
                                            fontFamily = dmSansFontFamily(),
                                            color = Color(0xFF065F46),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            is ReferralValidationState.Error -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = "✕", color = ErrorRed, fontWeight = FontWeight.ExtraBold)
                                        Text(
                                            text = state.errorMessage,
                                            fontSize = 12.5.sp,
                                            fontFamily = dmSansFontFamily(),
                                            color = Color(0xFF991B1B),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            else -> {}
                        }
                    }

                    // Action Button
                    Button(
                        onClick = {
                            if (validationState is ReferralValidationState.Success) {
                                val amount = (validationState as ReferralValidationState.Success).rewardAmount
                                onCodeAppliedSuccess(referralCode, amount)
                            } else {
                                validateAndApply()
                            }
                        },
                        enabled = referralCode.isNotBlank() && !isApplying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (validationState is ReferralValidationState.Success) SuccessGreen else BrandOrange
                        )
                    ) {
                        Text(
                            text = if (validationState is ReferralValidationState.Success) "Claim Reward & Continue" else "Apply Referral Code",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Referral Policy & Guidelines
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "HOW IT WORKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    ReferralRuleBullet(
                        number = "1",
                        text = "Referral code is applicable only on your very first intercity booking."
                    )
                    ReferralRuleBullet(
                        number = "2",
                        text = "Bonus reward is credited directly into your GoIndiaCab Wallet once verified."
                    )
                    ReferralRuleBullet(
                        number = "3",
                        text = "Your friend will also earn rewards after you complete your first ride!"
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ReferralRuleBullet(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(BrandOrange.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = BrandOrange
            )
        }

        Text(
            text = text,
            fontSize = 12.5.sp,
            fontFamily = dmSansFontFamily(),
            color = TextDark,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

sealed class ReferralValidationState {
    object Idle : ReferralValidationState()
    data class Success(val message: String, val rewardAmount: Int) : ReferralValidationState()
    data class Error(val errorMessage: String) : ReferralValidationState()
}

@Preview
@Composable
fun EnterReferralCodeScreenPreview() {
    GoIndiaCabTheme {
        EnterReferralCodeScreen()
    }
}
