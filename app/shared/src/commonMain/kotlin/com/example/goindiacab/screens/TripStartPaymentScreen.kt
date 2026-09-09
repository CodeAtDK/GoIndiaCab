package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.di.AppContainer

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
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorNavyDark = Color(0xFF020C1B)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorBlue = Color(0xFF1A73E8)
private val ColorGreen = Color(0xFF10B981)
private val ColorGreenDark = Color(0xFF047857)
private val ColorGreenBg = Color(0xFFECFDF5)
private val ColorGreenBorder = Color(0xFFA7F3D0)
private val ColorBgLight = Color(0xFFF4F6F9)
private val ColorTextDark = Color(0xFF111827)
private val ColorTextMuted = Color(0xFF6B7280)
private val ColorCardBorder = Color(0xFFE5E7EB)

/**
 * Screen: Pay Trip Start (40%) (trip-start-payment.svg).
 * Official screen for paying the 40% Trip Start milestone with UPI / Wallet options
 * and "Pay ₹8,000 & Confirm" action button.
 */
@Composable
fun TripStartPaymentScreen(
    viewModel: BookingFlowViewModel,
    onPaymentConfirmed: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule by viewModel.paymentSchedule.collectAsState()
    val session by viewModel.bookingSession.collectAsState()

    val totalFare = if (schedule.totalAmount > 0) schedule.totalAmount else 20000
    val startPaymentAmount = (totalFare * 0.40).toInt().coerceAtLeast(8000)
    val previousPaymentAmount = (totalFare * 0.10).toInt().coerceAtLeast(2000)
    val midTripAmount = (totalFare * 0.30).toInt().coerceAtLeast(6000)
    val tripEndAmount = totalFare - previousPaymentAmount - startPaymentAmount - midTripAmount

    var selectedMethod by remember { mutableStateOf(PaymentMethodType.GOOGLE_PAY) }

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundColor = ColorBgLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Navy Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorNavyDark)
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
                    text = "Pay Trip Start (40%)",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
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

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Orange-Accented Alert Banner Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Orange Accent Stripe
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(56.dp)
                                .background(ColorOrange)
                        )

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                text = "Pay 40% to start your trip. Share OTP with partner after payment.",
                                fontSize = 13.sp,
                                color = Color(0xFF4B5563),
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // 2. Amount Breakdown Card
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
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Amount Breakdown",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextDark
                        )

                        TripAmountRow(
                            label = "Trip Total",
                            amount = "₹${totalFare.toString().reversed().chunked(3).joinToString(",").reversed()}"
                        )

                        TripAmountRow(
                            label = "Trip Start Payment (40%)",
                            amount = "₹${startPaymentAmount.toString().reversed().chunked(3).joinToString(",").reversed()}"
                        )

                        TripAmountRow(
                            label = "Previous Payments (10%)",
                            amount = "-₹${previousPaymentAmount.toString().reversed().chunked(3).joinToString(",").reversed()}",
                            amountColor = ColorGreen
                        )

                        // Dashed Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .drawBehind {
                                    drawLine(
                                        color = ColorCardBorder,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
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
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextDark
                            )
                            Text(
                                text = "₹${startPaymentAmount.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ColorBlue
                            )
                        }
                    }
                }

                // 3. Remaining Payment Schedule Card
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
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Remaining Payment Schedule",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextDark
                        )

                        RemainingMilestoneRow(
                            title = "Mid Trip (Day 5)",
                            amount = "₹${midTripAmount.toString().reversed().chunked(3).joinToString(",").reversed()} (30%)"
                        )

                        RemainingMilestoneRow(
                            title = "Trip End",
                            amount = "₹${tripEndAmount.coerceAtLeast(4000).toString().reversed().chunked(3).joinToString(",").reversed()} (20%)"
                        )
                    }
                }

                // 4. Preferred UPI Apps Card
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
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Preferred UPI Apps",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextDark
                        )

                        TripUpiOptionRow(
                            name = "Google Pay",
                            isSelected = selectedMethod == PaymentMethodType.GOOGLE_PAY,
                            onSelect = { selectedMethod = PaymentMethodType.GOOGLE_PAY }
                        )

                        TripUpiOptionRow(
                            name = "PhonePe",
                            isSelected = selectedMethod == PaymentMethodType.PHONE_PE,
                            onSelect = { selectedMethod = PaymentMethodType.PHONE_PE }
                        )

                        TripUpiOptionRow(
                            name = "Paytm",
                            isSelected = selectedMethod == PaymentMethodType.PAYTM,
                            onSelect = { selectedMethod = PaymentMethodType.PAYTM }
                        )
                    }
                }

                // 5. GoIndiaCab Wallet Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedMethod = PaymentMethodType.WALLET },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selectedMethod == PaymentMethodType.WALLET) 1.5.dp else 1.dp,
                        color = if (selectedMethod == PaymentMethodType.WALLET) ColorOrange else ColorCardBorder
                    ),
                    shadowElevation = 1.dp
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
                                tint = ColorNavyDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "GoIndiaCab Wallet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorTextDark
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

                // 6. Refund Guarantee Banner Pill
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ColorGreenBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorGreenBorder)
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
                            tint = ColorGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "100% refund if trip cancelled before start",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Fixed Bottom CTA Button matching SVG: "Pay ₹8,000 & Confirm"
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Button(
                        onClick = {
                            viewModel.verifyOtp("7429") {
                                onPaymentConfirmed()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                    ) {
                        Text(
                            text = "Pay ₹${startPaymentAmount.toString().reversed().chunked(3).joinToString(",").reversed()} & Confirm",
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
private fun TripAmountRow(
    label: String,
    amount: String,
    amountColor: Color = ColorTextDark
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = ColorTextMuted
        )
        Text(
            text = amount,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = amountColor
        )
    }
}

@Composable
private fun RemainingMilestoneRow(
    title: String,
    amount: String
) {
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
                tint = ColorTextMuted,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = ColorTextMuted
            )
        }

        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextDark
        )
    }
}

@Composable
private fun TripUpiOptionRow(
    name: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFFFF7ED) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) ColorOrange else ColorCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circular Placeholder Icon with check/cross
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFFFEDD5) else ColorBgLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) ColorOrange else ColorTextMuted
                    )
                }

                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = ColorTextDark
                )
            }

            // Radio Button on the right matching SVG
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) ColorOrange else Color(0xFFD1D5DB),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(ColorOrange)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TripStartPaymentScreenPreview() {
    GoIndiaCabTheme {
        TripStartPaymentScreen(
            viewModel = AppContainer.createBookingFlowViewModel(),
            onPaymentConfirmed = {},
            onBackClick = {}
        )
    }
}
