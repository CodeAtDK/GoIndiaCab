package com.example.goindiacab.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val TopBarNavy = Color(0xFF0A1128)
private val ColorBg = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

enum class TicketStatus(val label: String, val bgColor: Color, val textColor: Color) {
    OPEN("Open", Color(0xFFFFFBEB), Color(0xFFD97706)),
    IN_PROGRESS("In Progress", Color(0xFFEFF6FF), Color(0xFF1E40AF)),
    RESOLVED("Resolved", Color(0xFFECFDF5), Color(0xFF059669))
}

data class SupportTicketItem(
    val id: String,
    val category: String,
    val tripId: String,
    val subject: String,
    val status: TicketStatus,
    val submittedAt: String,
    val lastAgentResponse: String
)

/**
 * Screen: Support Ticket History.
 * Shows riders their open, in-progress, and resolved support queries.
 */
@Composable
fun HelpTicketHistoryScreen(
    onBackClick: () -> Unit = {},
    onRaiseNewTicketClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    var selectedFilter by remember { mutableStateOf("All") }

    val tickets = remember {
        listOf(
            SupportTicketItem(
                id = "GIC-8492",
                category = "Payment & Refund",
                tripId = "TRIP-104",
                subject = "Refund status for cancelled trip",
                status = TicketStatus.RESOLVED,
                submittedAt = "15 Aug, 08:30 PM",
                lastAgentResponse = "Support Agent: Refund of ₹850 has been successfully processed to your UPI ID (Arn: 49201938210)."
            ),
            SupportTicketItem(
                id = "GIC-9120",
                category = "Trip Experience",
                tripId = "TRIP-101",
                subject = "Driver demanded extra toll cash at expressway plaza",
                status = TicketStatus.IN_PROGRESS,
                submittedAt = "Today, 10:15 AM",
                lastAgentResponse = "Support Agent: We have contacted the driver partner and verified the toll statement. Resolution in progress."
            ),
            SupportTicketItem(
                id = "GIC-9340",
                category = "Trip Experience",
                tripId = "TRIP-105",
                subject = "Left reading glasses in backseat",
                status = TicketStatus.OPEN,
                submittedAt = "Today, 11:45 AM",
                lastAgentResponse = "Automated: Ticket assigned to Bangalore Fleet Operations. Checking with driver partner."
            )
        )
    }

    val filteredTickets = remember(selectedFilter, tickets) {
        when (selectedFilter) {
            "Open" -> tickets.filter { it.status == TicketStatus.OPEN || it.status == TicketStatus.IN_PROGRESS }
            "Resolved" -> tickets.filter { it.status == TicketStatus.RESOLVED }
            else -> tickets
        }
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
                        .height(56.dp)
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
                        text = "My Support Tickets",
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = onRaiseNewTicketClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "+ Raise a New Ticket",
                                fontSize = 15.sp,
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Filter Pills (All, Open, Resolved)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Open", "Resolved").forEach { tab ->
                    val isSelected = selectedFilter == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFilter = tab },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) BrandOrange else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) BrandOrange else CardBorderColor)
                    ) {
                        Text(
                            text = tab,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Ticket Cards List
            filteredTickets.forEach { ticket ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "#${ticket.id}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )
                                Text("•", fontSize = 12.sp, color = TextMuted)
                                Text(
                                    text = ticket.category,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandOrange
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ticket.status.bgColor
                            ) {
                                Text(
                                    text = ticket.status.label.uppercase(),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = ticket.status.textColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = ticket.subject,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )

                        Text(
                            text = "Linked Trip: ${ticket.tripId} • Submitted ${ticket.submittedAt}",
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            fontFamily = dmSansFontFamily()
                        )

                        // Agent reply box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                        ) {
                            Text(
                                text = ticket.lastAgentResponse,
                                fontSize = 12.sp,
                                fontFamily = dmSansFontFamily(),
                                color = Color(0xFF334155),
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
