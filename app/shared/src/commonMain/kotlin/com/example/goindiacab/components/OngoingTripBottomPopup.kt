package com.example.goindiacab.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.outfitFontFamily
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_cab
import org.jetbrains.compose.resources.painterResource

private val PopupNavy = Color(0xFF0A1128)

/**
 * Data model for the Ongoing Trip floating snippet preview.
 */
data class OngoingTripSnippet(
    val tripId: String = "TRIP-8492",
    val routeTitle: String = "Delhi ➔ Agra ➔ Jaipur ➔ Delhi",
    val tripType: String = "Round Trip • Day 2 of 4",
    val currentStop: String = "Current Stop: Agra (Stop 2)",
    val driverName: String = "Rajesh Kumar",
    val vehicleModel: String = "Toyota Innova Crysta",
    val vehicleNumber: String = "DL 01 AB 1234",
    val statusText: String = "Driver on route • 18 km to stop",
    val pickupOtp: String = "4829"
)

/**
 * Ongoing Trip Bottom Pop-Up component.
 * Displays an interactive, pulsing live trip status card docked right above
 * the bottom navigation bar on the Home Screen.
 * Clicking the card opens the full OngoingTripScreen.
 */
@Composable
fun OngoingTripBottomPopup(
    tripSnippet: OngoingTripSnippet = OngoingTripSnippet(),
    isDismissed: Boolean = false,
    onCardClick: () -> Unit,
    onDismiss: () -> Unit = {},
    onReopen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Pulsing live indicator animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    AnimatedVisibility(
        visible = !isDismissed,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickable { onCardClick() },
            shape = RoundedCornerShape(16.dp),
            color = PopupNavy,
            border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.65f)),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Header: Live Badge + OTP + Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Pulsing Green Live Dot
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size((14 * pulseScale).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = pulseAlpha * 0.4f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                        }

                        Text(
                            text = "LIVE TRIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = Color(0xFF10B981),
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )

                        Text(
                            text = tripSnippet.tripType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = outfitFontFamily(),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // OTP Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Text(
                                text = "OTP: ${tripSnippet.pickupOtp}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = BrandOrange,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                            )
                        }

                        // Close button
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable { onDismiss() }
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CloseIcon(size = 14.dp, color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }

                // Middle Row: Cab Icon + Route + Details + Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cab Icon inside orange container
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandOrange.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_cab),
                            contentDescription = "Cab",
                            tint = BrandOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Trip Info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = tripSnippet.routeTitle,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = tripSnippet.currentStop,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = outfitFontFamily(),
                            color = Color(0xFFFBBF24), // amber gold
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${tripSnippet.driverName} • ${tripSnippet.vehicleModel}",
                            fontSize = 11.sp,
                            fontFamily = outfitFontFamily(),
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Action Button / Arrow
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BrandOrange,
                        modifier = Modifier.clickable { onCardClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Track",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )
                            ChevronRightIcon(size = 13.dp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Minimized Pill Bar when dismissed
    AnimatedVisibility(
        visible = isDismissed,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .clickable { onReopen() },
            shape = RoundedCornerShape(12.dp),
            color = PopupNavy,
            border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.5f)),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Text(
                        text = "Ongoing: ${tripSnippet.routeTitle}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Tap to open",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = BrandOrange
                    )
                    ChevronRightIcon(size = 12.dp, color = BrandOrange)
                }
            }
        }
    }
}
