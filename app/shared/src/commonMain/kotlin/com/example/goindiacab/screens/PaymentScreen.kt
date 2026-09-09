package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.di.AppContainer

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.PaymentMethodType
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.viewmodel.CabDetailAction
import com.example.goindiacab.viewmodel.PaymentUiState
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorNavy = Color(0xFF0D1E3A)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorBlue = Color(0xFF0052CC)
private val ColorGreen = Color(0xFF10B981)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)

/**
 * Screen 32: Pay Advance (10%) & Payment Options (Phase 6).
 *
 * Architecture & Lifecycle Role:
 * - Collects the 10% advance deposit to secure the vehicle and driver dispatch.
 * - Supports Google Pay, PhonePe, Paytm, Credit/Debit Cards, NetBanking, and Cash on Pickup.
 * - Navigation Flow:
 *     - "Pay Advance" pushes [PAYMENT_PROCESSING] to initiate the bank gateway handshake.
 * - Back Button Contract:
 *     - Hardware back and top back arrow invoke [onBackClick], safely returning to [BOOKING_SUMMARY].
 */
@Composable
fun PaymentScreen(
    viewModel: BookingFlowViewModel,
    onBackClick: () -> Unit,
    onProceedToProcessing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val paymentUiState by viewModel.paymentUiState.collectAsState()

    val fare = session.fareBreakdown
    val totalFare = fare.totalEstimatedFare
    val advanceTenPercent = fare.advanceDepositAmount
    val discount = fare.discountAmount
    val payNowAmount = (advanceTenPercent - discount).coerceAtLeast(500)

    val day1Amount = (totalFare * 0.40).toInt()
    val midTripAmount = (totalFare * 0.30).toInt()
    val tripEndAmount = totalFare - advanceTenPercent - day1Amount - midTripAmount

    var selectedMethod by remember { mutableStateOf(PaymentMethodType.GOOGLE_PAY) }

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundColor = ColorBg
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Navy Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorNavy)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Pay Advance (10%)",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )

                IconButton(onClick = { /* Menu */ }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_more_vert),
                        contentDescription = "More options",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Orange Exclamation Alert Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFED7AA)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_alert_circle_orange),
                            contentDescription = null,
                            tint = ColorOrange,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Pay 10% to confirm booking. Partner will be assigned after payment.",
                            fontSize = 13.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 18.sp
                        )
                    }
                }

                // Amount Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Amount Breakdown",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        AmountRow(title = "Trip Total", amount = "₹$totalFare")
                        AmountRow(title = "Advance Payment (10%)", amount = "₹$advanceTenPercent")

                        if (discount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Coupon Discount",
                                    fontSize = 14.sp,
                                    color = ColorGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "-₹$discount",
                                    fontSize = 15.sp,
                                    color = ColorGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Dotted line
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .drawBehind {
                                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    drawLine(
                                        color = Color(0xFFE5E7EB),
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        pathEffect = pathEffect,
                                        strokeWidth = 2f
                                    )
                                }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pay Now",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                            Text(
                                text = "₹$payNowAmount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorBlue
                            )
                        }
                    }
                }

                // Remaining Payment Schedule Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Remaining Payment Schedule",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        ScheduleMilestoneItem("Trip Start (Day 1)", "₹$day1Amount (40%)")
                        ScheduleMilestoneItem("Mid Trip (Day 5)", "₹$midTripAmount (30%)")
                        ScheduleMilestoneItem("Trip End", "₹$tripEndAmount (20%)")
                    }
                }

                // Preferred UPI Apps Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Preferred UPI Apps",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        UpiOptionRow(
                            name = "Google Pay",
                            isSelected = selectedMethod == PaymentMethodType.GOOGLE_PAY,
                            onSelect = { selectedMethod = PaymentMethodType.GOOGLE_PAY }
                        )
                        UpiOptionRow(
                            name = "PhonePe",
                            isSelected = selectedMethod == PaymentMethodType.PHONE_PE,
                            onSelect = { selectedMethod = PaymentMethodType.PHONE_PE }
                        )
                        UpiOptionRow(
                            name = "Paytm",
                            isSelected = selectedMethod == PaymentMethodType.PAYTM,
                            onSelect = { selectedMethod = PaymentMethodType.PAYTM }
                        )
                    }
                }

                // GoIndiaCab Wallet
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMethod = PaymentMethodType.WALLET },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = if (selectedMethod == PaymentMethodType.WALLET) {
                        CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ColorBlue), width = 1.5.dp)
                    } else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_wallet_card),
                                contentDescription = null,
                                tint = ColorNavy,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "GoIndiaCab Wallet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorTextPrimary
                            )
                        }

                        Text(
                            text = "₹1,250",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorBlue
                        )
                    }
                }

                // Trust Badge Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE5E7EB)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
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
                            text = "100% refund if no partner found within 12 hrs",
                            fontSize = 13.sp,
                            color = Color(0xFF4B5563)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sticky Bottom CTA Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.handleAction(CabDetailAction.SelectPaymentMethod(selectedMethod))
                            onProceedToProcessing()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                    ) {
                        Text(
                            text = "Pay ₹$payNowAmount & Confirm",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AmountRow(title: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = ColorTextSecondary
        )
        Text(
            text = amount,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextPrimary
        )
    }
}

@Composable
private fun ScheduleMilestoneItem(title: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_calendar_event),
                contentDescription = null,
                tint = ColorTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = ColorTextSecondary
            )
        }
        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextPrimary
        )
    }
}

@Composable
private fun UpiOptionRow(
    name: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFF0F6FF) else Color.White)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) ColorBlue else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Radio indicator
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) ColorBlue else Color(0xFF9CA3AF),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ColorBlue)
                        )
                    }
                }

                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = ColorTextPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun PaymentScreenPreview() {
    GoIndiaCabTheme {
        PaymentScreen(
            viewModel = AppContainer.createBookingFlowViewModel(),
            onBackClick = {},
            onProceedToProcessing = {}
        )
    }
}
