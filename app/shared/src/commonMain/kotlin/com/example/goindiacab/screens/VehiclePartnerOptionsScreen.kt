package com.example.goindiacab.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import com.example.goindiacab.viewmodel.VehicleSelectionAction
import com.example.goindiacab.viewmodel.VehicleSelectionUiState
import com.example.goindiacab.viewmodel.VehicleSelectionViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Design Palette matching SVG #25
private val NavyHeaderColor = Color(0xFF0A2540)
private val LightOrangeBg = Color(0xFFFFF0E6)
private val OrangeBorder = Color(0xFFFF6B00)
private val SelectedBlueBorder = Color(0xFF2563EB)
private val RecommendedGreenBg = Color(0xFFE6F7F0)
private val RecommendedGreenText = Color(0xFF059669)
private val CardBorderColor = Color(0xFFE2E8F0)
private val ScreenBgColor = Color(0xFFF8FAFC)
private val WarningBannerBg = Color(0xFFFFF0E6)
private val WarningBannerText = Color(0xFF9A3412)

/**
 * Screen 25: Choose Your Vehicle / Vehicle Partner Options.
 * Production-ready, fully responsive, MVVM-driven screen with exhaustive state modeling,
 * reactive filtering, smooth animations, and edge-to-edge system insets.
 */
@Composable
fun VehiclePartnerOptionsScreen(
    viewModel: VehicleSelectionViewModel = remember { AppContainer.createVehicleSelectionViewModel() },
    onBackClick: () -> Unit,
    onVehicleSelected: (VehiclePartnerOption) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    // Intercept hardware/system back gesture cleanly
    PlatformBackHandler(enabled = true) {
        if (showInfoDialog) {
            showInfoDialog = false
        } else {
            onBackClick()
        }
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = ScreenBgColor,
            topBar = {
                VehicleOptionsTopBar(
                    onBackClick = onBackClick,
                    onInfoClick = { showInfoDialog = true }
                )
            },
            bottomBar = {
                val successState = uiState as? VehicleSelectionUiState.Success
                val selectedVehicle = successState?.selectedVehicle
                if (selectedVehicle != null) {
                    VehicleStickyBottomBar(
                        selectedVehicle = selectedVehicle,
                        onContinueClick = {
                            viewModel.handleAction(
                                VehicleSelectionAction.BookVehicle(selectedVehicle.id) { vehicle ->
                                    onVehicleSelected(vehicle)
                                }
                            )
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is VehicleSelectionUiState.Loading -> {
                        VehicleSelectionLoadingState()
                    }

                    is VehicleSelectionUiState.Error -> {
                        VehicleSelectionErrorState(
                            message = state.message,
                            onRetry = { viewModel.handleAction(VehicleSelectionAction.Retry) }
                        )
                    }

                    is VehicleSelectionUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            // 1. Route Summary Header Card
                            item {
                                Spacer(modifier = Modifier.height(14.dp))
                                RouteSummaryCard(
                                    header = state.routeHeader,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // 2. Horizontal Filter Chips Row
                            item {
                                CategoryFilterRow(
                                    selectedCategory = state.selectedCategory,
                                    onCategorySelected = { category ->
                                        viewModel.handleAction(VehicleSelectionAction.SelectCategory(category))
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // 3. Standard Vehicle Options List (Sedan, SUV, Premium)
                            items(
                                items = state.standardVehicles,
                                key = { it.id }
                            ) { vehicle ->
                                val isSelected = vehicle.id == state.selectedVehicleId
                                StandardVehicleCard(
                                    vehicle = vehicle,
                                    isSelected = isSelected,
                                    onClick = {
                                        viewModel.handleAction(VehicleSelectionAction.SelectVehicle(vehicle.id))
                                    },
                                    onBookClick = {
                                        viewModel.handleAction(
                                            VehicleSelectionAction.BookVehicle(vehicle.id) {
                                                onVehicleSelected(it)
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }

                            // 4. Group Travel Section (if any in filtered items)
                            if (state.hasGroupTravel) {
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Group Travel",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextDark,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                items(
                                    items = state.groupTravelVehicles,
                                    key = { it.id }
                                ) { vehicle ->
                                    val isSelected = vehicle.id == state.selectedVehicleId
                                    GroupTravelCard(
                                        vehicle = vehicle,
                                        isSelected = isSelected,
                                        onClick = {
                                            viewModel.handleAction(VehicleSelectionAction.SelectVehicle(vehicle.id))
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            // 5. Night Charges & Policy Alert Card
                            item {
                                Spacer(modifier = Modifier.height(18.dp))
                                PolicyNoticeBanner(
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Vehicle Tiers / Inclusions Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Vehicle Fare Inclusions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "• All-Inclusive Fares: Base fare includes fuel, driver allowance, and standard route toll charges.",
                        fontSize = 13.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF475569),
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "• Free KM Limit: Standard booking covers up to 250 km. Additional kilometers will be charged at the per-km rate indicated on each vehicle.",
                        fontSize = 13.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF475569),
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "• Night Surcharge: Rides between 10:00 PM and 6:00 AM incur standard driver night charges (₹250 - ₹500).",
                        fontSize = 13.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF475569),
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Got It", fontFamily = outfitFontFamily(), fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

/**
 * Navy Blue Top Bar with Back button, Title, and Info button.
 */
@Composable
private fun VehicleOptionsTopBar(
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Surface(
        color = NavyHeaderColor,
        modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = "Choose Your Vehicle",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            IconButton(onClick = onInfoClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_info_circle),
                    contentDescription = "Fare Inclusions Info",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Route Summary Card ("Delhi -> Agra", "230 km - One Way - Today, 10:30 AM", Price Badge).
 */
@Composable
private fun RouteSummaryCard(
    header: RouteSummaryHeader,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${header.origin} → ${header.destination}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${header.distanceKm} km • ${header.tripType} • ${header.departureTime}",
                    fontSize = 13.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LightOrangeBg
            ) {
                Text(
                    text = header.basePrice.formatInr(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = OrangeBorder,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Category Filter Chips Row (All, Sedan, SUV, Premium, Group Travel).
 */
@Composable
private fun CategoryFilterRow(
    selectedCategory: VehicleCategory,
    onCategorySelected: (VehicleCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VehicleCategory.entries.forEach { category ->
            val isSelected = category == selectedCategory
            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) LightOrangeBg else Color.White,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) OrangeBorder else CardBorderColor
                ),
                modifier = Modifier.height(38.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.displayName,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontFamily = outfitFontFamily(),
                        color = if (isSelected) OrangeBorder else Color(0xFF475569)
                    )
                }
            }
        }
    }
}

/**
 * Standard Vehicle Card (Sedan / SUV / Premium) matching Screen 25.
 */
@Composable
private fun StandardVehicleCard(
    vehicle: VehiclePartnerOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) SelectedBlueBorder else CardBorderColor
        ),
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // "RECOMMENDED" Badge (if applicable)
            if (vehicle.badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RecommendedGreenBg,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = vehicle.badgeText.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = RecommendedGreenText,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Top Row: Title, Subtitle, Rating vs Car Photo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vehicle.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = vehicle.typeDescription,
                        fontSize = 13.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★",
                            fontSize = 13.sp,
                            color = Color(0xFFF59E0B)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${vehicle.rating}★",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = dmSansFontFamily(),
                            color = TextDark
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Vehicle Image
                val painter = when (vehicle.imageDrawableKey) {
                    "img_car_swift_dzire" -> painterResource(Res.drawable.img_car_swift_dzire)
                    "img_car_innova_crysta" -> painterResource(Res.drawable.img_car_innova_crysta)
                    else -> painterResource(Res.drawable.ic_tempo_traveller)
                }

                Image(
                    painter = painter,
                    contentDescription = vehicle.name,
                    modifier = Modifier
                        .width(130.dp)
                        .height(78.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dashed Divider
            DashedDivider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(14.dp))

            // Price & Book Now CTA Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "All-Inclusive Fare",
                        fontSize = 12.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = vehicle.allInclusiveFare.formatInr(),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = BrandOrange
                    )
                }

                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Book Now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Extra charges microcopy
            Text(
                text = "Extra charges: ₹${vehicle.extraKmRate}/km after ${vehicle.freeKmLimit} km • Night charges ₹${vehicle.nightCharges} (${vehicle.nightChargeWindow})",
                fontSize = 11.5.sp,
                fontFamily = dmSansFontFamily(),
                color = Color(0xFF64748B),
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Group Travel Card (12, 16, 26 Seater Tempo Travellers) matching Screen 25.
 */
@Composable
private fun GroupTravelCard(
    vehicle: VehiclePartnerOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) SelectedBlueBorder else CardBorderColor
        ),
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Minibus / Tempo icon container
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_tempo_traveller),
                    contentDescription = vehicle.name,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehicle.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★",
                        fontSize = 12.sp,
                        color = Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${vehicle.rating}★",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = dmSansFontFamily(),
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Feature Pill Badges: Seats, Diesel, AC
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("${vehicle.seatingCapacity} seats", "Diesel", "AC").forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = dmSansFontFamily(),
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Per-km rate and extra charges
                if (vehicle.perKmRate != null) {
                    Text(
                        text = "Per km rate: ₹${vehicle.perKmRate}/km",
                        fontSize = 12.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "Extra ₹${vehicle.extraKmRate}/km after ${vehicle.freeKmLimit} km",
                    fontSize = 12.sp,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = vehicle.allInclusiveFare.formatInr(),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = BrandOrange
                )

                if (vehicle.badgeText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = RecommendedGreenBg
                    ) {
                        Text(
                            text = vehicle.badgeText.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = RecommendedGreenText,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Notice banner: "Night charges apply between 10PM and 6AM. Extra per km charges apply after 250 km."
 */
@Composable
private fun PolicyNoticeBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = WarningBannerBg,
        border = BorderStroke(1.dp, OrangeBorder.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDBA74).copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_shield_alert),
                    contentDescription = "Night charges policy alert",
                    tint = OrangeBorder,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Night charges apply between 10PM and 6AM. Extra per km charges apply after 250 km.",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = dmSansFontFamily(),
                color = WarningBannerText,
                lineHeight = 17.sp
            )
        }
    }
}

/**
 * Sticky Bottom Bar displaying Selected Vehicle Name, price, and "Continue" CTA button.
 */
@Composable
private fun VehicleStickyBottomBar(
    selectedVehicle: VehiclePartnerOption,
    onContinueClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SELECTED VEHICLE",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = selectedVehicle.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${selectedVehicle.allInclusiveFare.formatInr()} • One Way",
                    fontSize = 13.sp,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onContinueClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                modifier = Modifier
                    .width(136.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Custom Dashed Divider matching the Figma SVG design.
 */
@Composable
private fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = CardBorderColor,
    dashWidth: Dp = 4.dp,
    dashGap: Dp = 4.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val dashWidthPx = dashWidth.toPx()
        val dashGapPx = dashGap.toPx()
        var currentX = 0f
        while (currentX < size.width) {
            drawLine(
                color = color,
                start = Offset(currentX, 0f),
                end = Offset(minOf(currentX + dashWidthPx, size.width), 0f),
                strokeWidth = 1.dp.toPx()
            )
            currentX += dashWidthPx + dashGapPx
        }
    }
}

/**
 * Loading shimmer / indicator state.
 */
@Composable
private fun VehicleSelectionLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = BrandOrange,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Finding available vehicles...",
                fontSize = 14.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted
            )
        }
    }
}

/**
 * Error state with retry action.
 */
@Composable
private fun VehicleSelectionErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "⚠️",
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Unable to load vehicles",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Retry", fontFamily = outfitFontFamily(), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
