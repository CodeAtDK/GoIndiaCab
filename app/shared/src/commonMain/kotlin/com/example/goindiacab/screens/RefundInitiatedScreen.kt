package com.example.goindiacab.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val ColorBgLight = Color(0xFFF4F6F9)
private val ColorTextDark = Color(0xFF111827)
private val ColorTextMuted = Color(0xFF6B7280)
private val ColorTextSecondary = Color(0xFF4B5563)
private val ColorEmerald = Color(0xFF10B981)
private val ColorAmberSoft = Color(0xFFFFF4EC)
private val ColorAmberAccent = Color(0xFFFF7A00)
private val ColorBlueAccent = Color(0xFF1A73E8)
private val ColorCardBorder = Color(0xFFE5E7EB)

/**
 * Screen: Refund Status / No Partner Available (refund-initiated.svg).
 * Production-ready screen displaying instant refund transparency, payment gateway timeline,
 * itemized refund invoice, and alternative booking re-entry points.
 */
@Composable
fun RefundInitiatedScreen(
    viewModel: BookingFlowViewModel,
    onTryDifferentDateClick: () -> Unit,
    onTryDifferentVehicleClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val refundInvoice by viewModel.refundInvoice.collectAsState()
    val session by viewModel.bookingSession.collectAsState()

    var showSupportDialog by remember { mutableStateOf(false) }

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

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
                    text = "Refund Status",
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
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Amber Warning Alert Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(ColorAmberSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_alert_circle_orange),
                        contentDescription = "No Partner Available",
                        tint = ColorAmberAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = refundInvoice.failureReasonTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorTextDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = refundInvoice.failureReasonDescription,
                    fontSize = 14.sp,
                    color = ColorTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // REFUND INVOICE Card
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
                            text = "REFUND INVOICE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextDark,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Amount Paid Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Amount Paid",
                                fontSize = 14.sp,
                                color = ColorTextSecondary
                            )
                            Text(
                                text = "₹${refundInvoice.amountPaid}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Refund Amount Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Refund Amount",
                                fontSize = 14.sp,
                                color = ColorTextSecondary
                            )
                            Text(
                                text = "₹${refundInvoice.refundAmount}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorEmerald
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            thickness = 1.dp,
                            color = Color(0xFFF3F4F6)
                        )

                        // Timeline Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Timeline",
                                fontSize = 14.sp,
                                color = ColorTextSecondary
                            )
                            Text(
                                text = refundInvoice.timelineText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorBlueAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Refunding To Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Refunding To",
                                fontSize = 14.sp,
                                color = ColorTextSecondary
                            )
                            Text(
                                text = refundInvoice.refundingToMethod,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextDark
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            thickness = 1.dp,
                            color = Color(0xFFF3F4F6)
                        )

                        // Status Progression Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = refundInvoice.statusText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorEmerald
                            )
                            Text(
                                text = refundInvoice.statusSubtext,
                                fontSize = 13.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFE5E7EB))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(refundInvoice.progressPercent)
                                    .fillMaxHeight()
                                    .background(ColorEmerald)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary CTA: Try Different Date
                Button(
                    onClick = onTryDifferentDateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Text(
                        text = "Try Different Date",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary CTA: Try Different Vehicle
                OutlinedButton(
                    onClick = onTryDifferentVehicleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorCardBorder)
                ) {
                    Text(
                        text = "Try Different Vehicle",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contact Support Text Link
                TextButton(
                    onClick = { showSupportDialog = true }
                ) {
                    Text(
                        text = "Contact Support",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBlueAccent
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = {
                Text(
                    text = "GoIndiaCab 24x7 Helpline",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Our travel support executives are available round-the-clock to assist you with refunds or booking alternative outstation transport.",
                        fontSize = 14.sp,
                        color = ColorTextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorBgLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Toll Free: 1800-202-CAB (222)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Email: refunds@goindiacab.com",
                                fontSize = 13.sp,
                                color = ColorBlueAccent
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSupportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    Text("Call Helpline", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSupportDialog = false }) {
                    Text("Dismiss", color = ColorTextMuted)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
