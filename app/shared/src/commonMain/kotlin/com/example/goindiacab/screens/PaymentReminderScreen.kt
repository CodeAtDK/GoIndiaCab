package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.theme.outfitFontFamily
import com.example.goindiacab.viewmodel.PaymentReminderUiState
import com.example.goindiacab.viewmodel.PaymentReminderViewModel

/**
 * Payment Reminder Popup Screen matching payment-reminder-popup.svg.
 *
 * Displays an urgent milestone payment reminder card (e.g. 30% mid-trip payment)
 * centered over a dimmed backdrop, allowing riders to either "Pay Now" or "Remind Later" (snooze).
 */
@Composable
fun PaymentReminderScreen(
    viewModel: PaymentReminderViewModel = remember { AppContainer.createPaymentReminderViewModel() },
    onPayNowClick: () -> Unit = {},
    onRemindLaterClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Android hardware / gesture back handler
    PlatformBackHandler(enabled = true) {
        viewModel.onRemindLaterClicked()
        onBackClick()
    }

    LaunchedEffect(uiState.isProceedingToPayment) {
        if (uiState.isProceedingToPayment) {
            onPayNowClick()
            viewModel.resetState()
        }
    }

    LaunchedEffect(uiState.isDismissed) {
        if (uiState.isDismissed) {
            onRemindLaterClick()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    viewModel.onRemindLaterClicked()
                    onBackClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle simulated background card to mirror Figma SVG layered layout
        SimulatedBackgroundLayer(modifier = Modifier.fillMaxSize())

        // Modal Popup Card
        PaymentReminderPopup(
            uiState = uiState,
            onPayNowClick = { viewModel.onPayNowClicked() },
            onRemindLaterClick = {
                viewModel.onRemindLaterClicked()
                onBackClick()
            },
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Prevents click-through dismissal
                )
        )
    }
}

/**
 * Reusable Payment Reminder Card matching payment-reminder-popup.svg.
 */
@Composable
fun PaymentReminderPopup(
    uiState: PaymentReminderUiState,
    onPayNowClick: () -> Unit,
    onRemindLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Alert Circle Icon (Peach circle with orange cross badge)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF1E8)),
                contentAlignment = Alignment.Center
            ) {
                // Inner circle with orange outline and '✕'
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, BrandOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(14.dp)) {
                        val strokeW = 2.dp.toPx()
                        // Cross lines
                        drawLine(
                            color = BrandOrange,
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = BrandOrange,
                            start = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            end = androidx.compose.ui.geometry.Offset(0f, size.height),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Bold Headline
            Text(
                text = uiState.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Highlighted Description text
            val annotatedDescription = buildAnnotatedString {
                append("Your ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF111827))) {
                    append("${uiState.milestonePercent}% ${uiState.milestoneName}")
                }
                append(" for your ${uiState.routeOrigin} to ${uiState.routeDestination} trip is due in ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BrandOrange)) {
                    append("${uiState.dueInMinutes} minutes")
                }
                append(". Please complete the payment to avoid any disruption.")
            }

            Text(
                text = annotatedDescription,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFF4B5563),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Amount Due Container Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3F6FA))
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AMOUNT DUE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹${formatCurrency(uiState.amountDueInr)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = Color(0xFF111827)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Dual Action Buttons: Remind Later & Pay Now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Remind Later Button (Outlined)
                OutlinedButton(
                    onClick = onRemindLaterClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1F2937)
                    )
                ) {
                    Text(
                        text = "Remind Later",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = outfitFontFamily()
                    )
                }

                // Pay Now Button (Primary Brand Orange)
                Button(
                    onClick = onPayNowClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp), spotColor = BrandOrange.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Pay Now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Footer subtext
            Text(
                text = "You will be reminded again in ${uiState.remindAgainMinutes} minutes",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Subtle simulated background mirroring the SVG layered screenshot
 */
@Composable
private fun SimulatedBackgroundLayer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF4A5568).copy(alpha = 0.3f))
    )
}

private fun formatCurrency(amount: Int): String {
    val s = amount.toString()
    if (s.length <= 3) return s
    val lastThree = s.substring(s.length - 3)
    val remaining = s.substring(0, s.length - 3)
    val formatted = StringBuilder()
    var count = 0
    for (i in remaining.length - 1 downTo 0) {
        formatted.insert(0, remaining[i])
        count++
        if (count == 2 && i != 0) {
            formatted.insert(0, ',')
            count = 0
        }
    }
    return "$formatted,$lastThree"
}

@Preview
@Composable
fun PaymentReminderScreenPreview() {
    GoIndiaCabTheme {
        PaymentReminderScreen(
            viewModel = remember {
                PaymentReminderViewModel(
                    initialAmountDue = 3150,
                    initialRouteOrigin = "Delhi",
                    initialRouteDestination = "Jaipur"
                )
            }
        )
    }
}
