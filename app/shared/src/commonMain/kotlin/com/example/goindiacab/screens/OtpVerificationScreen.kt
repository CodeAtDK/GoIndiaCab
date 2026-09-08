package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.InteractiveOtpInput
import com.example.goindiacab.components.PrimaryButton
import com.example.goindiacab.components.ScreenTopBar
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_clock
import org.jetbrains.compose.resources.painterResource

enum class OtpScreenMode {
    NORMAL,
    EXPIRED,
    ERROR
}

@Composable
fun OtpVerificationScreen(
    phoneNumber: String = "+91 98765 43210",
    initialMode: OtpScreenMode = OtpScreenMode.NORMAL,
    onBackClick: () -> Unit = {},
    onEditPhoneClick: () -> Unit = {},
    onVerifySuccess: () -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    var mode by remember { mutableStateOf(initialMode) }
    var otpValue by remember {
        mutableStateOf(
            when (initialMode) {
                OtpScreenMode.NORMAL -> "487"
                OtpScreenMode.ERROR -> "1209"
                OtpScreenMode.EXPIRED -> ""
            }
        )
    }
    val focusManager = LocalFocusManager.current

    AdaptiveContainer(backgroundColor = BackgroundWhite) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with accessible Back Button
            ScreenTopBar(
                title = "Verify OTP",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Heading based on Mode (Verify OTP, OTP Expired, Verification Failed)
                val titleText = when (mode) {
                    OtpScreenMode.NORMAL -> "Verify OTP"
                    OtpScreenMode.EXPIRED -> "OTP Expired"
                    OtpScreenMode.ERROR -> "Verification Failed"
                }

                Text(
                    text = titleText,
                    fontSize = 28.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark,
                    letterSpacing = 0.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle based on Mode with DM Sans typography (14px, 140% line height)
                when (mode) {
                    OtpScreenMode.NORMAL -> {
                        val subtitle = buildAnnotatedString {
                            append("We sent a 4-digit OTP to ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = dmSansFontFamily(),
                                    fontSize = 14.sp,
                                    letterSpacing = 0.sp,
                                    color = TextMuted
                                )
                            ) {
                                append(phoneNumber)
                            }
                            append(" ")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = subtitle,
                                fontSize = 14.sp,
                                lineHeight = 19.6.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = dmSansFontFamily(),
                                letterSpacing = 0.sp,
                                color = TextMuted
                            )
                            Box(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onEditPhoneClick() }
                                    .padding(vertical = 4.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    text = "Edit",
                                    fontSize = 14.sp,
                                    lineHeight = 19.6.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = dmSansFontFamily(),
                                    letterSpacing = 0.sp,
                                    color = BrandOrange,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        }
                    }

                    OtpScreenMode.EXPIRED -> {
                        val subtitle = buildAnnotatedString {
                            append("OTP code sent to ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = dmSansFontFamily(),
                                    fontSize = 14.sp,
                                    letterSpacing = 0.sp,
                                    color = TextMuted
                                )
                            ) {
                                append(phoneNumber)
                            }
                            append(" has expired. Tap below to resend.")
                        }
                        Text(
                            text = subtitle,
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = dmSansFontFamily(),
                            letterSpacing = 0.sp,
                            color = TextMuted,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OtpScreenMode.ERROR -> {
                        Text(
                            text = "Entered OTP is invalid. Please check the code or resend.",
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = dmSansFontFamily(),
                            letterSpacing = 0.sp,
                            color = TextMuted,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Interactive 4 OTP Boxes with real keyboard integration
                InteractiveOtpInput(
                    otpValue = otpValue,
                    onOtpChange = {
                        otpValue = it
                        if (mode == OtpScreenMode.ERROR) {
                            mode = OtpScreenMode.NORMAL
                        }
                    },
                    isError = mode == OtpScreenMode.ERROR,
                    isExpired = mode == OtpScreenMode.EXPIRED,
                    onComplete = {
                        focusManager.clearFocus()
                        onVerifySuccess()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Middle Notice / Error / Banner
                when (mode) {
                    OtpScreenMode.NORMAL -> {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Didn't receive OTP? Resend in ",
                                fontSize = 14.sp,
                                fontFamily = outfitFontFamily(),
                                color = TextMuted
                            )
                            Text(
                                text = "00:28",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = BrandBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "We will auto-read OTP once received. Please do not share this OTP with anyone.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontFamily = outfitFontFamily(),
                            color = TextSubtle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    OtpScreenMode.EXPIRED -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BadgePeachBg)
                                .border(1.dp, BadgePeachBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_clock),
                                contentDescription = null,
                                tint = BrandOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "OTP expired. Tap to resend.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = outfitFontFamily(),
                                color = BrandOrange
                            )
                        }
                    }

                    OtpScreenMode.ERROR -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Invalid OTP. Please try again.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = ErrorRed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "2 attempts remaining",
                                fontSize = 13.sp,
                                fontFamily = outfitFontFamily(),
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Primary Action Button
                val buttonText = when (mode) {
                    OtpScreenMode.NORMAL -> "Verify & Proceed"
                    OtpScreenMode.EXPIRED -> "Resend OTP"
                    OtpScreenMode.ERROR -> "Try Again"
                }

                PrimaryButton(
                    text = buttonText,
                    backgroundColor = BrandOrange,
                    onClick = {
                        focusManager.clearFocus()
                        when (mode) {
                            OtpScreenMode.NORMAL -> onVerifySuccess()
                            OtpScreenMode.EXPIRED -> {
                                mode = OtpScreenMode.NORMAL
                                otpValue = ""
                                onResendClick()
                            }
                            OtpScreenMode.ERROR -> {
                                mode = OtpScreenMode.NORMAL
                                otpValue = ""
                            }
                        }
                    }
                )

                // Resend Link for Error Mode
                if (mode == OtpScreenMode.ERROR) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn't get the code? ",
                            fontSize = 14.sp,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted
                        )
                        Box(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    mode = OtpScreenMode.NORMAL
                                    otpValue = ""
                                    onResendClick()
                                }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "Resend code",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = BrandOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview
@Composable
fun OtpVerificationNormalPreview() {
    GoIndiaCabTheme {
        OtpVerificationScreen(
            phoneNumber = "+91 98765 43210",
            initialMode = OtpScreenMode.NORMAL
        )
    }
}

@Preview
@Composable
fun OtpVerificationExpiredPreview() {
    GoIndiaCabTheme {
        OtpVerificationScreen(
            phoneNumber = "+91 98765 43210",
            initialMode = OtpScreenMode.EXPIRED
        )
    }
}

@Preview
@Composable
fun OtpVerificationErrorPreview() {
    GoIndiaCabTheme {
        OtpVerificationScreen(
            phoneNumber = "+91 98765 43210",
            initialMode = OtpScreenMode.ERROR
        )
    }
}
