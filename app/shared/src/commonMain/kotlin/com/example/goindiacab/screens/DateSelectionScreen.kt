package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.SurfaceGray
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily

/**
 * Screen 23: Select Travel Date Screen (Phase 5).
 * Full calendar picker matching SVG 23 with interactive date selection and 10-day advance booking constraint.
 * Back navigation pops cleanly to Schedule a Ride without modifying unconfirmed changes.
 */
@Composable
fun DateSelectionScreen(
    initialDate: Int = 20,
    onBackClick: () -> Unit,
    onDateConfirmed: (Int) -> Unit
) {
    var selectedDay by remember { mutableStateOf(initialDate) }

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = SurfaceGray,
            topBar = {
                OutstationTopBar(
                    title = "Select Travel Date",
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
                            onClick = { onDateConfirmed(selectedDay) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "Confirm Date",
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
                        Text("📅", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Book up to 10 days in advance",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFFEA580C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Month Header & Change Month
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "October 2026",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                    Text(
                        text = "Change Month ›",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF0052CC)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Days of week row
                val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDays.forEach { day ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = day,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = dmSansFontFamily(),
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Monthly Calendar Grid (Days 1 to 28)
                val allDays = (1..28).toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allDays) { day ->
                        val isSelectable = day in 15..25
                        val isSelected = selectedDay == day

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> BrandOrange
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = isSelectable) {
                                    selectedDay = day
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$day",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = dmSansFontFamily(),
                                color = when {
                                    isSelected -> Color.White
                                    isSelectable -> TextDark
                                    else -> Color(0xFFCBD5E1)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DateSelectionScreenPreview() {
    GoIndiaCabTheme {
        DateSelectionScreen(
            initialDate = 20,
            onBackClick = {},
            onDateConfirmed = {}
        )
    }
}
