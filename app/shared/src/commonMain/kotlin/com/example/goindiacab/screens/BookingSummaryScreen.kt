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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorNavy = Color(0xFF0D1E3A)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorBlue = Color(0xFF0052CC)
private val ColorGreen = Color(0xFF10B981)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)
private val ColorPillOrange = Color(0xFFFFF0E6)
private val ColorPillBlue = Color(0xFFE6F0FF)
private val ColorPillGreen = Color(0xFFECFDF5)

/**
 * Screen 30: Booking Summary & Payment Schedule (Phase 6).
 *
 * Architecture & Lifecycle Role:
 * - Comprehensive itinerary summary: origin, destination, vehicle category, passenger info,
 *   fare breakdown, coupon deduction, and 4-milestone payment plan (10% advance deposit).
 * - Navigation Flow:
 *     - "Apply Coupon" opens [APPLY_COUPON].
 *     - "Proceed to Pay Advance" navigates forward to [PAYMENT_SCREEN].
 * - Back Button Contract:
 *     - Hardware back and top back arrow invoke [onBackClick], popping back to [CAB_DETAIL] or [FARE_DETAILS].
 */
@Composable
fun BookingSummaryScreen(
    viewModel: BookingFlowViewModel,
    onBackClick: () -> Unit,
    onApplyCouponClick: () -> Unit,
    onProceedToPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val vehicle = session.selectedVehicle
    val fare = session.fareBreakdown
    val totalFare = fare.totalEstimatedFare
    val advanceAmount = fare.advanceDepositAmount
    val appliedCoupon = session.appliedCoupon

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    // Dynamic milestone amounts based on total
    val day1Amount = (totalFare * 0.40).toInt()
    val midTripAmount = (totalFare * 0.30).toInt()
    val tripEndAmount = totalFare - advanceAmount - day1Amount - midTripAmount

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundColor = ColorBg
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // White Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    text = "Booking Summary",
                    color = ColorTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )

                IconButton(onClick = { /* Menu */ }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_more_vert),
                        contentDescription = "More options",
                        tint = ColorTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Route Multi-Stop Timeline Card
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoutePointRow(
                            label = "PICKUP",
                            location = session.pickupLocation,
                            dotColor = ColorGreen
                        )
                        TimelineConnector()

                        session.stopLocations.forEachIndexed { index, stop ->
                            RoutePointRow(
                                label = "STOP ${index + 1}",
                                location = stop,
                                dotColor = ColorBlue
                            )
                            TimelineConnector()
                        }

                        RoutePointRow(
                            label = "DROPOFF",
                            location = session.dropLocation,
                            dotColor = ColorOrange
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFF3F4F6)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${session.routeDistanceKm} km • ${session.tripDurationDays} Days",
                                fontSize = 13.sp,
                                color = ColorTextSecondary,
                                fontWeight = FontWeight.Medium
                            )

                            Box(
                                modifier = Modifier
                                    .background(ColorPillOrange, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = vehicle?.category?.displayName?.uppercase() ?: "PREMIUM SUV",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorOrange
                                )
                            }
                        }
                    }
                }

                // Selected Cab Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Vehicle Thumbnail
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
                                contentDescription = vehicle?.name ?: "Cab",
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
                                        text = vehicle?.category?.displayName ?: "Premium SUV",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorBlue
                                    )
                                }
                            }

                            Text(
                                text = "${vehicle?.seatingCapacity ?: 6} Seater • Diesel • ${vehicle?.rating ?: 4.7}★",
                                fontSize = 13.sp,
                                color = ColorTextSecondary
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(ColorPillGreen, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "With Driver",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF047857)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Vehicle Only",
                                        fontSize = 11.sp,
                                        color = ColorTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Payment Schedule Card
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Payment Schedule",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Trip Total",
                                fontSize = 14.sp,
                                color = ColorTextSecondary
                            )
                            Text(
                                text = "₹${totalFare}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorBlue
                            )
                        }

                        HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF3F4F6))

                        ScheduleMilestoneRow(
                            title = "At Booking (Now)",
                            amountText = "₹$advanceAmount (10%)"
                        )
                        ScheduleMilestoneRow(
                            title = "Trip Start (Day 1)",
                            amountText = "₹$day1Amount (40%)"
                        )
                        ScheduleMilestoneRow(
                            title = "Mid Trip (Day 5)",
                            amountText = "₹$midTripAmount (30%)"
                        )
                        ScheduleMilestoneRow(
                            title = "Trip End",
                            amountText = "₹$tripEndAmount (20%)"
                        )

                        // Coupon Badge if applied
                        if (appliedCoupon != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ColorPillGreen, RoundedCornerShape(10.dp))
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
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }
                }

                // Extra Charges Info Note
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_calendar_event),
                        contentDescription = null,
                        tint = ColorBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Extra charges: ₹${vehicle?.extraKmRate ?: 12}/km after limit, Night ₹250.",
                        fontSize = 12.sp,
                        color = ColorTextSecondary
                    )
                }

                // Apply Coupon Card (Dashed Orange Border)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(
                            width = 1.2.dp,
                            color = ColorOrange,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onApplyCouponClick() }
                        .padding(14.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF047857)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_discount_tag),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = if (appliedCoupon != null) "Coupon: ${appliedCoupon.code}" else "Apply Coupon",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = if (appliedCoupon != null) "Saved ₹${appliedCoupon.discountAmount} • Tap to change" else "Save more on your trip",
                                    fontSize = 13.sp,
                                    color = ColorTextSecondary
                                )
                            }
                        }

                        Icon(
                            painter = painterResource(Res.drawable.ic_chevron_right),
                            contentDescription = "Apply",
                            tint = ColorOrange,
                            modifier = Modifier.size(20.dp)
                        )
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
                                tint = ColorBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "If no partner accepts your booking, 100% refund within 12 hours.",
                                fontSize = 13.sp,
                                color = ColorTextSecondary,
                                lineHeight = 17.sp
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_luggage_bag),
                                contentDescription = null,
                                tint = ColorBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Luggage allowance: 2 medium bags + 1 small bag.",
                                fontSize = 13.sp,
                                color = ColorTextSecondary,
                                lineHeight = 17.sp
                            )
                        }
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
                        onClick = onProceedToPayClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                    ) {
                        Text(
                            text = "Pay ₹$advanceAmount (10%) & Book",
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
private fun RoutePointRow(
    label: String,
    location: String,
    dotColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextSecondary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = location,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextPrimary
            )
        }
    }
}

@Composable
private fun TimelineConnector() {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .width(2.dp)
            .height(12.dp)
            .background(Color(0xFFE5E7EB))
    )
}

@Composable
private fun ScheduleMilestoneRow(
    title: String,
    amountText: String
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
            text = amountText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextPrimary
        )
    }
}
