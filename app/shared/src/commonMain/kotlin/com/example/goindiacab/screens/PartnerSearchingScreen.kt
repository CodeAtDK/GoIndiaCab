package com.example.goindiacab.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.viewmodel.PartnerSearchUiState
import goindiacab.app.shared.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private val ColorNavyDark = Color(0xFF091E42)
private val ColorNavySurface = Color(0xFF0F275A)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorEmerald = Color(0xFF10B981)

/**
 * Screen: Partner Searching (Radar Screen)
 * High-fidelity production screen matching partner-searching.svg.
 * Features multi-stage concentric radar ripple animation, live 45-second matching countdown,
 * rotating status messaging, trip snapshot card, and automatic transition when partner is matched.
 */
@Composable
fun PartnerSearchingScreen(
    viewModel: BookingFlowViewModel,
    onPartnerFound: () -> Unit,
    onCancelSearch: () -> Unit,
    onNoPartnerFound: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val searchState by viewModel.partnerSearchState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    // Intercept back button to confirm cancellation
    PlatformBackHandler(enabled = true) {
        showCancelDialog = true
    }

    // Live countdown timer from 45 seconds down
    var secondsLeft by remember { mutableStateOf(45) }
    LaunchedEffect(Unit) {
        viewModel.startPartnerSearch()
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        if (searchState !is PartnerSearchUiState.Found) {
            viewModel.loadRefundInvoice()
            onNoPartnerFound()
        }
    }

    // Informational radar scanner rotating messages
    val tickerMessages = remember {
        listOf(
            "Scanning verified 4.8★+ drivers in your area...",
            "Checking vehicle cleanliness & sanitized boot space...",
            "Sending route details to top 3 nearby drivers...",
            "Locking guaranteed fixed outstation fare...",
            "Confirming driver partner & arrival ETA..."
        )
    }
    var currentMessageIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2200)
            currentMessageIndex = (currentMessageIndex + 1) % tickerMessages.size
        }
    }

    // Automatic navigation when partner is found
    LaunchedEffect(searchState) {
        if (searchState is PartnerSearchUiState.Found) {
            delay(600) // smooth visual transition
            onPartnerFound()
        }
    }

    // Radar Concentric Expanding Circle Animations
    val infiniteTransition = rememberInfiniteTransition()

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, delayMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, delayMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val coreGlow by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val vehicle = session.selectedVehicle
    val vehicleName = vehicle?.name ?: "Sedan (Swift Dzire / Etios)"
    val pickupText = session.pickupLocation.ifBlank { "Pickup Location" }
    val dropText = session.dropLocation.ifBlank { "Destination" }
    val advanceAmount = session.fareBreakdown.advanceDepositAmount

    AdaptiveContainer(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ColorNavyDark, Color(0xFF0A1B38), Color(0xFF061124))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showCancelDialog = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Cancel Search",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Live Timer Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(ColorEmerald)
                        )
                        Text(
                            text = "Matching in ${secondsLeft}s",
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Concentric Radar Canvas Animation
            Box(
                modifier = Modifier
                    .size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Multi-pulse animated radar waves
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val maxRadius = size.minDimension / 2

                    // Pulse 1
                    val r1 = maxRadius * pulse1
                    val alpha1 = (1f - pulse1).coerceIn(0f, 1f) * 0.35f
                    drawCircle(
                        color = ColorOrange.copy(alpha = alpha1),
                        radius = r1,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Pulse 2
                    val r2 = maxRadius * pulse2
                    val alpha2 = (1f - pulse2).coerceIn(0f, 1f) * 0.35f
                    drawCircle(
                        color = ColorOrange.copy(alpha = alpha2),
                        radius = r2,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Pulse 3
                    val r3 = maxRadius * pulse3
                    val alpha3 = (1f - pulse3).coerceIn(0f, 1f) * 0.35f
                    drawCircle(
                        color = ColorOrange.copy(alpha = alpha3),
                        radius = r3,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Outer fixed subtle ring
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                )

                // Middle fixed ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                )

                // Core Glowing Orange Circle with Cab Silhouette
                Box(
                    modifier = Modifier
                        .size(76.dp * coreGlow)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(ColorOrange, Color(0xFFE05A00))
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_cab),
                        contentDescription = "Cab Search",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Title
            Text(
                text = "FINDING YOUR BEST MATCH...",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rotating Subtitle Ticker
            AnimatedContent(
                targetState = currentMessageIndex,
                label = "ticker"
            ) { idx ->
                Text(
                    text = tickerMessages.getOrElse(idx) { tickerMessages.first() },
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Trip Snapshot Card (Glassmorphic Midnight Blue)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ColorNavySurface.copy(alpha = 0.8f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Vehicle & Advance status row
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
                                painter = painterResource(Res.drawable.ic_car_sedan),
                                contentDescription = null,
                                tint = ColorOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = vehicleName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Advance Paid Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorEmerald.copy(alpha = 0.15f))
                                .border(1.dp, ColorEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "₹$advanceAmount Paid ✓",
                                color = ColorEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    )

                    // Origin -> Destination row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PICKUP",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = pickupText,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }

                        Icon(
                            painter = painterResource(Res.drawable.ic_chevron_right),
                            contentDescription = null,
                            tint = ColorOrange,
                            modifier = Modifier.size(18.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "DROP",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dropText,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date & Route Distance
                    Text(
                        text = "Travel: ${session.travelDate} • ${session.travelTime} (${session.routeDistanceKm} km)",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel Search Ghost Outline Button
            OutlinedButton(
                onClick = { showCancelDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
            ) {
                Text(
                    text = "Cancel Search",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE2E8F0)
                )
            }

            // QA Fast Forward Buttons (discreet for rapid validation)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        viewModel.generateBookingTicket()
                        onPartnerFound()
                    },
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "⚡ Partner Match (Bypass)",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }

                TextButton(
                    onClick = {
                        viewModel.loadRefundInvoice()
                        onNoPartnerFound()
                    },
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "⚠️ No Partner (Refund)",
                        color = Color(0xFFFCA5A5).copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    // Cancellation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "Cancel Cab Search?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to stop searching for drivers? You can restart anytime with your current itinerary.",
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCancelDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorOrange)
                ) {
                    Text("Keep Searching", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelPartnerSearch()
                        onCancelSearch()
                    }
                ) {
                    Text("Cancel Search", color = Color(0xFFDC2626))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
