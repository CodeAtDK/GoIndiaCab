package com.example.goindiacab.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val NavyDark = Color(0xFF0A1A3A)
private val ScreenBg = Color(0xFFF4F6F9)
private val BrandOrange = Color(0xFFFF6B00)
private val CancelRed = Color(0xFFEF4444)
private val CancelRedBg = Color(0xFFFEE2E2)
private val SuccessGreen = Color(0xFF10B981)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

/**
 * Screen: Cancel Booking Flow with Reason Selector and Refund Policy Calculation.
 * Backed by API-42.
 */
@Composable
fun CancelBookingScreen(
    bookingId: String = "GIC-849201",
    origin: String = "New Delhi",
    destination: String = "Agra",
    travelDate: String = "20 Sep 2026, 10:30 AM",
    advancePaidAmount: Int = 520,
    onBackClick: () -> Unit = {},
    onConfirmCancellation: (reason: String) -> Unit = {}
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val cancellationReasons = remember {
        listOf(
            "Driver asked me to cancel the booking",
            "Driver is delayed / unable to reach pickup on time",
            "Change in travel plans or personal emergency",
            "Booked wrong date, time, or vehicle type",
            "Found alternative ride or public transport",
            "My meeting / event was cancelled",
            "Other reason"
        )
    }

    var selectedReason by remember { mutableStateOf(cancellationReasons[0]) }
    var otherReasonText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Enforce free cancellation policy calculation (API-42)
    val cancellationFee = 0
    val refundAmount = advancePaidAmount - cancellationFee

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        ChevronRightIcon(
                            size = 18.dp,
                            color = Color.White,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Cancel Booking",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, NavyDark)
                    ) {
                        Text(
                            text = "Keep Booking",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = NavyDark
                        )
                    }

                    Button(
                        onClick = {
                            isSubmitting = true
                            val finalReason = if (selectedReason == "Other reason" && otherReasonText.isNotBlank()) {
                                otherReasonText
                            } else {
                                selectedReason
                            }
                            onConfirmCancellation(finalReason)
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CancelRed)
                    ) {
                        Text(
                            text = "Confirm Cancel",
                            fontSize = 14.sp,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Trip Card Preview
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BOOKING REF: $bookingId",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = BrandOrange,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = travelDate,
                            fontSize = 12.sp,
                            fontFamily = dmSansFontFamily(),
                            color = TextMuted
                        )
                    }

                    Text(
                        text = "$origin → $destination",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                }
            }

            // 2. Refund & Cancellation Fee Notice Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.2.dp, SuccessGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Eligible for 100% Full Refund",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color(0xFF065F46)
                        )
                    }

                    Text(
                        text = "Because you are cancelling more than 2 hours before scheduled departure, zero cancellation charges apply.",
                        fontSize = 12.5.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF047857),
                        lineHeight = 17.sp
                    )

                    HorizontalDivider(color = SuccessGreen.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Advance Paid:", fontSize = 13.sp, color = TextMuted, fontFamily = dmSansFontFamily())
                        Text("₹$advancePaidAmount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cancellation Fee:", fontSize = 13.sp, color = TextMuted, fontFamily = dmSansFontFamily())
                        Text("₹$cancellationFee", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Refund:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, fontFamily = outfitFontFamily())
                        Text("₹$refundAmount", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen, fontFamily = outfitFontFamily())
                    }
                }
            }

            // 3. Reason Selector Section
            Text(
                text = "PLEASE SELECT A REASON TO CANCEL",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    cancellationReasons.forEachIndexed { index, reason ->
                        val isSelected = selectedReason == reason
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = CancelRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reason,
                                fontSize = 13.5.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextDark,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (index < cancellationReasons.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }

            // Optional text field if "Other reason" is selected
            AnimatedVisibility(visible = selectedReason == "Other reason") {
                OutlinedTextField(
                    value = otherReasonText,
                    onValueChange = { otherReasonText = it },
                    placeholder = { Text("Please describe your reason (optional)...", color = Color(0xFFA0AEC0), fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = BorderColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun CancelBookingScreenPreview() {
    GoIndiaCabTheme {
        CancelBookingScreen()
    }
}
