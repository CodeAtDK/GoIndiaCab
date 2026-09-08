package com.example.goindiacab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.goindiacab.data.models.ScheduleRideData
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BrandBlue
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.MarkerOrange
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.TextMuted
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import com.example.goindiacab.viewmodel.ScheduleRideViewModel

/**
 * Screen 22: Schedule a Ride Screen (Phase 5).
 *
 * Architecture & Lifecycle Role:
 * - Allows selecting travel date (day strip or calendar picker) and pickup time (slot selector),
 *   enforcing a 4-hour advance booking rule for outstation cab assignments.
 * - Navigation Flow:
 *     - "Change Month" opens [DATE_SELECTION].
 *     - "Select Time" opens [TIME_SELECTION].
 *     - "Proceed to Vehicle Selection" forwards confirmed schedule to [VEHICLE_OPTIONS].
 * - Back Button Contract:
 *     - Both hardware back and top app bar invoke [onBackClick] to return to [ROUTE_CONFIRMATION].
 */
@Composable
fun ScheduleRideScreen(
    viewModel: ScheduleRideViewModel = remember { AppContainer.createScheduleRideViewModel() },
    onBackClick: () -> Unit,
    onChangeMonthClick: () -> Unit,
    onSelectTimeClick: () -> Unit,
    onScheduleConfirmed: (ScheduleRideData) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val ride = uiState.rideData

    // Hardware & gesture back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                OutstationTopBar(
                    title = "Schedule a Ride",
                    onBackClick = onBackClick
                )
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.confirmSchedule { data ->
                                    onScheduleConfirmed(data)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "Schedule Ride",
                                fontSize = 16.sp,
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // 1. Origin & Destination Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Origin
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BrandBlue)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = ride.pickupLocation,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextDark
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(start = 22.dp, top = 10.dp, bottom = 10.dp),
                                color = Color(0xFFF1F5F9),
                                thickness = 1.dp
                            )

                            // Destination
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(MarkerOrange)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = ride.dropoffLocation,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextDark
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. Date Selection Strip Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ride.selectedMonth,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )

                                Text(
                                    text = "Change Month ›",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = dmSansFontFamily(),
                                    color = BrandBlue,
                                    modifier = Modifier.clickable(onClick = onChangeMonthClick)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Days of week header row
                            val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
                            val dates = listOf(20, 21, 22, 23, 24, 25, 26)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                daysOfWeek.forEach { day ->
                                    Box(modifier = Modifier.width(36.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = day,
                                            fontSize = 12.sp,
                                            fontFamily = dmSansFontFamily(),
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Date chips row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                dates.forEach { d ->
                                    val isSelected = ride.selectedDate == d
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) BrandBlue else Color.Transparent)
                                            .clickable { viewModel.updateDate(d) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$d",
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontFamily = dmSansFontFamily(),
                                            color = if (isSelected) Color.White else TextDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 3. Time Selector Row (Hour, Minute, Period)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TimeValueCard(
                            label = "HOUR",
                            value = ride.selectedHour,
                            modifier = Modifier.weight(1f),
                            onClick = onSelectTimeClick
                        )
                        TimeValueCard(
                            label = "MINUTE",
                            value = ride.selectedMinute,
                            modifier = Modifier.weight(1f),
                            onClick = onSelectTimeClick
                        )
                        TimeValueCard(
                            label = "PERIOD",
                            value = ride.selectedPeriod,
                            modifier = Modifier.weight(1f),
                            onClick = onSelectTimeClick
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. Estimated Fare Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF7ED),
                        border = BorderStroke(1.dp, Color(0xFFFED7AA))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ESTIMATED FARE (${ride.selectedVehicle.uppercase()})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = BrandOrange,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "₹${ride.estimatedFareMin} - ₹${ride.estimatedFareMax}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )
                            }

                            Text(
                                text = "All taxes included",
                                fontSize = 12.sp,
                                fontFamily = dmSansFontFamily(),
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 5. Vehicle Type Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Sedan", "SUV", "Premium").forEach { vehicle ->
                            val isSelected = ride.selectedVehicle.equals(vehicle, ignoreCase = true)
                            Surface(
                                onClick = { viewModel.updateVehicle(vehicle) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFEBF3FF) else Color.White,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) BrandBlue else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = vehicle,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontFamily = dmSansFontFamily(),
                                        color = if (isSelected) BrandBlue else TextDark
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 6. Free cancellation disclaimer
                item {
                    Text(
                        text = "* Free cancellation up to 1 hour before scheduled pickup.",
                        fontSize = 12.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeValueCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.height(72.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
        }
    }
}
