package com.example.goindiacab.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorBlueHeader = Color(0xFF0052CC)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorGreen = Color(0xFF10B981)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)
private val ColorPillOrange = Color(0xFFFFF0E6)
private val ColorPillBlue = Color(0xFFE6F0FF)

/**
 * Screen 31: Review Booking & Booking Confirmed Status (Phase 8).
 *
 * Architecture & Lifecycle Role:
 * - Post-confirmation voucher displaying journey overview, confirmed driver assignment,
 *   fare breakdown, advance payment status, and customer support links.
 * - Back Button Contract:
 *     - Pressing hardware back or top back button invokes [onBackClick] to return cleanly to Home root
 *       with a cleared backstack.
 */
@Composable
fun BookingConfirmationScreen(
    viewModel: BookingFlowViewModel,
    onBackClick: () -> Unit,
    onBackToHomeClick: () -> Unit,
    onConfirmAndPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val confirmedBooking by viewModel.confirmedBooking.collectAsState()

    val vehicle = session.selectedVehicle
    val fare = session.fareBreakdown
    val totalFare = fare.totalEstimatedFare
    val appliedCoupon = session.appliedCoupon
    val isConfirmed = session.paymentStatus == "CONFIRMED" || confirmedBooking != null

    // Hardware & system back button support: navigate cleanly to Home root
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
            // Blue Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorBlueHeader)
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
                    text = if (isConfirmed) "Booking Confirmed" else "Review Booking",
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
                // Booking Success Banner if confirmed
                if (isConfirmed) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ColorGreen))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ColorGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_check_circle_green),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Ride Booked Successfully!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = "Booking ID: ${session.bookingId} • Partner Assigned",
                                    fontSize = 13.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }
                }

                // Trip Details Card
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Trip Details",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )

                            Box(
                                modifier = Modifier
                                    .background(ColorPillOrange, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "One-Way",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorOrange
                                )
                            }
                        }

                        // Origin and Drop Route with Connector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Connector Column
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(18.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(ColorGreen)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(Color(0xFFCBD5E1))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(ColorOrange)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = session.pickupLocation,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorTextPrimary
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = session.dropLocation,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorTextPrimary
                                )
                            }
                        }

                        HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF3F4F6))

                        // 3-Column Stats Row: DATE, TIME, DISTANCE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatColumn("DATE", session.travelDate.substringBefore("2026").trim())
                            StatColumn("TIME", session.travelTime)
                            StatColumn("DISTANCE", "${session.routeDistanceKm} km")
                        }
                    }
                }

                // Selected Cab & Assigned Driver Card
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
                            text = "Selected Cab",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3F4F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                val carDrawable = if (vehicle?.id == "veh_swift_dzire") {
                                    Res.drawable.img_car_swift_dzire
                                } else {
                                    Res.drawable.img_car_innova_crysta
                                }
                                Image(
                                    painter = painterResource(carDrawable),
                                    contentDescription = vehicle?.name ?: "Vehicle",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(64.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = vehicle?.name ?: "Toyota Innova Crysta",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTextPrimary
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(ColorPillBlue, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = vehicle?.category?.displayName ?: "SUV",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ColorBlueHeader
                                        )
                                    }
                                }

                                Text(
                                    text = "DL 1Y A 4872 • Sohan Singh",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorTextSecondary
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "★",
                                        color = Color(0xFFF59E0B),
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "4.9 Excellent driver",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Fare Summary Card
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
                            text = "Fare Summary",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        SummaryFareRow("Base fare", "₹${fare.baseFare}")
                        SummaryFareRow("Taxes", "₹${fare.gstAndBookingFees}")
                        SummaryFareRow("Toll", if (fare.tollAndStateTaxIncluded) "Included" else "₹50")

                        if (fare.discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Coupon discount",
                                    fontSize = 14.sp,
                                    color = ColorGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "-₹${fare.discountAmount}",
                                    fontSize = 15.sp,
                                    color = ColorGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF3F4F6))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                            Text(
                                text = "₹${totalFare}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                        }

                        if (appliedCoupon != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFECFDF5), RoundedCornerShape(10.dp))
                                    .border(1.dp, ColorGreen, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_check_circle_green),
                                    contentDescription = null,
                                    tint = ColorGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Coupon '${appliedCoupon.code}' applied successfully! Saving ₹${appliedCoupon.discountAmount}.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF047857),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Important Notes Card
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
                            text = "Important Notes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_shield_check),
                                contentDescription = null,
                                tint = ColorBlueHeader,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Free cancellation up to 30 minutes before pickup.",
                                fontSize = 13.sp,
                                color = ColorTextSecondary
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_luggage_bag),
                                contentDescription = null,
                                tint = ColorBlueHeader,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Luggage allowance: 2 medium bags + 1 small bag.",
                                fontSize = 13.sp,
                                color = ColorTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sticky Bottom Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (isConfirmed) {
                                onBackToHomeClick()
                            } else {
                                onConfirmAndPayClick()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_shield_check),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (isConfirmed) "Back to Home" else "Confirm & Pay ₹${totalFare}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = if (isConfirmed) "Need assistance? Call Driver: +91 98123 45678" else "Cancel Booking",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onBackClick() }
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextSecondary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary
        )
    }
}

@Composable
private fun SummaryFareRow(title: String, amount: String) {
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
