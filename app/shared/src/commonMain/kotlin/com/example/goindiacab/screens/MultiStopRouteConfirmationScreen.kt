package com.example.goindiacab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.RouteStopItem
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BadgeOrangeText
import com.example.goindiacab.theme.BadgePeachBg
import com.example.goindiacab.theme.BrandBlue
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.MarkerGreen
import com.example.goindiacab.theme.MarkerOrange
import com.example.goindiacab.theme.SurfaceGray
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.TextMuted
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import androidx.compose.foundation.clickable
import com.example.goindiacab.viewmodel.MultiStopRouteViewModel

/**
 * Screen 21: Plan Your Route - Multi-Stop Route Summary Screen (Phase 5).
 *
 * Architecture & Lifecycle Role:
 * - Visualizes complete road journey itinerary: origin pickup point, intermediate tourist/rest stops,
 *   destination point, toll road estimations, total distance (km), and estimated travel time.
 * - Allows adding new waypoints through an interactive dialog.
 * - Navigation Flow:
 *     - Pressing "Continue" forwards configured route data to [SCHEDULE_RIDE] for date/time booking.
 * - Back Button Contract:
 *     - If the "Add Stop" dialog is visible, pressing back dismisses the dialog first without leaving the screen.
 *     - Otherwise, invokes [onBackClick] to return cleanly to [SEARCH_DESTINATION] without creating loops.
 */
@Composable
fun MultiStopRouteConfirmationScreen(
    viewModel: MultiStopRouteViewModel = remember { AppContainer.createMultiStopRouteViewModel() },
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onChangeDestinationClick: () -> Unit = onBackClick
) {
    val uiState by viewModel.uiState.collectAsState()
    val routeData = uiState.routeData

    var showAddStopDialog by remember { mutableStateOf(false) }
    var newStopName by remember { mutableStateOf("") }
    var newStopLocation by remember { mutableStateOf("") }

    // Intercept back button only to dismiss dialog if open, else let central handler navigate back
    PlatformBackHandler(enabled = showAddStopDialog) {
        showAddStopDialog = false
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = SurfaceGray,
            topBar = {
                OutstationTopBar(
                    title = "Plan Your Route",
                    onBackClick = onBackClick
                )
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "TOTAL ESTIMATE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹${routeData.totalEstimateInr}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            )
                            Text(
                                text = "Multi-stop route",
                                fontSize = 12.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted
                            )
                        }

                        Button(
                            onClick = onContinueClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            modifier = Modifier
                                .height(48.dp)
                                .width(140.dp)
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp)
            ) {
                item {
                    // Multi-Stop Route Summary Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Header & Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Multi-Stop Route Summary",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BadgePeachBg)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "SCHEDULE ONLY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = BadgeOrangeText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 1. Pickup Point
                            TimelineStopItem(
                                dotColor = MarkerGreen,
                                tag = "PICKUP POINT",
                                title = routeData.pickupPoint,
                                showConnectingLine = true
                            )

                            // 2. + Add Stop Action Button
                            Row(
                                modifier = Modifier
                                    .padding(start = 24.dp, top = 6.dp, bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    onClick = { showAddStopDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceGray,
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Add Stop",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = dmSansFontFamily(),
                                            color = BrandOrange
                                        )
                                    }
                                }
                            }

                            // 3. Dynamic Intermediate Stops
                            routeData.stops.forEach { stop ->
                                TimelineStopItem(
                                    dotColor = BrandBlue,
                                    tag = "STOP ${stop.stopNumber}",
                                    title = "${stop.name}, ${stop.locationSubtitle}",
                                    showConnectingLine = true,
                                    onDeleteClick = { viewModel.removeStop(stop.id) }
                                )
                            }

                            // 4. Drop-off Point (tap to change destination)
                            TimelineStopItem(
                                dotColor = MarkerOrange,
                                tag = "DROP-OFF POINT (TAP TO CHANGE)",
                                title = routeData.dropoffPoint,
                                showConnectingLine = false,
                                onClick = onChangeDestinationClick
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Route Distance & Duration Metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "TOTAL DISTANCE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${routeData.totalDistanceKm} km",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = outfitFontFamily(),
                                        color = BrandBlue
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "EST. DURATION",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = routeData.estDurationText,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dialog for adding an intermediate stop
            if (showAddStopDialog) {
                AlertDialog(
                    onDismissRequest = { showAddStopDialog = false },
                    title = {
                        Text("Add Route Stop", fontFamily = outfitFontFamily(), fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = newStopName,
                                onValueChange = { newStopName = it },
                                label = { Text("Stop Name (e.g. Neemrana Fort)") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = newStopLocation,
                                onValueChange = { newStopLocation = it },
                                label = { Text("Location / State (e.g. Rajasthan)") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newStopName.isNotBlank()) {
                                    viewModel.addStop(newStopName, newStopLocation.ifBlank { "India" })
                                    newStopName = ""
                                    newStopLocation = ""
                                    showAddStopDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                        ) {
                            Text("Add")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddStopDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TimelineStopItem(
    dotColor: Color,
    tag: String,
    title: String,
    showConnectingLine: Boolean,
    onDeleteClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onClick)
                        .padding(vertical = 4.dp)
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            if (showConnectingLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tag,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )

                if (onDeleteClick != null) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("✕", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (showConnectingLine) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
