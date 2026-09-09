package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.CalendarIcon
import com.example.goindiacab.components.PersonIcon
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

/**
 * Filter categories matching my-trips-screen.svg.
 */
enum class TripFilterTab(val label: String) {
    ALL("All"),
    UPCOMING("Upcoming"),
    ONGOING("Ongoing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

/**
 * Status of a trip in My Trips.
 */
enum class MyTripStatus(
    val label: String,
    val bgColor: Color,
    val textColor: Color
) {
    UPCOMING("Upcoming", Color(0xFFE6F0FF), Color(0xFF1E60D5)),
    ONGOING("Ongoing", Color(0xFFFFF8E6), Color(0xFFD97706)),
    COMPLETED("Completed", Color(0xFFE6F8F0), Color(0xFF00A86B)),
    CANCELLED("Cancelled", Color(0xFFFFEAEA), Color(0xFFE53935))
}

/**
 * Trip card item model.
 */
data class MyTripItem(
    val id: String,
    val origin: String,
    val destination: String,
    val status: MyTripStatus,
    val dateTimeText: String,
    val vehicleModel: String,
    val fareLabel: String = "Total Fare",
    val fareAmountText: String
)

/**
 * Screen 34: My Trips Screen (Phase 4).
 * Reimplemented from scratch strictly matching my-trips-screen.svg.
 */
@Composable
fun MyTripsScreen(
    onBackClick: () -> Unit = {},
    onTripClick: (MyTripItem) -> Unit = {},
    onOngoingTripClick: (MyTripItem) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onOffersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(TripFilterTab.ALL) }

    val allTrips = remember {
        listOf(
            MyTripItem(
                id = "TRIP-101",
                origin = "Delhi",
                destination = "Jaipur",
                status = MyTripStatus.UPCOMING,
                dateTimeText = "Sat, 15 Oct • 06:00 AM",
                vehicleModel = "Toyota Innova",
                fareLabel = "Total Fare",
                fareAmountText = "₹4,250"
            ),
            MyTripItem(
                id = "TRIP-102",
                origin = "Delhi",
                destination = "Agra",
                status = MyTripStatus.COMPLETED,
                dateTimeText = "Wed, 12 Sep • 09:15 AM",
                vehicleModel = "Maruti Suzuki Dzire",
                fareLabel = "Total Fare",
                fareAmountText = "₹2,450"
            ),
            MyTripItem(
                id = "TRIP-103",
                origin = "Mumbai",
                destination = "Pune",
                status = MyTripStatus.COMPLETED,
                dateTimeText = "Tue, 28 Aug • 02:30 PM",
                vehicleModel = "Toyota Etios",
                fareLabel = "Total Fare",
                fareAmountText = "₹2,199"
            ),
            MyTripItem(
                id = "TRIP-104",
                origin = "Delhi",
                destination = "Noida",
                status = MyTripStatus.CANCELLED,
                dateTimeText = "Wed, 15 Aug • 08:00 PM",
                vehicleModel = "Honda City",
                fareLabel = "Total Fare",
                fareAmountText = "₹850"
            ),
            MyTripItem(
                id = "TRIP-105",
                origin = "Bengaluru",
                destination = "Mysuru",
                status = MyTripStatus.ONGOING,
                dateTimeText = "Today, 10:45 AM • Pickup at 10:30 AM",
                vehicleModel = "Toyota Innova",
                fareLabel = "Est. Fare",
                fareAmountText = "₹3,250"
            )
        )
    }

    val filteredTrips = remember(selectedFilter, allTrips) {
        when (selectedFilter) {
            TripFilterTab.ALL -> allTrips
            TripFilterTab.UPCOMING -> allTrips.filter { it.status == MyTripStatus.UPCOMING }
            TripFilterTab.ONGOING -> allTrips.filter { it.status == MyTripStatus.ONGOING }
            TripFilterTab.COMPLETED -> allTrips.filter { it.status == MyTripStatus.COMPLETED }
            TripFilterTab.CANCELLED -> allTrips.filter { it.status == MyTripStatus.CANCELLED }
        }
    }

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFF6B00)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "My Trips",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White
                    )

                    IconButton(onClick = { /* Overflow options */ }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_more_vert),
                            contentDescription = "Options",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Standard 4-Tab Bottom Navigation (No Wallet)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Home
                    BottomNavTabItem(
                        label = "Home",
                        isSelected = false,
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_nav_home),
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = onHomeClick
                    )

                    // 2. My Trips (Active Orange)
                    BottomNavTabItem(
                        label = "My Trips",
                        isSelected = true,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF6B00)),
                                contentAlignment = Alignment.Center
                            ) {
                                CalendarIcon(size = 16.dp, color = Color.White)
                            }
                        },
                        onClick = {}
                    )

                    // 3. Offers
                    BottomNavTabItem(
                        label = "Offers",
                        isSelected = false,
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_discount_tag),
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = onOffersClick
                    )

                    // 4. Profile
                    BottomNavTabItem(
                        label = "Profile",
                        isSelected = false,
                        icon = {
                            PersonIcon(
                                size = 20.dp,
                                color = Color(0xFF6B7280)
                            )
                        },
                        onClick = onProfileClick
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Filter Chips Row matching SVG
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TripFilterTab.values().forEach { tab ->
                    val isSelected = tab == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFFFF6B00) else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFFFF6B00) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = tab }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.label,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = dmSansFontFamily(),
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            // Trip Cards List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredTrips, key = { it.id }) { trip ->
                    MyTripCard(
                        trip = trip,
                        onClick = {
                            if (trip.status == MyTripStatus.ONGOING) {
                                onOngoingTripClick(trip)
                            } else {
                                onTripClick(trip)
                            }
                        }
                    )
                }

                if (filteredTrips.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No ${selectedFilter.label.lowercase()} trips found.",
                                fontSize = 14.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = dmSansFontFamily()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Trip Card matching SVG.
 */
@Composable
private fun MyTripCard(
    trip: MyTripItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top: Route + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${trip.origin} ➔ ${trip.destination}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color(0xFF0F172A)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = trip.status.bgColor
                ) {
                    Text(
                        text = trip.status.label,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = dmSansFontFamily(),
                        color = trip.status.textColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom: Date/Time + Car Model on left, Total Fare on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = trip.dateTimeText,
                        fontSize = 12.5.sp,
                        color = Color(0xFF64748B),
                        fontFamily = dmSansFontFamily()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_cab),
                            contentDescription = null,
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = trip.vehicleModel,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = trip.fareLabel,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = dmSansFontFamily()
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = trip.fareAmountText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = Color(0xFF1E60D5)
                    )
                }
            }
        }
    }
}

/**
 * Bottom Nav Item composable for MyTripsScreen.
 */
@Composable
private fun BottomNavTabItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon()
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = dmSansFontFamily(),
            color = if (isSelected) Color(0xFFFF6B00) else Color(0xFF6B7280)
        )
    }
}

@Preview
@Composable
fun MyTripsScreenPreview() {
    GoIndiaCabTheme {
        MyTripsScreen()
    }
}
