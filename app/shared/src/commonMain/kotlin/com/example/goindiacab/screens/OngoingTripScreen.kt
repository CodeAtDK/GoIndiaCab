package com.example.goindiacab.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val TopBarNavy = Color(0xFF0A1128)
private val BrandOrange = Color(0xFFFF6B00)
private val StatusGreenBg = Color(0xFFE6F8F0)
private val StatusGreenText = Color(0xFF00A86B)
private val BadgeOrangeBg = Color(0xFFFFF3E0)
private val BadgeOrangeText = Color(0xFFFF6B00)
private val ColorBg = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

/**
 * Route timeline stop state for Ongoing Round Trip.
 */
enum class TimelineStopStatus {
    COMPLETED,
    CURRENT,
    UPCOMING
}

data class OngoingRouteStop(
    val title: String,
    val subtitle: String,
    val status: TimelineStopStatus,
    val badgeLabel: String? = null
)

/**
 * Screen 35: Ongoing Round Trip Live Tracking Dashboard.
 * Reimplemented strictly matching ongoing-round-trip.svg.
 */
@Composable
fun OngoingTripScreen(
    onBackClick: () -> Unit = {},
    onTrackOnMapClick: () -> Unit = {},
    onContactDriverClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val routeStops = remember {
        listOf(
            OngoingRouteStop(
                title = "Delhi (Start)",
                subtitle = "Completed on Day 1 • 06:00 AM",
                status = TimelineStopStatus.COMPLETED,
                badgeLabel = "Completed"
            ),
            OngoingRouteStop(
                title = "Jaipur (Stop 1)",
                subtitle = "Completed on Day 1 • 01:30 PM",
                status = TimelineStopStatus.COMPLETED,
                badgeLabel = "Completed"
            ),
            OngoingRouteStop(
                title = "Udaipur (Stop 2)",
                subtitle = "Reached on Day 2 • Active Stop",
                status = TimelineStopStatus.CURRENT,
                badgeLabel = "CURRENT STOP"
            ),
            OngoingRouteStop(
                title = "Jodhpur (Stop 3)",
                subtitle = "Upcoming • Day 3 Stop",
                status = TimelineStopStatus.UPCOMING
            ),
            OngoingRouteStop(
                title = "Jaipur (Return Stop)",
                subtitle = "Upcoming • Day 4 Stop",
                status = TimelineStopStatus.UPCOMING
            ),
            OngoingRouteStop(
                title = "Delhi (End)",
                subtitle = "Upcoming • Day 5 Stop",
                status = TimelineStopStatus.UPCOMING
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ColorBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TopBarNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(60.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back_arrow),
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Ongoing Trip",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        bottomBar = {
            // Sticky Action Buttons: Track on Map & Contact Driver
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Track on Map Button (Outlined)
                    OutlinedButton(
                        onClick = onTrackOnMapClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandOrange),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_location_pin),
                                contentDescription = null,
                                tint = BrandOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Track on Map",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = BrandOrange
                            )
                        }
                    }

                    // Contact Driver Button (Filled Orange)
                    Button(
                        onClick = onContactDriverClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_headset),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Contact Driver",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Trip Header Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROUND TRIP JOURNEY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusGreenBg
                        ) {
                            Text(
                                text = "IN PROGRESS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = dmSansFontFamily(),
                                color = StatusGreenText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "Trip ID: GIC-RT-78542",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                }
            }

            // 2. Route Details Timeline Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Route Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    routeStops.forEachIndexed { index, stop ->
                        val isLast = index == routeStops.size - 1
                        TimelineStopRow(
                            stop = stop,
                            isLast = isLast
                        )
                    }
                }
            }

            // 3. Current Status & Map Preview Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "CURRENT STATUS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Currently at Udaipur",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                    }

                    // Assigned Driver Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            PersonIcon(size = 22.dp, color = Color(0xFF475569))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rajesh Kumar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            )
                            Text(
                                text = "Toyota Innova • DL 1CA 4567",
                                fontSize = 12.5.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                StarIcon(size = 12.dp, color = Color(0xFFD97706), isFilled = true)
                                Text(
                                    text = "4.9",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }

                    // Map Container Graphic Preview
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                // Lake circle
                                drawCircle(
                                    color = Color(0xFF93C5FD).copy(alpha = 0.4f),
                                    radius = 42.dp.toPx(),
                                    center = Offset(w * 0.4f, h * 0.5f)
                                )
                                drawCircle(
                                    color = Color(0xFF60A5FA).copy(alpha = 0.35f),
                                    radius = 28.dp.toPx(),
                                    center = Offset(w * 0.72f, h * 0.45f)
                                )
                                // Route road
                                drawLine(
                                    color = BrandOrange,
                                    start = Offset(w * 0.1f, h * 0.8f),
                                    end = Offset(w * 0.5f, h * 0.45f),
                                    strokeWidth = 3.5.dp.toPx()
                                )
                                drawLine(
                                    color = BrandOrange,
                                    start = Offset(w * 0.5f, h * 0.45f),
                                    end = Offset(w * 0.9f, h * 0.25f),
                                    strokeWidth = 3.5.dp.toPx()
                                )
                                // Active pin
                                drawCircle(
                                    color = BrandOrange,
                                    radius = 6.dp.toPx(),
                                    center = Offset(w * 0.5f, h * 0.45f)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.BottomEnd),
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "Live GPS Active",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Trip Metrics Row: Covered, Remaining, Days Left
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "COVERED",
                    value = "580 km",
                    subtext = "Distance",
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "REMAINING",
                    value = "720 km",
                    subtext = "Distance",
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "DAYS LEFT",
                    value = "3 Days",
                    subtext = "Duration",
                    modifier = Modifier.weight(1f)
                )
            }

            // 5. Payment Breakdown Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Payment Breakdown",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                        Text(
                            text = "50% Paid",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                    }

                    // Multi-Segmented Progress Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        // 10% Booking (Black)
                        Box(
                            modifier = Modifier
                                .weight(0.10f)
                                .fillMaxHeight()
                                .background(Color(0xFF0F172A))
                        )
                        // 40% Trip Start (Orange)
                        Box(
                            modifier = Modifier
                                .weight(0.40f)
                                .fillMaxHeight()
                                .background(BrandOrange)
                        )
                        // 50% Pending (Light Grey)
                        Box(
                            modifier = Modifier
                                .weight(0.50f)
                                .fillMaxHeight()
                                .background(Color(0xFFE2E8F0))
                        )
                    }

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                            )
                            Text(
                                text = "10% Booking",
                                fontSize = 11.5.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandOrange)
                            )
                            Text(
                                text = "40% Trip Start",
                                fontSize = 11.5.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted
                            )
                        }

                        Text(
                            text = "50% Pending on End",
                            fontSize = 11.5.sp,
                            fontFamily = dmSansFontFamily(),
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * Metric Stat Box item.
 */
@Composable
private fun MetricStatCard(
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
            Text(
                text = subtext,
                fontSize = 11.sp,
                fontFamily = dmSansFontFamily(),
                color = Color(0xFF94A3B8)
            )
        }
    }
}

/**
 * Timeline row item for route stops.
 */
@Composable
private fun TimelineStopRow(
    stop: OngoingRouteStop,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Timeline Dot & Dotted Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            when (stop.status) {
                TimelineStopStatus.COMPLETED -> {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00A86B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                TimelineStopStatus.CURRENT -> {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(BrandOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
                TimelineStopStatus.UPCOMING -> {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF94A3B8))
                    )
                }
            }

            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                ) {
                    val lineColor = when (stop.status) {
                        TimelineStopStatus.COMPLETED -> Color(0xFF00A86B)
                        TimelineStopStatus.CURRENT -> Color(0xFF00A86B)
                        TimelineStopStatus.UPCOMING -> Color(0xFFCBD5E1)
                    }
                    drawLine(
                        color = lineColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }
            }
        }

        // Stop Text & Badge
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stop.title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = if (stop.status == TimelineStopStatus.CURRENT) BrandOrange else TextDark
                )
                Text(
                    text = stop.subtitle,
                    fontSize = 12.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted
                )
            }

            if (stop.badgeLabel != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (stop.status == TimelineStopStatus.CURRENT) BadgeOrangeBg else StatusGreenBg
                ) {
                    Text(
                        text = stop.badgeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = dmSansFontFamily(),
                        color = if (stop.status == TimelineStopStatus.CURRENT) BadgeOrangeText else StatusGreenText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun OngoingTripScreenPreview() {
    GoIndiaCabTheme {
        OngoingTripScreen()
    }
}
