package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.*
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.data.models.MilestoneStatus
import goindiacab.app.shared.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private val ColorNavy = Color(0xFF0D1E3A)
private val ColorBlue = Color(0xFF0052CC)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)
private val ColorCardBorder = Color(0xFFE5E7EB)

/**
 * Screen 33 / Multi-Stage: Verify Payment & Partner OTP Authentication.
 * Reimplemented strictly matching payment-otp-verification.svg, screen-1.svg, and screen-2.svg.
 * Shows only the single active advance payment milestone currently due for OTP verification.
 */
@Composable
fun PaymentOtpVerificationScreen(
    viewModel: BookingFlowViewModel = remember { AppContainer.createBookingFlowViewModel() },
    onPaymentVerified: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val schedule by viewModel.paymentSchedule.collectAsState()

    val activeMilestone = schedule.milestones.firstOrNull { it.status == MilestoneStatus.DUE }
        ?: schedule.milestones.firstOrNull { it.status == MilestoneStatus.UPCOMING }
        ?: schedule.milestones.firstOrNull()

    val milestoneAmount = activeMilestone?.amount ?: 4800
    val milestoneTitle = activeMilestone?.title ?: "Trip Start Advance Payment (40%)"
    val dueIndex = schedule.milestones.indexOfFirst { it.id == activeMilestone?.id }.let { if (it >= 0) it + 1 else 2 }
    val totalCount = schedule.milestones.size.coerceAtLeast(4)
    val progressLabel = "Progress: $dueIndex of $totalCount advance payments verified"

    var countdownSeconds by remember { mutableStateOf(600) } // 10 minutes
    var isResent by remember { mutableStateOf(false) }

    // 10-minute OTP countdown timer
    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val minutes = countdownSeconds / 60
    val seconds = countdownSeconds % 60
    val timerString = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundColor = ColorBg
    ) {
        Scaffold(
            containerColor = ColorBg,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ColorNavy
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(60.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_back_arrow),
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Text(
                            text = "Verify Payment",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        )

                        IconButton(onClick = { /* menu */ }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_more_vert),
                                contentDescription = "More",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                            colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                        ) {
                            Text(
                                text = "Confirm & Proceed",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )
                        }

                        Text(
                            text = progressLabel,
                            fontSize = 12.5.sp,
                            fontFamily = dmSansFontFamily(),
                            color = ColorTextSecondary
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Dark Navy "VERIFYING AMOUNT" Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ColorNavy,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "VERIFYING AMOUNT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "₹$milestoneAmount",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White
                        )

                        Text(
                            text = milestoneTitle,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFF34D399)
                        )
                    }
                }

                // 2. Multi-Stop Route Summary Card (Matching screen-1.svg & screen-2.svg)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (session.dropLocation.isNotBlank()) "${session.pickupLocation.ifBlank { "Delhi" }} ➔ ${session.dropLocation} Multi-Stop" else schedule.routeSummary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = ColorTextPrimary
                            )
                            Text(
                                text = schedule.durationAndDistance,
                                fontSize = 12.5.sp,
                                fontFamily = dmSansFontFamily(),
                                color = ColorTextSecondary
                            )
                        }

                        Text(
                            text = if (session.fareBreakdown.totalEstimatedFare > 0) "Total: ₹${session.fareBreakdown.totalEstimatedFare}" else "Total: ₹${schedule.totalAmount}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = ColorBlue
                        )
                    }
                }

                // 3. OTP Digit Entry Boxes Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "Share this OTP with your partner to confirm payment received:",
                            fontSize = 14.sp,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFF374151),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        // 4 Large OTP Digit Display Boxes [ 7 ] [ 4 ] [ 2 ] [ 9 ]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val otpDigits = listOf("7", "4", "2", "9")
                            otpDigits.forEach { digit ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 58.dp, height = 64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = outfitFontFamily(),
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "OTP valid for $timerString minutes",
                            fontSize = 13.sp,
                            fontFamily = dmSansFontFamily(),
                            color = ColorTextSecondary
                        )
                    }
                }

                // 4. Info Banner: "Partner will verify payment via OTP."
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_shield_check),
                            contentDescription = null,
                            tint = ColorBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Partner will verify payment via OTP.",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                // 5. Assigned Partner / Driver Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_nav_home),
                                contentDescription = null,
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rajesh Kumar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = ColorTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Toyota Innova Crysta • DL 01 AB 1234",
                                fontSize = 12.5.sp,
                                fontFamily = dmSansFontFamily(),
                                color = ColorTextSecondary
                            )
                        }
                    }
                }

                // 6. Resend OTP Link Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isResent = true
                            countdownSeconds = 600
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isResent) "OTP Resent Successfully ✓" else "Resend OTP via SMS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = dmSansFontFamily(),
                        color = if (isResent) Color(0xFF059669) else ColorBlue
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun PaymentOtpVerificationScreenPreview() {
    GoIndiaCabTheme {
        PaymentOtpVerificationScreen()
    }
}
