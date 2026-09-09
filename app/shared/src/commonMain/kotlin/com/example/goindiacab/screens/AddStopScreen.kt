package com.example.goindiacab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

/**
 * Stop item for popular transit waypoints.
 */
data class TransitStopItem(
    val id: String,
    val name: String,
    val state: String,
    val description: String = "Popular En-Route Waypoint",
    val distanceKm: Int? = null
)

/**
 * Screen 21b: Dedicated Add Stop Screen.
 * Shares the exact theme, visual language, and interaction model of SearchDestinationScreen,
 * but dedicated specifically to adding intermediate waypoints along the route.
 */
@Composable
fun AddStopScreen(
    onBackClick: () -> Unit = {},
    onStopSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val popularWaypoints = remember {
        listOf(
            TransitStopItem("s1", "Mathura", "Uttar Pradesh", "Famous Krishna Janmabhoomi Waypoint", 145),
            TransitStopItem("s2", "Vrindavan", "Uttar Pradesh", "Popular Pilgrimage Stop", 155),
            TransitStopItem("s3", "Neemrana", "Rajasthan", "Historical Fort & Heritage Stop", 122),
            TransitStopItem("s4", "Alwar", "Rajasthan", "Sariska Tiger Reserve Gateway", 158),
            TransitStopItem("s5", "Murthal", "Haryana", "Famous Dhaba & Food Waypoint", 52),
            TransitStopItem("s6", "Kurukshetra", "Haryana", "Historic Cultural City", 160),
            TransitStopItem("s7", "Sonipat", "Haryana", "Industrial Hub & Food Stops", 45),
            TransitStopItem("s8", "Rewari", "Haryana", "Heritage Steam Engine Waypoint", 88)
        )
    }

    val filteredStops = remember(searchQuery, popularWaypoints) {
        if (searchQuery.isBlank()) popularWaypoints
        else popularWaypoints.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.state.contains(searchQuery, ignoreCase = true)
        }
    }

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = SurfaceGray,
            topBar = {
                OutstationTopBar(
                    title = "Add Stop",
                    onBackClick = onBackClick
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. Active Orange Search Input Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, BrandOrange),
                        shadowElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(BrandOrange)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search stop city or waypoint...",
                                        fontSize = 15.sp,
                                        fontFamily = dmSansFontFamily(),
                                        color = TextMuted
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = dmSansFontFamily(),
                                        color = TextDark
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(BrandOrange),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            if (searchQuery.isNotBlank()) {
                                                onStopSelected(searchQuery.trim())
                                            }
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✕", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Custom Query Direct Add Row
                if (searchQuery.isNotBlank() && !popularWaypoints.any { it.name.equals(searchQuery.trim(), ignoreCase = true) }) {
                    item {
                        Surface(
                            onClick = { onStopSelected(searchQuery.trim()) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF7ED),
                            border = BorderStroke(1.5.dp, BrandOrange)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📍", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Add \"${searchQuery.trim()}\"",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = outfitFontFamily(),
                                            color = BrandOrange
                                        )
                                        Text(
                                            text = "Tap to add custom waypoint to route",
                                            fontSize = 12.sp,
                                            fontFamily = dmSansFontFamily(),
                                            color = TextMuted
                                        )
                                    }
                                }
                                Text(
                                    text = "+ Add",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = BrandOrange
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // 2. Quick Transit Waypoint Chips
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "POPULAR EN-ROUTE STOPS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark,
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(popularWaypoints.take(6)) { stop ->
                                Surface(
                                    onClick = { onStopSelected(stop.name) },
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    shadowElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("📍", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = stop.name,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = dmSansFontFamily(),
                                            color = TextDark
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                // 3. Section Header: SUGGESTED STOPS
                item {
                    Text(
                        text = if (searchQuery.isNotBlank()) "MATCHING WAYPOINTS" else "ALL TRANSIT WAYPOINTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 4. Stop Rows
                items(
                    items = filteredStops,
                    key = { it.id }
                ) { stop ->
                    TransitStopRow(
                        stop = stop,
                        onClick = { onStopSelected(stop.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransitStopRow(
    stop: TransitStopItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF7ED)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "${stop.name}, ${stop.state}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stop.description,
                        fontSize = 12.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextMuted
                    )
                }
            }

            if (stop.distanceKm != null) {
                Text(
                    text = "${stop.distanceKm} km",
                    fontSize = 13.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(start = 66.dp, end = 16.dp),
            color = Color(0xFFF1F5F9),
            thickness = 1.dp
        )
    }
}

@Preview
@Composable
fun AddStopScreenPreview() {
    GoIndiaCabTheme {
        AddStopScreen()
    }
}
