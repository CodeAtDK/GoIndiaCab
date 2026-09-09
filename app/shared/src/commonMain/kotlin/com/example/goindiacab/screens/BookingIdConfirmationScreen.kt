package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.di.AppContainer

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.components.rememberPlatformToast
import com.example.goindiacab.data.models.BookingTicket
import com.example.goindiacab.data.models.DriverPartnerInfo
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorNavy = Color(0xFF091E42)
private val ColorEmerald = Color(0xFF10B981)
private val ColorEmeraldLight = Color(0xFFD1FAE5)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)

/**
 * Screen 35: Booking ID & Confirmation (Final Ticket Screen)
 * Exact fidelity to 35. booking-id-confirmation.svg.
 * Displays ticket perforated card with notches, full route timeline,
 * driver partner profile, high-visibility 4-digit pickup OTP,
 * itemized payment status, and quick map tracking.
 */
@Composable
fun BookingIdConfirmationScreen(
    viewModel: BookingFlowViewModel,
    onBackToHomeClick: () -> Unit,
    onTrackRideClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val ticketState by viewModel.latestBookingTicket.collectAsState()
    val toast = rememberPlatformToast()

    // Ensure ticket model is generated if not already
    LaunchedEffect(Unit) {
        if (ticketState == null) {
            viewModel.generateBookingTicket()
        }
    }

    val ticket: BookingTicket = ticketState ?: remember(session) {
        val totalAmount = session.fareBreakdown.totalEstimatedFare
        val advanceAmount = session.fareBreakdown.advanceDepositAmount
        val balanceAmount = session.fareBreakdown.remainingPayableAtPickup
        val vehicle = session.selectedVehicle

        BookingTicket(
            bookingId = session.bookingId.ifBlank { "GIC-2026-09145" },
            confirmationTime = "Today, 06:45 PM",
            origin = session.pickupLocation.ifBlank { "Pickup Location" },
            destination = session.dropLocation.ifBlank { "Destination" },
            stops = session.stopLocations,
            travelDate = session.travelDate,
            travelTime = session.travelTime,
            tripType = "One-Way Outstation",
            distanceKm = session.routeDistanceKm,
            vehicleCategory = vehicle?.category?.displayName ?: "Prime Sedan",
            vehicleModel = vehicle?.name ?: "Maruti Swift Dzire",
            totalFare = totalAmount,
            advancePaid = advanceAmount,
            balanceRemaining = balanceAmount,
            driver = DriverPartnerInfo(
                id = "drv_92145",
                name = "Rajesh Kumar",
                rating = 4.9f,
                totalTrips = 1420,
                phone = "+91 98765 43210",
                vehicleModel = vehicle?.name ?: "Swift Dzire",
                vehicleNumber = "DL 01 AB 1234",
                vehicleColor = "White",
                startOtp = "4829",
                etaMinutes = 4
            ),
            paymentMethod = session.selectedPaymentMethod,
            paymentTxnId = "TXN-9842109823",
            isConfirmed = true
        )
    }

    // Hardware back navigates cleanly back to home
    PlatformBackHandler(enabled = true) {
        onBackToHomeClick()
    }

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundColor = ColorBg
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Navy Header Hero with Emerald Confirmation Badge
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorNavy)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with Home Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackToHomeClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back_arrow),
                            contentDescription = "Home",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "Booking Confirmed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(onClick = { toast("Booking details shared via SMS & WhatsApp") }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_more_vert),
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Emerald Checkmark Circle
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(ColorEmerald)
                        .border(4.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_check_circle_green),
                        contentDescription = "Confirmed",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "BOOKING CONFIRMED!",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Booking ID Pill Badge with Copy
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .clickable { toast("Booking ID ${ticket.bookingId} copied to clipboard!") }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ID: ${ticket.bookingId}",
                        color = Color(0xFFE2E8F0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "COPY",
                        color = ColorOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Confirmed on ${ticket.confirmationTime}",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }

            // Scrollable Ticket Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Perforated Ticket Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Vehicle & Trip Type Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = ticket.vehicleModel,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorTextPrimary
                                )
                                Text(
                                    text = "${ticket.vehicleCategory} • ${ticket.tripType}",
                                    fontSize = 13.sp,
                                    color = ColorTextSecondary
                                )
                            }

                            // Travel Schedule Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${ticket.travelDate}\n${ticket.travelTime}",
                                    color = Color(0xFF1D4ED8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Route Timeline
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorBg)
                                .padding(12.dp)
                        ) {
                            // Origin
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(ColorEmerald)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = ticket.origin,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorTextPrimary
                                )
                            }

                            // Intermediate stops if any
                            ticket.stops.forEach { stop ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(16.dp)
                                            .background(Color(0xFFD1D5DB))
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = "Via: $stop",
                                        fontSize = 12.sp,
                                        color = ColorTextSecondary
                                    )
                                }
                            }

                            if (ticket.stops.isEmpty()) {
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(18.dp)
                                            .background(Color(0xFFD1D5DB))
                                    )
                                }
                            }

                            // Destination
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = ticket.destination,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Total Distance: ~${ticket.distanceKm} km (All tolls & state permits included)",
                                fontSize = 11.sp,
                                color = ColorTextSecondary
                            )
                        }

                        // Perforated Dashed Divider
                        PerforatedTicketDivider(modifier = Modifier.padding(vertical = 16.dp))

                        // Assigned Driver & Vehicle Section
                        Text(
                            text = "ASSIGNED DRIVER & VEHICLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextSecondary,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Driver Avatar
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, ColorOrange.copy(alpha = 0.4f), CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.img_driver_portrait),
                                    contentDescription = "Driver Portrait",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ticket.driver.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorTextPrimary
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "★ ${ticket.driver.rating}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                    Text(
                                        text = "• ${ticket.driver.totalTrips} Trips",
                                        fontSize = 12.sp,
                                        color = ColorTextSecondary
                                    )
                                }

                                Text(
                                    text = "${ticket.driver.vehicleColor} ${ticket.driver.vehicleModel} • ${ticket.driver.vehicleNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorTextPrimary
                                )
                            }

                            // Call Driver Button
                            IconButton(
                                onClick = { toast("Calling driver Rajesh Kumar: ${ticket.driver.phone}") },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF))
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_headset),
                                    contentDescription = "Call Driver",
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // High-Visibility Start Ride OTP Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFFBEB))
                                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "START RIDE OTP",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "Share with driver before boarding",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }

                                // OTP Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFB45309))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = ticket.driver.startOtp,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }
                        }

                        // Perforated Dashed Divider
                        PerforatedTicketDivider(modifier = Modifier.padding(vertical = 16.dp))

                        // Fare & Payment Summary
                        Text(
                            text = "PAYMENT SUMMARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextSecondary,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Estimated Fare", fontSize = 13.sp, color = ColorTextSecondary)
                            Text(text = "₹${ticket.totalFare}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "Advance Deposit (10%)", fontSize = 13.sp, color = ColorTextSecondary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ColorEmeraldLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "PAID ✓", color = ColorEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(text = "₹${ticket.advancePaid}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ColorEmerald)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Balance Payable at Drop", fontSize = 13.sp, color = ColorTextSecondary)
                            Text(text = "₹${ticket.balanceRemaining}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ColorOrange)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Passenger Safety Assurance Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_shield_check),
                        contentDescription = null,
                        tint = ColorEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Free cancellation up to 2 hours before scheduled pickup. 24x7 SOS & Live ride GPS sharing included.",
                        fontSize = 12.sp,
                        color = ColorTextSecondary,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom Fixed Action Bar
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
                    // Primary CTA: Track Ride on Map
                    Button(
                        onClick = onTrackRideClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_location_pin),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Track Ride on Map",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Secondary Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { toast("Invoice & Ticket PDF saved to Downloads") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Download PDF",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorTextPrimary
                            )
                        }

                        Button(
                            onClick = onBackToHomeClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorNavy)
                        ) {
                            Text(
                                text = "Back to Home",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visual dashed perforated line with subtle notch dividers
 */
@Composable
private fun PerforatedTicketDivider(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawLine(
            color = Color(0xFFE5E7EB),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx(),
            pathEffect = pathEffect
        )
    }
}

@Preview
@Composable
fun BookingIdConfirmationScreenPreview() {
    GoIndiaCabTheme {
        BookingIdConfirmationScreen(
            viewModel = AppContainer.createBookingFlowViewModel(),
            onBackToHomeClick = {},
            onTrackRideClick = {}
        )
    }
}
