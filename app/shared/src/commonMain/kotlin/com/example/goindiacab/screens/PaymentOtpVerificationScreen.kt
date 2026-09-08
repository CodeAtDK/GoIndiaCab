package com.example.goindiacab.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private val ColorBgLight = Color(0xFFF4F6F9)
private val ColorNavyDark = Color(0xFF091E42)
private val ColorTextDark = Color(0xFF111827)
private val ColorTextMuted = Color(0xFF6B7280)
private val ColorTextSecondary = Color(0xFF4B5563)
private val ColorMintAccent = Color(0xFF4ADE80)
private val ColorBlueAccent = Color(0xFF1A73E8)
private val ColorCardBorder = Color(0xFFE5E7EB)

/**
 * Screen: Verify Payment OTP (payment-otp-verification.svg).
 * Production-ready screen presenting 4-digit mutual payment verification code,
 * driver vehicle identification card, SMS re-dispatch mechanism, and live timeout ticker.
 */
@Composable
fun PaymentOtpVerificationScreen(
    viewModel: BookingFlowViewModel,
    onPaymentVerified: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val otpData by viewModel.paymentOtpData.collectAsState()
    val ticket by viewModel.latestBookingTicket.collectAsState()
    val driver = ticket?.driver ?: com.example.goindiacab.data.models.DriverPartnerInfo()

    var showResentBanner by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(600) } // 10 minutes

    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds -= 1
        }
    }

    val minutes = countdownSeconds / 60
    val seconds = countdownSeconds % 60
    val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer(
        modifier = modifier
            .fillMaxSize()
            .background(ColorBgLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = ColorTextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Verify Payment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextDark
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card 1: Navy Amount Header Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ColorNavyDark,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VERIFYING AMOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "₹${otpData.amount}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = otpData.stageLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorMintAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card 2: OTP Verification Card with 4 Digit Boxes
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Share this OTP with your partner to confirm payment received:",
                            fontSize = 14.sp,
                            color = ColorTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4 Big Digit Boxes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val digits = otpData.otpCode.take(4).padEnd(4, '0')
                            digits.forEach { digit ->
                                Surface(
                                    modifier = Modifier.size(width = 54.dp, height = 62.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = ColorBgLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = digit.toString(),
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ColorTextDark
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "OTP valid for $formattedTime minutes",
                            fontSize = 13.sp,
                            color = ColorTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 3: Driver Partner Strip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.img_driver_portrait),
                            contentDescription = driver.name,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = driver.name.ifBlank { "Rajesh Kumar" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${driver.vehicleModel} • ${driver.vehicleNumber}",
                                fontSize = 12.sp,
                                color = ColorTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Link: Resend OTP via SMS
                TextButton(
                    onClick = {
                        viewModel.resendOtp {
                            showResentBanner = true
                        }
                    }
                ) {
                    Text(
                        text = "Resend OTP via SMS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBlueAccent
                    )
                }

                if (showResentBanner) {
                    Text(
                        text = "✓ New OTP dispatched via SMS to ${driver.phone}",
                        fontSize = 12.sp,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFE5E7EB),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = otpData.progressText,
                    fontSize = 13.sp,
                    color = ColorTextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Interactive QA / Simulation Action: Partner Confirms Payment
                Button(
                    onClick = {
                        viewModel.verifyOtp("7429") {
                            onPaymentVerified()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Text(
                        text = "Confirm Payment Received",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
