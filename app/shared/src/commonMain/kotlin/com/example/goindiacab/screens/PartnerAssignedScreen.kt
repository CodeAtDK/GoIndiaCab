package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.di.AppContainer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorBgLight = Color(0xFFF4F6F9)
private val ColorTextDark = Color(0xFF111827)
private val ColorTextMuted = Color(0xFF6B7280)
private val ColorEmerald = Color(0xFF10B981)
private val ColorEmeraldSoft = Color(0xFFE8F8F0)
private val ColorBlueAccent = Color(0xFF1A73E8)
private val ColorBlueSoft = Color(0xFFEDF5FF)
private val ColorPeachSoft = Color(0xFFFFF1E8)
private val ColorPeachText = Color(0xFF9A3412)
private val ColorCardBorder = Color(0xFFE5E7EB)

/**
 * Screen: Partner Assigned / Trip Confirmed (partner-assigned.svg).
 * Production-ready MVVM screen displaying confirmed driver partner details,
 * vehicle specs, departure schedule, payment terms, and trip management CTAs.
 */
@Composable
fun PartnerAssignedScreen(
    viewModel: BookingFlowViewModel,
    onViewTripDetailsClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val ticket by viewModel.latestBookingTicket.collectAsState()
    val driver = ticket?.driver ?: com.example.goindiacab.data.models.DriverPartnerInfo()

    var showContactDialog by remember { mutableStateOf(false) }

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
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = ColorTextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Partner Assigned",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextDark
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Success Checkmark
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(ColorEmeraldSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_check_circle_green),
                        contentDescription = "Success",
                        tint = ColorEmerald,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Trip Confirmed!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorTextDark
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Card 1: Vehicle & Driver Information
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Vehicle Details Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(width = 90.dp, height = 62.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = ColorBgLight
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.img_car_innova_crysta),
                                    contentDescription = driver.vehicleModel,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = driver.vehicleModel.ifBlank { "Toyota Innova Crysta" },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTextDark
                                    )

                                    Text(
                                        text = "★ ${driver.vehicleRating}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B)
                                    )
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = driver.vehicleSpecsText.ifBlank { "Diesel • 6 Seater" },
                                    fontSize = 12.sp,
                                    color = ColorTextMuted
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF3F4F6)
                                ) {
                                    Text(
                                        text = driver.vehicleNumber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTextDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            thickness = 1.dp,
                            color = Color(0xFFF3F4F6)
                        )

                        // Driver Details Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.img_driver_portrait),
                                contentDescription = driver.name,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = driver.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTextDark
                                    )

                                    if (driver.isVerified) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFEBF3FF)
                                        ) {
                                            Text(
                                                text = "VERIFIED",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2F80ED),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = "${driver.rating}★ • ${driver.totalTrips}+ Trips",
                                    fontSize = 13.sp,
                                    color = ColorTextMuted
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Speaks: ${driver.speaksLanguages}",
                                    fontSize = 12.sp,
                                    color = ColorTextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: Departure Scheduled
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = ColorBlueSoft
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "DEPARTURE SCHEDULED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorBlueAccent,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (session.travelDate.isNotBlank()) "${session.travelDate} at ${session.travelTime}" else driver.departureScheduledText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 3: Next Payment Notice Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = ColorPeachSoft
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Next Payment: ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("₹8,000 (40%)")
                            }
                            append(" due at trip start.")
                        },
                        fontSize = 13.sp,
                        color = ColorPeachText,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary CTA: View Trip Details
                Button(
                    onClick = onViewTripDetailsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Text(
                        text = "View Trip Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary CTA: Contact Partner
                OutlinedButton(
                    onClick = { showContactDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder)
                ) {
                    Text(
                        text = "Contact Partner",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextDark
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Contact Partner Dialog
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = {
                Text(
                    text = "Contact Driver Partner",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "You can reach ${driver.name} directly regarding pickup coordination or route instructions.",
                        fontSize = 14.sp,
                        color = ColorTextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorBgLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📞 ${driver.phone}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextDark,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showContactDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Text("Call Driver", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Close", color = ColorTextMuted)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview
@Composable
fun PartnerAssignedScreenPreview() {
    GoIndiaCabTheme {
        PartnerAssignedScreen(
            viewModel = AppContainer.createBookingFlowViewModel(),
            onViewTripDetailsClick = {},
            onBackClick = {}
        )
    }
}
