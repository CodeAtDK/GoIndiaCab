package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.di.AppContainer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.CouponItem
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.viewmodel.CabDetailAction
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_back_arrow
import goindiacab.app.shared.generated.resources.ic_check_circle_green
import goindiacab.app.shared.generated.resources.ic_more_vert
import org.jetbrains.compose.resources.painterResource

private val ColorNavy = Color(0xFF0D1E3A)
private val ColorOrange = Color(0xFFFF6B00)
private val ColorBlue = Color(0xFF0052CC)
private val ColorGreen = Color(0xFF10B981)
private val ColorGreenBg = Color(0xFFECFDF5)
private val ColorBg = Color(0xFFF4F6F9)
private val ColorTextPrimary = Color(0xFF111827)
private val ColorTextSecondary = Color(0xFF6B7280)

/**
 * Screen 29: Apply Coupon / Promo Offers (Phase 6).
 *
 * Architecture & Lifecycle Role:
 * - Allows user to enter promo codes or choose from curated discount cards (e.g., FIRST500, OUTSTATION10).
 * - Applying a coupon immediately calculates the updated fare and discount in [BookingFlowViewModel].
 * - Back Button Contract:
 *     - Hardware back and top back arrow invoke [onBackClick], returning to [BOOKING_SUMMARY].
 */
@Composable
fun ApplyCouponScreen(
    viewModel: BookingFlowViewModel,
    onBackClick: () -> Unit,
    onCouponApplied: () -> Unit = onBackClick,
    modifier: Modifier = Modifier
) {
    val session by viewModel.bookingSession.collectAsState()
    val availableCoupons by viewModel.availableCoupons.collectAsState()
    val appliedCoupon = session.appliedCoupon
    val focusManager = LocalFocusManager.current

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    var couponInput by remember(appliedCoupon) {
        mutableStateOf(appliedCoupon?.code ?: "")
    }

    AdaptiveContainer(
        modifier = modifier.fillMaxSize(),
        backgroundColor = ColorBg
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Navy Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorNavy)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Apply Coupon",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )

                IconButton(onClick = { /* Menu */ }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_more_vert),
                        contentDescription = "More options",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input Row Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = couponInput,
                        onValueChange = { couponInput = it.uppercase() },
                        placeholder = { Text("Enter coupon code", color = Color(0xFF9CA3AF), fontSize = 15.sp) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (couponInput.isNotBlank()) {
                                    viewModel.handleAction(CabDetailAction.ApplyCoupon(couponInput))
                                    onCouponApplied()
                                }
                            }
                        )
                    )

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (couponInput.isNotBlank()) {
                                viewModel.handleAction(CabDetailAction.ApplyCoupon(couponInput))
                                onCouponApplied()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorOrange),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Apply",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                // Active Coupon Success Banner
                if (appliedCoupon != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColorGreenBg, RoundedCornerShape(12.dp))
                            .border(1.dp, ColorGreen, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_check_circle_green),
                            contentDescription = null,
                            tint = ColorGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "✓ ${appliedCoupon.code} applied! Saving ₹${appliedCoupon.discountAmount}.",
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Section Title: AVAILABLE COUPONS
                Text(
                    text = "AVAILABLE COUPONS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorNavy,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Coupon Cards List
                availableCoupons.forEach { coupon ->
                    CouponOfferCard(
                        coupon = coupon,
                        isCurrentlyApplied = appliedCoupon?.code.equals(coupon.code, ignoreCase = true),
                        onApplyClick = {
                            viewModel.handleAction(CabDetailAction.SelectCouponItem(coupon))
                            onCouponApplied()
                        },
                        onRemoveClick = {
                            viewModel.handleAction(CabDetailAction.RemoveCoupon)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CouponOfferCard(
    coupon: CouponItem,
    isCurrentlyApplied: Boolean,
    onApplyClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Code tag & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dashed Orange Code Badge
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = ColorOrange,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(Color(0xFFFFF7ED), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = coupon.code,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorOrange
                    )
                }

                if (isCurrentlyApplied) {
                    Text(
                        text = "APPLIED",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBlue,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onRemoveClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                } else {
                    Text(
                        text = "APPLY",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBlue,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onApplyClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Description
            Text(
                text = coupon.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextPrimary,
                lineHeight = 19.sp
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFFF3F4F6)
            )

            // Subtitle & validity
            Text(
                text = coupon.subtitle,
                fontSize = 12.sp,
                color = ColorTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Preview
@Composable
fun ApplyCouponScreenPreview() {
    GoIndiaCabTheme {
        ApplyCouponScreen(
            viewModel = AppContainer.createBookingFlowViewModel(),
            onBackClick = {}
        )
    }
}
