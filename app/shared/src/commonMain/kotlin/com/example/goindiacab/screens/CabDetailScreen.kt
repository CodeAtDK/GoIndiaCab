package com.example.goindiacab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.*
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.TextMuted
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.viewmodel.CabDetailAction
import com.example.goindiacab.viewmodel.CabDetailUiState
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val NavyHeaderBg = Color(0xFF0A2540)
private val ScreenBg = Color(0xFFF4F6F9)
private val CardBorderColor = Color(0xFFE5E7EB)
private val SpecBoxBg = Color(0xFFF9FAFB)
private val GreenBadgeBg = Color(0xFFECFDF5)
private val GreenBadgeText = Color(0xFF10B981)
private val AmberStar = Color(0xFFF59E0B)

/**
 * Screen 27: Review Cab Details.
 * Production-ready screen strictly reproducing Screen in SVG/27. cab-detail.svg.
 * Fully responsive across Android/iOS/Desktop/Web with edge-to-edge support.
 */
@Composable
fun CabDetailScreen(
    viewModel: BookingFlowViewModel = remember { AppContainer.createBookingFlowViewModel() },
    onBackClick: () -> Unit,
    onBookNowClick: (CabDetailData) -> Unit,
    onViewFareDetailsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showOverflowMenu by remember { mutableStateOf(false) }

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = ScreenBg,
            topBar = {
                CabDetailTopBar(
                    onBackClick = onBackClick,
                    onOverflowClick = { showOverflowMenu = !showOverflowMenu }
                )
            },
            bottomBar = {
                val detail = (uiState as? CabDetailUiState.Success)?.detail
                if (detail != null) {
                    CabDetailStickyBottomBar(
                        detail = detail,
                        onBookNowClick = { onBookNowClick(detail) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val state = uiState) {
                    is CabDetailUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BrandOrange)
                        }
                    }
                    is CabDetailUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 15.sp,
                                    fontFamily = outfitFontFamily()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.handleAction(CabDetailAction.Refresh) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                                ) {
                                    Text("Retry", fontFamily = outfitFontFamily())
                                }
                            }
                        }
                    }
                    is CabDetailUiState.Success -> {
                        val detail = state.detail
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // 1. Scenic Highway Hero Banner
                            item {
                                Image(
                                    painter = painterResource(Res.drawable.img_cab_detail_hero),
                                    contentDescription = "Cab Banner",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(210.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // 2. Vehicle Overview & Specs Card
                            item {
                                VehicleSpecsCard(detail = detail)
                            }

                            // 3. Inclusions Card
                            item {
                                InclusionsCard(inclusions = detail.inclusions)
                            }

                            // 4. Exclusions Card
                            item {
                                ExclusionsCard(exclusions = detail.exclusions)
                            }

                            // 5. Hourly Charges Card
                            item {
                                HourlyChargesCard(charges = detail.hourlyCharges)
                            }

                            // 6. Cancellation Policy Card
                            item {
                                CancellationPolicyCard(policy = detail.cancellationPolicy)
                            }

                            // 7. Vehicle Fleet Reviews Card
                            item {
                                FleetReviewCard(review = detail.review)
                            }

                            // 8. Fare Breakdown Card
                            item {
                                FareBreakdownCard(
                                    fare = detail.fareBreakdown,
                                    onViewFareDetailsClick = onViewFareDetailsClick
                                )
                            }

                            // 9. Payment Details Note Card
                            item {
                                PaymentDetailsCard(terms = detail.paymentTermsText)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Navy Top Bar with Back Arrow, Title, and Overflow Menu.
 */
@Composable
private fun CabDetailTopBar(
    onBackClick: () -> Unit,
    onOverflowClick: () -> Unit
) {
    Surface(
        color = NavyHeaderBg,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(60.dp)
                .padding(horizontal = 8.dp),
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

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Review Cab Details",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onOverflowClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_more_vert),
                    contentDescription = "More Options",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Vehicle Name, Category/Features, and 2x2 Specs Grid.
 */
@Composable
private fun VehicleSpecsCard(detail: CabDetailData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Vehicle Title Row + Rating Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = detail.vehicle.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenBadgeBg
                ) {
                    Text(
                        text = "${detail.vehicle.rating} ★",
                        color = GreenBadgeText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = dmSansFontFamily(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle description (e.g. Premium SUV • 6 Seater • AC • Diesel)
            Text(
                text = detail.vehicle.typeDescription,
                fontSize = 14.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Vehicle Specs",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2x2 Grid of Spec Boxes
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecBox(
                        title = "FUEL TYPE",
                        value = detail.specs.fuelType,
                        modifier = Modifier.weight(1f)
                    )
                    SpecBox(
                        title = "TRANSMISSION",
                        value = detail.specs.transmission,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecBox(
                        title = "LUGGAGE",
                        value = detail.specs.luggageCapacity,
                        modifier = Modifier.weight(1f)
                    )
                    SpecBox(
                        title = "AC TYPE",
                        value = detail.specs.acType,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Individual specification pill box.
 */
@Composable
private fun SpecBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SpecBoxBg
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmSansFontFamily(),
                color = Color(0xFF6B7280),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
        }
    }
}

/**
 * Inclusions Card with green checkmark circles.
 */
@Composable
private fun InclusionsCard(inclusions: List<InclusionItem>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Inclusions",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                inclusions.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_check_circle_green),
                            contentDescription = null,
                            tint = GreenBadgeText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.text,
                            fontSize = 14.sp,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFF374151)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Exclusions Card with orange alert circle icons.
 */
@Composable
private fun ExclusionsCard(exclusions: List<ExclusionItem>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Exclusions",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                exclusions.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_alert_circle_orange),
                            contentDescription = null,
                            tint = AmberStar,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.text,
                            fontSize = 14.sp,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFF374151)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Hourly Charges Table Card.
 */
@Composable
private fun HourlyChargesCard(charges: List<HourlyCharge>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hourly Charges",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                charges.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.durationText,
                            fontSize = 14.sp,
                            fontFamily = dmSansFontFamily(),
                            color = Color(0xFF4B5563)
                        )
                        Text(
                            text = "₹${item.fareAmount}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cancellation Policy Notice Card.
 */
@Composable
private fun CancellationPolicyCard(policy: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Cancellation Policy",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = policy,
                fontSize = 14.sp,
                fontFamily = dmSansFontFamily(),
                color = Color(0xFF4B5563),
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * Vehicle Fleet Reviews Card with driver avatar, star rating, and quote.
 */
@Composable
private fun FleetReviewCard(review: DriverReview) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Vehicle Fleet Reviews",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Driver/Reviewer row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_driver_portrait),
                    contentDescription = review.reviewerName,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.reviewerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                    Text(
                        text = review.timeAgoText,
                        fontSize = 12.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF9CA3AF)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★",
                        color = AmberStar,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${review.rating}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = review.comment,
                fontSize = 13.sp,
                fontFamily = dmSansFontFamily(),
                color = Color(0xFF4B5563),
                lineHeight = 19.sp
            )
        }
    }
}

/**
 * Itemized Fare Breakdown Card.
 */
@Composable
private fun FareBreakdownCard(
    fare: ItemizedFareBreakdown,
    onViewFareDetailsClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fare Breakdown",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )

                Text(
                    text = "View Details >",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF0052CC),
                    modifier = Modifier.clickable { onViewFareDetailsClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Base Fare
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Base Fare (${fare.baseKmLimit} km limit)",
                    fontSize = 14.sp,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF4B5563)
                )
                Text(
                    text = "₹${fare.baseFare}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toll & State Taxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Estimated Toll & State Taxes",
                    fontSize = 14.sp,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF4B5563)
                )
                Text(
                    text = if (fare.tollAndStateTaxIncluded) "Included" else "₹${fare.tollAndStateTaxAmount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = dmSansFontFamily(),
                    color = GreenBadgeText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // GST & Booking fees
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "GST & Booking fees",
                    fontSize = 14.sp,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF4B5563)
                )
                Text(
                    text = "₹${fare.gstAndBookingFees}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
            }

            if (fare.discountAmount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Coupon Discount (${fare.couponCode ?: ""})",
                        fontSize = 14.sp,
                        fontFamily = dmSansFontFamily(),
                        color = GreenBadgeText
                    )
                    Text(
                        text = "-₹${fare.discountAmount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = GreenBadgeText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Total Estimated Fare Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Estimated Fare",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
                Text(
                    text = "₹${fare.totalEstimatedFare}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = BrandOrange
                )
            }
        }
    }
}

/**
 * Payment terms and partner guarantee note.
 */
@Composable
private fun PaymentDetailsCard(terms: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Payment details",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = terms,
                fontSize = 13.sp,
                fontFamily = dmSansFontFamily(),
                color = Color(0xFF4B5563),
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * Sticky Bottom Bar matching Screen 27.
 */
@Composable
private fun CabDetailStickyBottomBar(
    detail: CabDetailData,
    onBookNowClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOTAL FARE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "₹${detail.fareBreakdown.totalEstimatedFare}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = BrandOrange
                )
            }

            Button(
                onClick = onBookNowClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Book Now",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily()
                )
            }
        }
    }
}
