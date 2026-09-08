package com.example.goindiacab.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.MilestoneStatus
import com.example.goindiacab.data.models.PaymentScheduleMilestone
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorBgLight = Color(0xFFF4F6F9)
private val ColorTextDark = Color(0xFF111827)
private val ColorTextMuted = Color(0xFF6B7280)
private val ColorTextDisabled = Color(0xFF9CA3AF)
private val ColorEmerald = Color(0xFF10B981)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorBlueAccent = Color(0xFF1A73E8)
private val ColorBlueSoft = Color(0xFFEDF5FF)
private val ColorBlueText = Color(0xFF1E3A8A)
private val ColorCardBorder = Color(0xFFE5E7EB)

/**
 * Screen: Payment Schedule / Milestones (trip-payment-schedule.svg).
 * Production-ready screen tracking staged installment milestones (Advance 10%,
 * Trip Start 40%, Mid Trip 30%, Trip End 20%), OTP verification rules,
 * and direct one-click milestone settlement.
 */
@Composable
fun TripPaymentScheduleScreen(
    viewModel: BookingFlowViewModel,
    onPayMilestoneClick: (amount: Int, milestoneTitle: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule by viewModel.paymentSchedule.collectAsState()
    val session by viewModel.bookingSession.collectAsState()

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    // Identify active DUE milestone
    val dueMilestone = schedule.milestones.find { it.status == MilestoneStatus.DUE }
        ?: schedule.milestones.getOrNull(1)

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
                    text = "Payment Schedule",
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
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Card 1: Route Summary & Total Fare
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = schedule.routeSummary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = schedule.durationAndDistance,
                                fontSize = 13.sp,
                                color = ColorTextMuted
                            )
                        }

                        Text(
                            text = "Total: ₹${schedule.totalAmount}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorBlueAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card 2: PAYMENT MILESTONES Stepper
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "PAYMENT MILESTONES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextDark,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        schedule.milestones.forEachIndexed { index, milestone ->
                            val isLast = index == schedule.milestones.lastIndex
                            MilestoneTimelineItem(
                                milestone = milestone,
                                isLast = isLast
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card 3: Security OTP notice banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ColorBlueSoft
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_shield_check),
                            contentDescription = "Secure OTP",
                            tint = ColorBlueAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = schedule.securityNote,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorBlueText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary CTA: Pay Now for Due Milestone
                val payAmount = dueMilestone?.amount ?: 8000
                Button(
                    onClick = {
                        if (dueMilestone != null) {
                            onPayMilestoneClick(dueMilestone.amount, dueMilestone.title)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Text(
                        text = "Pay Now ₹$payAmount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MilestoneTimelineItem(
    milestone: PaymentScheduleMilestone,
    isLast: Boolean
) {
    val (nodeColor, badgeText, badgeColor) = when (milestone.status) {
        MilestoneStatus.PAID -> Triple(ColorEmerald, "PAID", ColorEmerald)
        MilestoneStatus.DUE -> Triple(ColorOrange, "DUE", ColorOrange)
        MilestoneStatus.UPCOMING -> Triple(Color(0xFFD1D5DB), "UPCOMING", ColorTextDisabled)
    }

    val isUpcoming = milestone.status == MilestoneStatus.UPCOMING
    val titleColor = if (isUpcoming) ColorTextDisabled else ColorTextDark
    val subtitleColor = if (isUpcoming) ColorTextDisabled else ColorTextMuted

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Node & Vertical Connector Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            // Node Dot
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(nodeColor)
            )

            // Vertical Connector Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(44.dp)
                        .background(
                            if (milestone.status == MilestoneStatus.PAID) ColorEmerald else Color(0xFFE5E7EB)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Text & Badge Row
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = milestone.title,
                    fontSize = 14.sp,
                    fontWeight = if (isUpcoming) FontWeight.Medium else FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = milestone.subtitle,
                    fontSize = 13.sp,
                    color = subtitleColor
                )
            }

            Text(
                text = badgeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = badgeColor
            )
        }
    }
}
