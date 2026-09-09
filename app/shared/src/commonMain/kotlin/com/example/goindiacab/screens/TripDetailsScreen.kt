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
 * Screen: Trip Details Screen.
 * Full-page dashboard displaying comprehensive trip specifics,
 * vehicle partner details, start OTP, and payment breakdown with advance paid vs remaining balance.
 */
@Composable
fun TripDetailsScreen(
    trip: MyTripItem,
    onBackClick: () -> Unit = {},
    onPayRemainingClick: (MyTripItem) -> Unit = {},
    onTrackLiveClick: () -> Unit = {},
    onContactDriverClick: () -> Unit = {},
    onRateTripClick: () -> Unit = {},
    onBookAgainClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()

    // Calculate dynamic monetary amounts based on the trip's fare
    val totalAmountNumeric = remember(trip) {
        trip.fareAmountText.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 4250
    }

    val (advancePaidAmount, remainingDueAmount) = remember(trip, totalAmountNumeric) {
        when (trip.status) {
            MyTripStatus.UPCOMING -> {
                val advance = (totalAmountNumeric * 0.10).toInt().coerceAtLeast(425)
                Pair(advance, totalAmountNumeric - advance)
            }
            MyTripStatus.ONGOING -> {
                val advance = (totalAmountNumeric * 0.50).toInt().coerceAtLeast(2125)
                Pair(advance, totalAmountNumeric - advance)
            }
            MyTripStatus.COMPLETED -> {
                Pair(totalAmountNumeric, 0)
            }
            MyTripStatus.CANCELLED -> {
                Pair(0, 0)
            }
        }
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_back_arrow),
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Trip Details",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    IconButton(onClick = { toast("Downloading Trip Invoice PDF...") }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_headset),
                            contentDescription = "Support",
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (trip.status) {
                        MyTripStatus.UPCOMING -> {
                            Button(
                                onClick = { onPayRemainingClick(trip) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Pay Remaining Payment (₹$remainingDueAmount)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = Color.White
                                    )
                                    ChevronRightIcon(size = 14.dp, color = Color.White)
                                }
                            }
                        }
                        MyTripStatus.ONGOING -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onTrackLiveClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, BrandOrange),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandOrange)
                                ) {
                                    Text(
                                        text = "Track Live",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = BrandOrange
                                    )
                                }

                                Button(
                                    onClick = { onPayRemainingClick(trip) },
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                                ) {
                                    Text(
                                        text = "Pay Next (₹$remainingDueAmount)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        MyTripStatus.COMPLETED -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onRateTripClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, BrandOrange),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandOrange)
                                ) {
                                    Text(
                                        text = "Rate Trip",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = BrandOrange
                                    )
                                }

                                Button(
                                    onClick = onBookAgainClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                                ) {
                                    Text(
                                        text = "Book Again",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        MyTripStatus.CANCELLED -> {
                            Button(
                                onClick = onBookAgainClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                            ) {
                                Text(
                                    text = "Book Again",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = Color.White
                                )
                            }
                        }
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
            // 1. Header Card with Trip ID, Date, and Status
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BOOKING ID: ${trip.id}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = trip.status.bgColor
                        ) {
                            Text(
                                text = trip.status.label.uppercase(),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = trip.status.textColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "${trip.origin} ➔ ${trip.destination}",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CalendarIcon(size = 14.dp, color = BrandOrange)
                        Text(
                            text = trip.dateTimeText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted
                        )
                    }
                }
            }

            // 2. Route & Address Details Card
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "ROUTE DETAILS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    // Origin
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .padding(top = 4.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Pickup Location",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                            Text(
                                text = "${trip.origin} City Center / Airport Terminal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }

                    // Divider dotted line
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .height(20.dp)
                            .width(2.dp)
                            .background(Color(0xFFCBD5E1))
                    )

                    // Destination
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(BrandOrange)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Drop Location",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                            Text(
                                text = "${trip.destination} Main Square / Hotel Destination",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }
                }
            }

            // 3. Assigned Cab & Driver Details Card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "VEHICLE & DRIVER PARTNER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandOrange.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_cab),
                                    contentDescription = "Cab",
                                    tint = BrandOrange,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = trip.vehicleModel,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )
                                Text(
                                    text = "DL 01 AB 1234 • AC Commercial",
                                    fontSize = 12.sp,
                                    fontFamily = outfitFontFamily(),
                                    color = TextMuted
                                )
                            }
                        }

                        // Call Driver Action
                        IconButton(
                            onClick = {
                                onContactDriverClick()
                                toast("Calling Driver Rajesh Kumar (+91 98765 43210)")
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE6F0FF))
                        ) {
                            PhoneCallIcon(size = 18.dp, color = Color(0xFF1E60D5))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Driver info and Start OTP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Driver: Rajesh Kumar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFFBEB)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    StarIcon(size = 11.dp, color = Color(0xFFD97706), isFilled = true)
                                    Text(
                                        text = "4.9",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                        }

                        // Start OTP
                        if (trip.status != MyTripStatus.COMPLETED && trip.status != MyTripStatus.CANCELLED) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A)
                            ) {
                                Text(
                                    text = "OTP: 4829",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = BrandOrange,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Payment & Remaining Due Balance Card (HIGHLIGHTED)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, if (remainingDueAmount > 0) BrandOrange.copy(alpha = 0.5f) else CardBorderColor),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PAYMENT BREAKDOWN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        if (remainingDueAmount > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF1F2)
                            ) {
                                Text(
                                    text = "PAYMENT DUE",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = Color(0xFFE11D48),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFECFDF5)
                            ) {
                                Text(
                                    text = "100% PAID",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = Color(0xFF059669),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Total Estimated Fare
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Estimated Fare", fontSize = 13.5.sp, color = TextMuted)
                        Text(trip.fareAmountText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }

                    // Advance Deposit Paid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Advance Paid", fontSize = 13.5.sp, color = TextMuted)
                            Text("✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        Text("₹$advancePaidAmount", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Remaining Payable Balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Remaining Due Amount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = outfitFontFamily(),
                                color = if (remainingDueAmount > 0) Color(0xFFE11D48) else TextDark
                            )
                            if (remainingDueAmount > 0) {
                                Text(
                                    text = "Pay in advance before trip stages",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Text(
                            text = "₹$remainingDueAmount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = if (remainingDueAmount > 0) Color(0xFFE11D48) else Color(0xFF10B981)
                        )
                    }

                    // Pay Remaining CTA Card inside the breakdown
                    if (remainingDueAmount > 0) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPayRemainingClick(trip) },
                            shape = RoundedCornerShape(12.dp),
                            color = BrandOrange.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Pay Remaining Balance",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = BrandOrange
                                    )
                                    Text(
                                        text = "UPI, Cards & Netbanking accepted",
                                        fontSize = 11.5.sp,
                                        color = TextMuted
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Pay Now",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandOrange
                                    )
                                    ChevronRightIcon(size = 14.dp, color = BrandOrange)
                                }
                            }
                        }
                    }
                }
            }

            // 5. Inclusions & Security Policy Notice
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "• All Toll Taxes & State Permits Included",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "• 100% Advance Payment Security Protected",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "• Free Cancellation up to 24 hours before pickup",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
