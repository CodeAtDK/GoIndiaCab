package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_alert_circle_orange
import goindiacab.app.shared.generated.resources.ic_back_arrow
import goindiacab.app.shared.generated.resources.ic_check_circle_green
import goindiacab.app.shared.generated.resources.ic_more_vert
import org.jetbrains.compose.resources.painterResource

private val ColorNavy = Color(0xFF0D1E3A)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorGreen = Color(0xFF10B981)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)
private val ColorPillBg = Color(0xFFFFF0E6)

/**
 * Screen 28: Fare Details / Itemized Breakdown (Phase 6).
 *
 * Architecture & Lifecycle Role:
 * - Details transparent pricing for the outstation booking: base fare, driver allowance, night charges,
 *   state tax & toll estimates, and GST.
 * - Navigation Flow:
 *     - "Continue" proceeds forward to [BOOKING_SUMMARY].
 * - Back Button Contract:
 *     - Hardware back and top back arrow invoke [onBackClick], returning cleanly to [CAB_DETAIL].
 */
@Composable
fun FareDetailsScreen(
    viewModel: BookingFlowViewModel,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val vehicle = session.selectedVehicle
    val fare = session.fareBreakdown
    val totalFare = fare.totalEstimatedFare

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer(
        modifier = modifier.fillMaxSize().background(ColorBg)
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
                    text = "Fare Details",
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

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Route Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${session.pickupLocation.substringBefore(",").substringBefore("(").trim()} → ${session.dropLocation.substringBefore(",").trim()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${session.routeDistanceKm} km • One Way • Today, 10:30 AM",
                                fontSize = 13.sp,
                                color = ColorTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(ColorPillBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "₹${totalFare}",
                                color = ColorOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Detailed Breakdown Card
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
                            text = "Detailed Breakdown",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        val perKm = vehicle?.perKmRate ?: 19
                        val baseAmount = fare.baseFare
                        val driverAllowance = 300
                        val gstAmount = fare.gstAndBookingFees.coerceAtLeast(234)

                        FareRow(
                            title = "Base Fare (${session.routeDistanceKm} km × ₹${perKm}/km)",
                            amountText = "₹$baseAmount"
                        )
                        FareRow(
                            title = "Driver Allowance",
                            amountText = "₹$driverAllowance"
                        )
                        FareRow(
                            title = "GST @5%",
                            amountText = "₹$gstAmount"
                        )
                        FareRow(
                            title = "Toll Charges (estimated)",
                            amountText = "Included",
                            amountColor = ColorGreen
                        )

                        if (fare.discountAmount > 0) {
                            FareRow(
                                title = "Coupon Discount (${fare.couponCode ?: ""})",
                                amountText = "-₹${fare.discountAmount}",
                                amountColor = ColorGreen
                            )
                        }

                        // Dotted Divider
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
                                text = "Total Fare",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                            Text(
                                text = "₹$totalFare",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorOrange
                            )
                        }
                    }
                }

                // Extra Charges Card
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
                            text = "Extra Charges",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        val extraKmRate = vehicle?.extraKmRate ?: 12
                        ExtraChargeRow("Per km after ${fare.baseKmLimit} km: ₹$extraKmRate/km")
                        ExtraChargeRow("Night charges (10PM–6AM): ₹250")
                        ExtraChargeRow("Waiting charges: ₹100/hr after 30 min free")
                        ExtraChargeRow("Parking & entry fees: As applicable")
                    }
                }

                // Hourly Rental Rates Card
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
                            text = "Hourly Rental Rates",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        HourlyRateRow("1 hour", "₹499")
                        HourlyRateRow("2 hours", "₹899")
                        HourlyRateRow("4 hours", "₹1,599")
                        HourlyRateRow("8 hours", "₹2,999")
                    }
                }

                // Fare Includes Card
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
                            text = "Fare Includes",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )

                        InclusionRow("Fuel charges & Driver allowance")
                        InclusionRow("State entry tax & Toll charges")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Sticky Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL FARE",
                            fontSize = 11.sp,
                            color = ColorTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹$totalFare",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorOrange
                        )
                    }

                    Button(
                        onClick = onContinueClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "Continue to Booking",
                            fontSize = 16.sp,
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
private fun FareRow(
    title: String,
    amountText: String,
    amountColor: Color = ColorTextPrimary
) {
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
            text = amountText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = amountColor
        )
    }
}

@Composable
private fun ExtraChargeRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_alert_circle_orange),
            contentDescription = null,
            tint = ColorOrange,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = ColorTextSecondary
        )
    }
}

@Composable
private fun HourlyRateRow(hours: String, price: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = hours,
            fontSize = 14.sp,
            color = ColorTextSecondary
        )
        Text(
            text = price,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary
        )
    }
}

@Composable
private fun InclusionRow(text: String) {
    Row(
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
            text = text,
            fontSize = 14.sp,
            color = ColorTextPrimary
        )
    }
}
