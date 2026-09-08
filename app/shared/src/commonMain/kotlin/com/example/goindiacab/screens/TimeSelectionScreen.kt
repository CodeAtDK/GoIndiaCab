package com.example.goindiacab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.goindiacab.data.models.OutstationSeedData
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.SurfaceGray
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily

/**
 * Screen 24: Select Pickup Time Screen (Phase 5).
 * Pickup slot selector matching SVG 24 with 4-hour advance booking notice.
 * Back navigation pops cleanly back to Schedule a Ride screen.
 */
@Composable
fun TimeSelectionScreen(
    initialTimeSlot: String = "06:00 PM",
    onBackClick: () -> Unit,
    onTimeConfirmed: (String) -> Unit
) {
    var selectedSlot by remember { mutableStateOf(initialTimeSlot) }

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = SurfaceGray,
            topBar = {
                OutstationTopBar(
                    title = "Select Pickup Time",
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
                            onClick = { onTimeConfirmed(selectedSlot) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "Confirm Time",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // 1. Advance Booking Notice Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF7ED),
                    border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🕒", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Minimum 4-hour advance booking required",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFFEA580C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 2. Section Title
                Text(
                    text = "Available Pickup Slots (Today)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. 2-Column Time Slots Grid
                val slots = OutstationSeedData.TIME_SLOTS
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(slots) { slot ->
                        val isSelected = selectedSlot == slot
                        Surface(
                            onClick = { selectedSlot = slot },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BrandOrange else Color(0xFFF1F5F9),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = slot,
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
        }
    }
}
