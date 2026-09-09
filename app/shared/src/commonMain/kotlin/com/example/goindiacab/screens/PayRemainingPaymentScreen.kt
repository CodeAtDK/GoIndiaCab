package com.example.goindiacab.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.data.models.PaymentMethodType
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.outfitFontFamily
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val TopBarNavy = Color(0xFF0A1128)
private val ColorBg = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

/**
 * Payment amount mode selection.
 */
enum class RemainingPaymentMode {
    NEXT_STAGE_ADVANCE,
    FULL_REMAINING_BALANCE
}

/**
 * Screen: Pay Remaining Payment.
 * Dedicated payment settlement screen allowing users to pay either the next advance milestone
 * or full remaining balance for an existing trip booking.
 */
@Composable
fun PayRemainingPaymentScreen(
    trip: MyTripItem,
    onPaymentSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()

    // Calculate numeric amounts
    val totalFare = remember(trip) {
        trip.fareAmountText.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 4250
    }

    val (advancePaid, remainingBalance) = remember(trip, totalFare) {
        when (trip.status) {
            MyTripStatus.UPCOMING -> {
                val advance = (totalFare * 0.10).toInt().coerceAtLeast(425)
                Pair(advance, totalFare - advance)
            }
            MyTripStatus.ONGOING -> {
                val advance = (totalFare * 0.50).toInt().coerceAtLeast(2125)
                Pair(advance, totalFare - advance)
            }
            MyTripStatus.COMPLETED -> Pair(totalFare, 0)
            MyTripStatus.CANCELLED -> Pair(0, 0)
        }
    }

    // Next stage milestone amount (e.g. 40% of trip or half remaining)
    val nextStageAmount = remember(remainingBalance, totalFare) {
        (totalFare * 0.40).toInt().coerceAtMost(remainingBalance).coerceAtLeast(1700)
    }

    var selectedMode by remember { mutableStateOf(RemainingPaymentMode.NEXT_STAGE_ADVANCE) }
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.UPI) }
    var isProcessing by remember { mutableStateOf(false) }

    val payableAmount = if (selectedMode == RemainingPaymentMode.NEXT_STAGE_ADVANCE) {
        nextStageAmount
    } else {
        remainingBalance
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ColorBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TopBarNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back_arrow),
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Pay Remaining Payment",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isProcessing) {
                                isProcessing = true
                                toast("Processing payment of ₹$payableAmount...")
                                onPaymentSuccess()
                            }
                        },
                        enabled = !isProcessing && payableAmount > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isProcessing) "Processing..." else "Pay ₹$payableAmount Securely",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )
                            ChevronRightIcon(size = 15.dp, color = Color.White)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔒 100% Safe & Secure • Instant Bank Confirmation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted
                        )
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Trip Summary Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandOrange.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_cab),
                            contentDescription = null,
                            tint = BrandOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${trip.origin} ➔ ${trip.destination}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                        Text(
                            text = "Booking ID: ${trip.id} • ${trip.vehicleModel}",
                            fontSize = 12.sp,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted
                        )
                    }
                }
            }

            // 2. Outstanding Balance Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "OUTSTANDING BALANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Trip Fare", fontSize = 13.5.sp, color = TextMuted)
                        Text("₹$totalFare", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Advance Already Paid", fontSize = 13.5.sp, color = TextMuted)
                        Text("₹$advancePaid (✓ Paid)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remaining Due Amount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color(0xFFE11D48)
                        )
                        Text(
                            text = "₹$remainingBalance",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = Color(0xFFE11D48)
                        )
                    }
                }
            }

            // 3. Payment Mode Selection (Installment vs Full)
            Text(
                text = "CHOOSE PAYMENT AMOUNT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            // Option 1: Next Stage Advance
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMode = RemainingPaymentMode.NEXT_STAGE_ADVANCE },
                shape = RoundedCornerShape(14.dp),
                color = if (selectedMode == RemainingPaymentMode.NEXT_STAGE_ADVANCE) Color(0xFFFFF7ED) else Color.White,
                border = BorderStroke(
                    1.5.dp,
                    if (selectedMode == RemainingPaymentMode.NEXT_STAGE_ADVANCE) BrandOrange else CardBorderColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = selectedMode == RemainingPaymentMode.NEXT_STAGE_ADVANCE,
                            onClick = { selectedMode = RemainingPaymentMode.NEXT_STAGE_ADVANCE },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandOrange)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Pay Next Stage Advance",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BrandOrange.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "RECOMMENDED",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandOrange,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Advance before departure segment",
                                fontSize = 12.sp,
                                fontFamily = outfitFontFamily(),
                                color = TextMuted
                            )
                        }
                    }

                    Text(
                        text = "₹$nextStageAmount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = BrandOrange
                    )
                }
            }

            // Option 2: Full Remaining Balance
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMode = RemainingPaymentMode.FULL_REMAINING_BALANCE },
                shape = RoundedCornerShape(14.dp),
                color = if (selectedMode == RemainingPaymentMode.FULL_REMAINING_BALANCE) Color(0xFFFFF7ED) else Color.White,
                border = BorderStroke(
                    1.5.dp,
                    if (selectedMode == RemainingPaymentMode.FULL_REMAINING_BALANCE) BrandOrange else CardBorderColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = selectedMode == RemainingPaymentMode.FULL_REMAINING_BALANCE,
                            onClick = { selectedMode = RemainingPaymentMode.FULL_REMAINING_BALANCE },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandOrange)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Pay Full Remaining Balance",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Text(
                                        text = "100% SETTLE",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Zero pending dues on trip",
                                fontSize = 12.sp,
                                fontFamily = outfitFontFamily(),
                                color = TextMuted
                            )
                        }
                    }

                    Text(
                        text = "₹$remainingBalance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                }
            }

            // 4. Payment Method Selection
            Text(
                text = "SELECT PAYMENT METHOD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            // UPI Option
            PaymentMethodCard(
                title = "UPI",
                subtitle = "Google Pay, PhonePe, Paytm, BHIM",
                isSelected = selectedMethod == PaymentMethodType.UPI,
                onClick = { selectedMethod = PaymentMethodType.UPI }
            )

            // Netbanking Option
            PaymentMethodCard(
                title = "Netbanking",
                subtitle = "HDFC, ICICI, SBI, Axis & all major banks",
                isSelected = selectedMethod == PaymentMethodType.NETBANKING,
                onClick = { selectedMethod = PaymentMethodType.NETBANKING }
            )

            // Cards Option
            PaymentMethodCard(
                title = "Credit / Debit Cards",
                subtitle = "Visa, MasterCard, RuPay",
                isSelected = selectedMethod == PaymentMethodType.CARDS,
                onClick = { selectedMethod = PaymentMethodType.CARDS }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFFFFF7ED) else Color.White,
        border = BorderStroke(1.5.dp, if (isSelected) BrandOrange else CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = BrandOrange)
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted
                    )
                }
            }

            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandOrange
                )
            }
        }
    }
}
