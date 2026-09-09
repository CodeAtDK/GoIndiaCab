package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.di.AppContainer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.PaymentFailureReason
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.viewmodel.PaymentUiState
import goindiacab.app.shared.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private val ColorNavyDark = Color(0xFF091E42)
private val ColorNavyMedium = Color(0xFF0D2554)
private val ColorOrangePrimary = Color(0xFFFF6B00)
private val ColorOrangeLight = Color(0xFFFF8B38)
private val ColorSecurityGreen = Color(0xFF10B981)

/**
 * Screen 33: Payment Processing
 * High-fidelity production screen with sweep gradient radar spinner,
 * dynamic SSL handshake step ticker, RBI & PCI-DSS compliance assurance,
 * and seamless ViewModel state handling.
 */
@Composable
fun PaymentProcessingScreen(
    viewModel: BookingFlowViewModel,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val paymentUiState by viewModel.paymentUiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    val advanceAmount = session.fareBreakdown.advanceDepositAmount
    val bookingId = session.bookingId.ifBlank { "GIC-849201" }

    // Intercept hardware back button to show safety dialog
    PlatformBackHandler(enabled = true) {
        showCancelDialog = true
    }

    // Step ticker progression animation
    var currentStepIndex by remember { mutableStateOf(0) }
    val stepMessages = remember {
        listOf(
            "Establishing 256-bit SSL encrypted tunnel...",
            "Connecting to Bank Payment Gateway...",
            "Authorizing payment with issuing bank...",
            "Finalizing instant confirmation..."
        )
    }

    LaunchedEffect(Unit) {
        // Trigger payment processing in ViewModel if not already processing
        if (paymentUiState !is PaymentUiState.Processing) {
            viewModel.processPayment()
        }

        // Rotate through informative security steps
        for (i in 0 until stepMessages.size) {
            currentStepIndex = i
            delay(1100)
        }
    }

    // Observe payment state transitions
    LaunchedEffect(paymentUiState) {
        when (paymentUiState) {
            is PaymentUiState.Success -> {
                delay(600) // Brief pause so user perceives success
                onPaymentSuccess()
            }
            is PaymentUiState.Error -> {
                delay(400)
                onPaymentFailed()
            }
            else -> {}
        }
    }

    // Continuous sweep spinner rotation
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(ColorNavyDark, ColorNavyMedium, Color(0xFF06142E))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with discreet close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showCancelDialog = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Cancel",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Booking ID Pill Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Booking #$bookingId",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.weight(0.7f))

            // Centerpiece: Glowing Circular Sweep Spinner
            Box(
                modifier = Modifier
                    .size(190.dp),
                contentAlignment = Alignment.Center
            ) {
                // Subtle glowing background halo
                Box(
                    modifier = Modifier
                        .size(170.dp * pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ColorOrangePrimary.copy(alpha = 0.18f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Background track ring
                Canvas(modifier = Modifier.size(136.dp)) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 8.dp.toPx())
                    )
                }

                // Animated Sweep Gradient Progress Ring
                Canvas(
                    modifier = Modifier.size(136.dp)
                ) {
                    val strokeWidth = 8.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeftOffset = Offset(strokeWidth / 2, strokeWidth / 2)

                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                ColorOrangePrimary.copy(alpha = 0.05f),
                                ColorOrangeLight,
                                ColorOrangePrimary
                            )
                        ),
                        startAngle = rotationAngle,
                        sweepAngle = 290f,
                        useCenter = false,
                        topLeft = topLeftOffset,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Inner Badge with Indian Rupee Icon
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₹",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Amount being processed
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorOrangePrimary.copy(alpha = 0.15f))
                    .border(1.dp, ColorOrangePrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Advance Deposit: ₹$advanceAmount",
                    color = ColorOrangeLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Status Header
            Text(
                text = "PROCESSING PAYMENT...",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Warning
            Text(
                text = "Please do not press back or close the app",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Step Handshake Ticker
            AnimatedContent(
                targetState = currentStepIndex,
                label = "step_ticker"
            ) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ColorSecurityGreen)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stepMessages.getOrElse(index) { stepMessages.last() },
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Security & Regulatory Trust Badge Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_shield_check),
                            contentDescription = null,
                            tint = ColorSecurityGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "256-Bit SSL Bank-Grade Encryption",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "100% RBI Compliant • PCI-DSS Certified Gateway",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Accepted network badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NetworkBadge(name = "UPI")
                        NetworkBadge(name = "RuPay")
                        NetworkBadge(name = "VISA")
                        NetworkBadge(name = "Mastercard")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QA Simulation helper (discreet button to test failure path)
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QA: Simulate Bank Failure",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    modifier = Modifier.clickable {
                        viewModel.setSimulatePaymentFailure(true, PaymentFailureReason.BANK_TIMEOUT)
                        viewModel.retryPayment()
                    }
                )
            }
        }
    }

    // Cancellation Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "Payment in Progress",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Your bank transaction is currently being processed. If you leave now, the transaction might fail or require a refund within 2-3 business days.",
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCancelDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorOrangePrimary)
                ) {
                    Text("Wait for Payment", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onPaymentFailed()
                    }
                ) {
                    Text("Cancel Transaction", color = Color(0xFFDC2626))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun NetworkBadge(name: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = name,
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PaymentProcessingScreenPreview() {
    GoIndiaCabTheme {
        PaymentProcessingScreen(
            viewModel = AppContainer.createBookingFlowViewModel(),
            onPaymentSuccess = {},
            onPaymentFailed = {}
        )
    }
}
