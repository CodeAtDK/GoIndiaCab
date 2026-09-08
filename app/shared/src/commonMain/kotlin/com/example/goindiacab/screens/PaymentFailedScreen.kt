package com.example.goindiacab.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.components.rememberPlatformToast
import com.example.goindiacab.data.models.PaymentFailureReason
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.viewmodel.PaymentUiState
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorNavy = Color(0xFF091E42)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorRedError = Color(0xFFDC2626)
private val ColorRedBg = Color(0xFFFEE2E2)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)

/**
 * Screen 34: Payment Failed (Phase 7).
 *
 * Architecture & Lifecycle Role:
 * - Displays bank transaction failure reason (insufficient funds, bank server timeout, card declined),
 *   auto-refund assurance statement (RBI 2-3 business day policy), and recovery actions.
 * - Recovery Actions:
 *     - "Retry Payment": pops failed state and relaunches [PAYMENT_PROCESSING] tunnel.
 *     - "Change Payment Method": unwinds to [PAYMENT_SCREEN] so user can choose UPI/Card.
 * - Back Button Contract:
 *     - Both hardware back and top app bar back arrow invoke [onBackClick], which triggers
 *       [popBackTo(AppScreen.PAYMENT_SCREEN)], unwinding the backstack past the transient spinner
 *       so the user is never stuck in a processing loop.
 */
@Composable
fun PaymentFailedScreen(
    viewModel: BookingFlowViewModel,
    onRetryPayment: () -> Unit,
    onChangePaymentMethod: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val failureReason by viewModel.paymentFailureReason.collectAsState()
    val paymentUiState by viewModel.paymentUiState.collectAsState()
    val toast = rememberPlatformToast()

    // Hardware & system back button support: unwind past spinner to Payment Selection
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val advanceAmount = session.fareBreakdown.advanceDepositAmount
    val bookingId = session.bookingId.ifBlank { "GIC-849201" }
    val txnId = "TXN-9842109823"

    // Error badge pop-in scale animation
    val scaleAnim = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    val failureMessage = when (val state = paymentUiState) {
        is PaymentUiState.Error -> state.message
        else -> failureReason.description
    }

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundColor = ColorBg
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = ColorTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Payment Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextPrimary
                )

                IconButton(onClick = { toast("Call 24x7 Helpline: 1800-120-CAB") }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_headset),
                        contentDescription = "Helpline",
                        tint = ColorOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Error Badge with Cross Icon
                Box(
                    modifier = Modifier
                        .scale(scaleAnim.value)
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(ColorRedBg)
                        .border(2.dp, ColorRedError.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_shield_alert),
                        contentDescription = "Error",
                        tint = ColorRedError,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Title Headline
                Text(
                    text = "Payment Failed",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorRedError
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "We couldn't process your transaction. Don't worry, your money is completely safe.",
                    fontSize = 14.sp,
                    color = ColorTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Transaction Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Amount Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Amount Attempted",
                                fontSize = 14.sp,
                                color = ColorTextSecondary
                            )
                            Text(
                                text = "₹$advanceAmount",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ColorTextPrimary
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = Color(0xFFF3F4F6)
                        )

                        // Key-Value rows
                        DetailRow(title = "Booking ID", value = bookingId)
                        Spacer(modifier = Modifier.height(10.dp))
                        DetailRow(
                            title = "Transaction Ref",
                            value = txnId,
                            isCopyable = true,
                            onCopy = { toast("Transaction ID copied to clipboard") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DetailRow(title = "Timestamp", value = "Today, 06:42 PM")

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = Color(0xFFF3F4F6)
                        )

                        // Failure Reason Pill
                        Column {
                            Text(
                                text = "Reason for Failure",
                                fontSize = 12.sp,
                                color = ColorTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorRedBg.copy(alpha = 0.6f))
                                    .border(1.dp, ColorRedError.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = failureMessage,
                                    color = ColorRedError,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // RBI Refund Assurance Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_info_circle),
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(20.dp).padding(top = 1.dp)
                        )
                        Text(
                            text = "If any money was debited from your bank account or card, it will be automatically refunded back within 2-3 business days as per RBI guidelines.",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 24x7 Customer Support Touchpoint
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .clickable { toast("Connecting to 24x7 Cab Support...") }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ColorOrange.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_headset),
                                contentDescription = null,
                                tint = ColorOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Need Help with Payment?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorTextPrimary
                            )
                            Text(
                                text = "Call 1800-120-CAB (Toll Free)",
                                fontSize = 12.sp,
                                color = ColorTextSecondary
                            )
                        }
                    }

                    Icon(
                        painter = painterResource(Res.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom Sticky Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary: Retry Payment
                    Button(
                        onClick = {
                            viewModel.setSimulatePaymentFailure(false)
                            viewModel.retryPayment()
                            onRetryPayment()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                    ) {
                        Text(
                            text = "Retry Payment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Secondary: Choose Another Payment Method
                    OutlinedButton(
                        onClick = onChangePaymentMethod,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorOrange)
                    ) {
                        Text(
                            text = "Choose Another Payment Method",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String,
    isCopyable: Boolean = false,
    onCopy: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = ColorTextSecondary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextPrimary
            )
            if (isCopyable) {
                Text(
                    text = "COPY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorOrange,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ColorOrange.copy(alpha = 0.1f))
                        .clickable { onCopy() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
