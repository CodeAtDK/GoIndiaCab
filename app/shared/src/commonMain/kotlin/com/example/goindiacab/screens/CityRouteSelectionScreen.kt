package com.example.goindiacab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.goindiacab.data.models.OutstationTripType
import com.example.goindiacab.data.models.PopularCityItem
import com.example.goindiacab.data.models.PopularRouteFare
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BrandBlue
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.TextMuted
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import com.example.goindiacab.viewmodel.OutstationRouteViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_back_arrow
import goindiacab.app.shared.generated.resources.img_monument_bengaluru
import goindiacab.app.shared.generated.resources.img_monument_chennai
import goindiacab.app.shared.generated.resources.img_monument_delhi
import goindiacab.app.shared.generated.resources.img_monument_hyderabad
import goindiacab.app.shared.generated.resources.img_monument_kolkata
import goindiacab.app.shared.generated.resources.img_monument_mumbai
import org.jetbrains.compose.resources.painterResource

/**
 * Screen 18: Select Outstation Route Screen (Phase 4).
 *
 * Architecture & Lifecycle Role:
 * - Allows passenger to configure trip modality (One Way, Round Trip, Airport Transfers, Hourly Rentals),
 *   select major metropolitan destination hubs, and pick popular predefined routes with transparent fixed pricing.
 * - Navigation Flow:
 *     - Selecting custom destination jumps forward to [SEARCH_DESTINATION].
 *     - Selecting a popular route (e.g. Delhi → Agra) computes distances and fares, then navigates to [ROUTE_CONFIRMATION].
 * - Back Button Contract:
 *     - Hardware back and top app bar both invoke [onBackClick], popping back cleanly to [HOME].
 */
@Composable
fun CityRouteSelectionScreen(
    viewModel: OutstationRouteViewModel = remember { AppContainer.createOutstationRouteViewModel() },
    onBackClick: () -> Unit,
    onSelectPickupClick: () -> Unit = onBackClick,
    onSelectDestinationClick: () -> Unit,
    onRouteSelected: (PopularRouteFare) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Hardware & gesture back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                OutstationTopBar(
                    title = "Select Outstation Route",
                    onBackClick = onBackClick
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. Trip Mode Segmented Tabs Row
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutstationTripType.entries.forEach { tripType ->
                            val isSelected = uiState.selectedTripType == tripType
                            Surface(
                                onClick = { viewModel.selectTripType(tripType) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) BrandBlue else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = tripType.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontFamily = dmSansFontFamily(),
                                        color = if (isSelected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 2. FROM / TO City Selection Card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                            // FROM CITY Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onSelectPickupClick)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BrandBlue)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "FROM CITY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (uiState.fromCity.isBlank()) "Select pickup location" else uiState.fromCity,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = if (uiState.fromCity.isBlank()) TextMuted else TextDark
                                    )
                                }
                            }

                            // Divider
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 22.dp, top = 10.dp, bottom = 10.dp),
                                color = Color(0xFFF1F5F9),
                                thickness = 1.dp
                            )

                            // TO CITY Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onSelectDestinationClick)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BrandOrange)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "TO CITY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (uiState.toCity.isBlank()) "Enter destination city" else uiState.toCity,
                                        fontSize = 15.sp,
                                        fontWeight = if (uiState.toCity.isBlank()) FontWeight.Medium else FontWeight.Bold,
                                        fontFamily = dmSansFontFamily(),
                                        color = if (uiState.toCity.isBlank()) Color(0xFF94A3B8) else TextDark
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 3. POPULAR CITIES Header & 2x3 Grid
                item {
                    Text(
                        text = "POPULAR CITIES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val cities = uiState.popularCities
                    for (i in cities.indices step 3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (j in 0 until 3) {
                                if (i + j < cities.size) {
                                    val city = cities[i + j]
                                    PopularCityCard(
                                        city = city,
                                        onClick = {
                                            viewModel.selectCity(city)
                                            val estimatedKm = when (city.name.lowercase()) {
                                                "agra" -> 230
                                                "jaipur" -> 280
                                                "mumbai" -> 1420
                                                "bengaluru" -> 2150
                                                "chennai" -> 2200
                                                "kolkata" -> 1530
                                                else -> 250
                                            }
                                            val estimatedFare = (estimatedKm * 12.5).toInt().coerceAtLeast(2100)
                                            val route = PopularRouteFare(
                                                id = "route_${city.name.lowercase()}",
                                                fromCity = uiState.fromCity,
                                                toCity = city.name,
                                                distanceKm = estimatedKm,
                                                priceInr = estimatedFare
                                            )
                                            onRouteSelected(route)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 4. POPULAR ONE-WAY ROUTES Header & List
                item {
                    Text(
                        text = "POPULAR ONE-WAY ROUTES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(
                    items = uiState.popularRoutes,
                    key = { it.id }
                ) { route ->
                    PopularRouteCard(
                        route = route,
                        onClick = {
                            viewModel.selectRoute(route)
                            onRouteSelected(route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PopularCityCard(
    city: PopularCityItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monumentDrawable = when (city.name.lowercase()) {
        "delhi" -> Res.drawable.img_monument_delhi
        "mumbai" -> Res.drawable.img_monument_mumbai
        "bengaluru" -> Res.drawable.img_monument_bengaluru
        "chennai" -> Res.drawable.img_monument_chennai
        "hyderabad" -> Res.drawable.img_monument_hyderabad
        "kolkata" -> Res.drawable.img_monument_kolkata
        else -> when (city.iconKey) {
            "monument" -> Res.drawable.img_monument_delhi
            "gateway" -> Res.drawable.img_monument_mumbai
            "tech" -> Res.drawable.img_monument_bengaluru
            "temple" -> Res.drawable.img_monument_chennai
            "charminar" -> Res.drawable.img_monument_hyderabad
            "bridge" -> Res.drawable.img_monument_kolkata
            else -> Res.drawable.img_monument_delhi
        }
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.height(92.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(monumentDrawable),
                    contentDescription = "${city.name} Monument",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = city.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmSansFontFamily(),
                color = TextDark,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PopularRouteCard(
    route: PopularRouteFare,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${route.fromCity} → ${route.toCity}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${route.distanceKm} km",
                    fontSize = 12.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${route.priceInr}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = BrandBlue
                )
                Text(
                    text = "onwards",
                    fontSize = 11.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted
                )
            }
        }
    }
}

/**
 * Reusable Dark Outstation Top Bar.
 */
@Composable
fun OutstationTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Surface(
        color = Color(0xFF0A1E42),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_back_arrow),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            // 3-dots overflow icon
            Text(
                text = "⋮",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}
