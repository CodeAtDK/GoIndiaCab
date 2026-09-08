package com.example.goindiacab.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.data.models.*
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.*
import com.example.goindiacab.viewmodel.HomeUiState
import com.example.goindiacab.viewmodel.HomeViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Color Tokens matching 15. home-screen.svg
private val HomeBg = Color(0xFFF4F6F9)
private val TopBarGradientStart = Color(0xFF0052CC)
private val TopBarGradientEnd = Color(0xFF091E42)
private val BrandBlue = Color(0xFF0052CC)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val TextPlaceholder = Color(0xFF9CA3AF)
private val BorderGray = Color(0xFFE5E7EB)
private val AccentOrange = Color(0xFFFF6B00)
private val ActiveChipBg = Color(0xFFE6F0FF)

enum class HomeBottomTab(val label: String) {
    HOME("Home"),
    MY_TRIPS("My Trips"),
    OFFERS("Offers"),
    WALLET("Wallet"),
    PROFILE("Profile")
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = remember { AppContainer.createHomeViewModel() },
    onOpenDrawer: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onViewAllRoutesClick: () -> Unit = {},
    onViewAllTripsClick: () -> Unit = {},
    onSelectPickupClick: () -> Unit = {},
    onSelectDropClick: () -> Unit = {},
    onOutstationClick: () -> Unit = {},
    onPopularRouteClick: (com.example.goindiacab.data.models.PopularRoute) -> Unit = {},
    onPromoBannerClick: (com.example.goindiacab.data.models.PromoBanner) -> Unit = {},
    onBookAgainClick: (com.example.goindiacab.data.models.RecentTrip) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var activeTab by remember { mutableStateOf(HomeBottomTab.HOME) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept back button when side drawer is open
    PlatformBackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
    }

    // Intercept back button when non-home tab is active
    PlatformBackHandler(enabled = !isDrawerOpen && activeTab != HomeBottomTab.HOME) {
        activeTab = HomeBottomTab.HOME
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearUserMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Pinned Top Bar
            HomeTopBar(
                onMenuClick = { isDrawerOpen = true },
                onNotificationClick = onNotificationsClick
            )

            // 2. Optimized Tab Body Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    HomeBottomTab.HOME -> {
                        if (uiState.isLoading && uiState.feedData == null) {
                            // Loading skeleton state
                            HomeLoadingSkeleton()
                        } else {
                            val feed = uiState.feedData
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                // A. Hero Service Types Grid
                                item(key = "hero_service_types", contentType = "hero") {
                            ServiceTypesHeroCard(
                                selectedService = uiState.selectedService,
                                onServiceSelected = { viewModel.selectService(it) }
                            )
                        }

                        // B. Trip Booking Card
                        item(key = "trip_booking_card", contentType = "booking") {
                            TripBookingCard(
                                pickup = uiState.pickupLocation,
                                drop = uiState.dropLocation,
                                dateTime = uiState.pickupDateTime,
                                vehicle = uiState.vehicleType,
                                onSearchClick = { viewModel.searchCabs() }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // C. Popular Routes Header
                        item(key = "popular_routes_header", contentType = "header") {
                            SectionHeader(
                                title = "Popular Routes",
                                actionText = "View All",
                                onActionClick = onViewAllRoutesClick
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // D. Popular Routes Horizontal Carousel
                        item(key = "popular_routes_carousel", contentType = "carousel") {
                            if (feed != null && feed.popularRoutes.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = feed.popularRoutes,
                                        key = { it.id },
                                        contentType = { "route_card" }
                                    ) { route ->
                                        PopularRouteCard(route = route)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(22.dp))
                        }

                        // E. Recent Trips Header
                        item(key = "recent_trips_header", contentType = "header") {
                            SectionHeader(
                                title = "Your Recent Trips",
                                actionText = "View All",
                                onActionClick = {
                                    activeTab = HomeBottomTab.MY_TRIPS
                                    onViewAllTripsClick()
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // F. Recent Trips Individual Items (Lazy recycled)
                        if (feed != null && feed.recentTrips.isNotEmpty()) {
                            items(
                                items = feed.recentTrips,
                                key = { it.id },
                                contentType = { "recent_trip_card" }
                            ) { trip ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                                    RecentTripCard(
                                        trip = trip,
                                        onBookAgain = {
                                            viewModel.bookAgain(trip)
                                            onBookAgainClick(trip)
                                        }
                                    )
                                }
                            }
                        }

                        // G. Airport Discount Promo Banner
                        item(key = "promo_banner", contentType = "banner") {
                            Spacer(modifier = Modifier.height(16.dp))
                            feed?.promoBanner?.let { promo ->
                                AirportPromoBanner(
                                    promo = promo,
                                    onClick = { onPromoBannerClick(promo) }
                                )
                            }
                            Spacer(modifier = Modifier.height(22.dp))
                        }

                        // H. Why GoIndiaCab Header
                        item(key = "why_goindiacab_header", contentType = "header") {
                            Text(
                                text = "Why GoIndiaCab",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // I. Why GoIndiaCab Feature Badges Row
                        item(key = "why_goindiacab_row", contentType = "carousel") {
                            if (feed != null && feed.whyFeatures.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(
                                        items = feed.whyFeatures,
                                        key = { it.id },
                                        contentType = { "feature_chip" }
                                    ) { feature ->
                                        WhyFeatureChip(feature = feature)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            HomeBottomTab.MY_TRIPS -> {
                        HomeMyTripsView(
                            recentTrips = uiState.feedData?.recentTrips ?: emptyList(),
                            onBookAgain = { trip ->
                                viewModel.bookAgain(trip)
                                onBookAgainClick(trip)
                            },
                            onBookNewRide = {
                                activeTab = HomeBottomTab.HOME
                            }
                        )
                    }
                    HomeBottomTab.OFFERS -> {
                        HomeOffersView(
                            onBookWithOffer = {
                                activeTab = HomeBottomTab.HOME
                                onOutstationClick()
                            }
                        )
                    }
                    HomeBottomTab.WALLET -> {
                        HomeWalletView(
                            onAddMoneyClick = {
                                activeTab = HomeBottomTab.HOME
                            }
                        )
                    }
                    HomeBottomTab.PROFILE -> {
                        HomeProfileView(
                            onLogoutClick = onLogoutClick,
                            onSavedPlacesClick = onSelectPickupClick
                        )
                    }
                }

                // Error Banner with Retry
                uiState.errorMessage?.let { errorMsg ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errorMsg,
                                fontSize = 12.sp,
                                color = Color(0xFFB91C1C),
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { viewModel.refresh() }) {
                                Text("Retry", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Snackbar host
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // 3. Pinned Bottom Navigation Bar
            HomeBottomNavigationBar(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }

        // Side Drawer Menu Overlay
        SideDrawerMenu(
            isOpen = isDrawerOpen,
            onDismiss = { isDrawerOpen = false },
            onItemClick = { item ->
                isDrawerOpen = false
                when (item) {
                    "My Trips" -> activeTab = HomeBottomTab.MY_TRIPS
                    "Profile" -> activeTab = HomeBottomTab.PROFILE
                    "Saved Places" -> onSelectPickupClick()
                    "Wallet" -> activeTab = HomeBottomTab.WALLET
                    "Refer & Earn", "Notifications" -> activeTab = HomeBottomTab.OFFERS
                    "Outstation Cabs", "One-Way Cabs", "Hourly Rentals" -> onOutstationClick()
                    else -> {}
                }
            },
            onLogoutClick = {
                isDrawerOpen = false
                onLogoutClick()
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = outfitFontFamily(),
            color = TextDark
        )
        Text(
            text = actionText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = outfitFontFamily(),
            color = AccentOrange,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
private fun HomeTopBar(
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(TopBarGradientStart, TopBarGradientEnd)
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Hamburger Menu Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center
            ) {
                HamburgerMenuIcon(size = 24.dp, color = Color.White)
            }

            // Center GoIndiaCab Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_cab),
                        contentDescription = "GoIndiaCab Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "GoIndiaCab",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }

            // Notification Bell with Unread Orange Dot
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                BellIcon(size = 20.dp, color = Color.White)

                // Unread Dot Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentOrange)
                )
            }
        }
    }
}

@Composable
private fun ServiceTypesHeroCard(
    selectedService: ServiceType,
    onServiceSelected: (ServiceType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .clip(RoundedCornerShape(20.dp))
            .height(230.dp)
    ) {
        // Landscape Background Image
        Image(
            painter = painterResource(Res.drawable.bg_home_hero),
            contentDescription = "Scenic India Heritage",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Atmospheric Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color(0xFF091E42).copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Service Types 2-Column Grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Service Types",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            val services = ServiceType.entries
            for (i in services.indices step 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ServiceChip(
                        modifier = Modifier.weight(1f),
                        service = services[i],
                        isSelected = services[i] == selectedService,
                        onClick = { onServiceSelected(services[i]) }
                    )
                    if (i + 1 < services.size) {
                        ServiceChip(
                            modifier = Modifier.weight(1f),
                            service = services[i + 1],
                            isSelected = services[i + 1] == selectedService,
                            onClick = { onServiceSelected(services[i + 1]) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceChip(
    modifier: Modifier = Modifier,
    service: ServiceType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(50),
        color = if (isSelected) ActiveChipBg else Color.White.copy(alpha = 0.90f),
        border = if (isSelected) BorderStroke(2.dp, BrandBlue) else null,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio Indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = if (isSelected) BrandBlue else TextPlaceholder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BrandBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = service.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = dmSansFontFamily(),
                color = if (isSelected) BrandBlue else TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            if (service.isNewBadge) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentOrange)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "NEW",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TripBookingCard(
    pickup: String,
    drop: String,
    dateTime: String,
    vehicle: String,
    onPickupClick: () -> Unit = {},
    onDropClick: () -> Unit = {},
    onSearchClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Pickup Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onPickupClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Green Marker Ring
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .border(3.dp, MarkerGreen, CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "PICKUP LOCATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (pickup.isBlank()) "Where are you starting from?" else pickup,
                        fontSize = 14.sp,
                        fontWeight = if (pickup.isBlank()) FontWeight.Normal else FontWeight.Bold,
                        fontFamily = dmSansFontFamily(),
                        color = if (pickup.isBlank()) TextPlaceholder else TextDark
                    )
                }
            }

            // Dotted / Subtle Divider
            Box(
                modifier = Modifier
                    .padding(start = 6.dp, top = 8.dp, bottom = 8.dp)
                    .width(2.dp)
                    .height(14.dp)
                    .background(BorderGray)
            )

            // 2. Drop Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onDropClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Orange Marker Ring
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .border(3.dp, MarkerOrange, CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "DROP LOCATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (drop.isBlank()) "Where do you want to go?" else drop,
                        fontSize = 14.sp,
                        fontWeight = if (drop.isBlank()) FontWeight.Normal else FontWeight.Bold,
                        fontFamily = dmSansFontFamily(),
                        color = if (drop.isBlank()) TextPlaceholder else TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Date / Time & Vehicle Options Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date & Time Chip
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CalendarIcon(size = 18.dp, color = BrandBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateTime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = dmSansFontFamily(),
                            color = TextDark
                        )
                    }
                }

                // Vehicle Type Chip
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_car_sedan),
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = vehicle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = dmSansFontFamily(),
                            color = TextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Solid Orange CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF6B00), Color(0xFFFF8A00))
                        )
                    )
                    .clickable(onClick = onSearchClick),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search_lens),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Search Cabs",
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

@Composable
private fun PopularRouteCard(
    route: PopularRoute,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (route.imageUrl == "img_route_agra") {
                Image(
                    painter = painterResource(Res.drawable.img_route_agra),
                    contentDescription = route.toCity,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (route.isFeatured) Color(0xFFFFECE0) else ActiveChipBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_cab),
                        contentDescription = null,
                        tint = if (route.isFeatured) AccentOrange else BrandBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = route.routeTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = route.formattedDistanceDuration,
                    fontSize = 11.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = route.formattedPrice,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = BrandBlue
                )
            }
        }
    }
}

@Composable
private fun RecentTripCard(
    trip: RecentTrip,
    onBookAgain: () -> Unit
) {
    val indicatorColor = try {
        Color(trip.statusColorHex.removePrefix("#").toLong(16) or 0x00000000FF000000)
    } catch (e: Exception) {
        MarkerGreen
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trip.routeTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = trip.formattedDate,
                    fontSize = 11.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = trip.formattedFare,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
            }

            // Book Again Button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AccentOrange,
                onClick = onBookAgain
            ) {
                Text(
                    text = "Book Again",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AirportPromoBanner(
    promo: PromoBanner,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFF6B00), Color(0xFFFF8A00))
                )
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = promo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = promo.subtitle,
                    fontSize = 11.sp,
                    fontFamily = dmSansFontFamily(),
                    color = Color.White.copy(alpha = 0.88f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Airplane badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_airplane),
                    contentDescription = "Airport offer",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun WhyFeatureChip(feature: WhyChooseUsItem) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderGray),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (feature.iconKey) {
                "shield" -> Icon(
                    painter = painterResource(Res.drawable.ic_shield_check),
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(18.dp)
                )
                "support" -> Icon(
                    painter = painterResource(Res.drawable.ic_headset),
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(18.dp)
                )
                "clean" -> Icon(
                    painter = painterResource(Res.drawable.ic_cab),
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(18.dp)
                )
                else -> CalendarIcon(size = 18.dp, color = BrandBlue)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = feature.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmSansFontFamily(),
                color = TextDark
            )
        }
    }
}

@Composable
private fun HomeLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        )
    }
}

@Composable
private fun HomeBottomNavigationBar(
    activeTab: HomeBottomTab,
    onTabSelected: (HomeBottomTab) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home
            BottomNavItem(
                label = "Home",
                isSelected = activeTab == HomeBottomTab.HOME,
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_nav_home),
                        contentDescription = null,
                        tint = if (activeTab == HomeBottomTab.HOME) AccentOrange else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onTabSelected(HomeBottomTab.HOME) }
            )

            // 2. My Trips
            BottomNavItem(
                label = "My Trips",
                isSelected = activeTab == HomeBottomTab.MY_TRIPS,
                icon = {
                    CalendarIcon(
                        size = 20.dp,
                        color = if (activeTab == HomeBottomTab.MY_TRIPS) AccentOrange else TextMuted
                    )
                },
                onClick = { onTabSelected(HomeBottomTab.MY_TRIPS) }
            )

            // 3. Offers
            BottomNavItem(
                label = "Offers",
                isSelected = activeTab == HomeBottomTab.OFFERS,
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_discount_tag),
                        contentDescription = null,
                        tint = if (activeTab == HomeBottomTab.OFFERS) AccentOrange else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = { onTabSelected(HomeBottomTab.OFFERS) }
            )

            // 4. Wallet
            BottomNavItem(
                label = "Wallet",
                isSelected = activeTab == HomeBottomTab.WALLET,
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_wallet_card),
                        contentDescription = null,
                        tint = if (activeTab == HomeBottomTab.WALLET) AccentOrange else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = { onTabSelected(HomeBottomTab.WALLET) }
            )

            // 5. Profile
            BottomNavItem(
                label = "Profile",
                isSelected = activeTab == HomeBottomTab.PROFILE,
                icon = {
                    PersonIcon(
                        size = 20.dp,
                        color = if (activeTab == HomeBottomTab.PROFILE) AccentOrange else TextMuted
                    )
                },
                onClick = { onTabSelected(HomeBottomTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = dmSansFontFamily(),
            color = if (isSelected) AccentOrange else TextMuted
        )
    }
}

@Composable
private fun HomeMyTripsView(
    recentTrips: List<RecentTrip>,
    onBookAgain: (RecentTrip) -> Unit,
    onBookNewRide: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "My Trips",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Track upcoming and completed rides",
                fontSize = 13.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted
            )
        }

        // Active Upcoming Trip Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, Color(0xFF0052CC)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEBF3FF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "UPCOMING RIDE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0052CC),
                                fontFamily = outfitFontFamily()
                            )
                        }
                        Text(
                            text = "GIC-849201",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            fontFamily = dmSansFontFamily()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Delhi (Connaught Place)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AccentOrange)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Jaipur, Rajasthan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Toyota Innova Crysta • Sohan Singh (4.9★)",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontFamily = dmSansFontFamily()
                        )
                        Text(
                            text = "₹4,500",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0052CC),
                            fontFamily = outfitFontFamily()
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Past Completed Rides",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (recentTrips.isNotEmpty()) {
            items(recentTrips) { trip ->
                RecentTripCard(
                    trip = trip,
                    onBookAgain = { onBookAgain(trip) }
                )
            }
        } else {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No completed trips yet", color = TextMuted, fontSize = 14.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onBookNewRide,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Book a New Cab",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun HomeOffersView(
    onBookWithOffer: () -> Unit
) {
    val coupons = listOf(
        Triple("FIRST50", "Flat ₹200 OFF on First Ride", "Valid on all outstation cab bookings"),
        Triple("AIRPORT200", "Flat ₹200 OFF Airport Transfers", "Valid on airport drop & pickup rides"),
        Triple("WEEKEND30", "15% OFF Weekend Getaways", "Max discount ₹500 on round trips"),
        Triple("REFER100", "Refer & Earn ₹500 GoCash", "Earn rewards when friends complete first ride")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Offers & Discounts",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Exclusive deals and verified coupon codes",
                fontSize = 13.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted
            )
        }

        items(coupons) { (code, title, desc) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderGray),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFF0E6),
                            border = BorderStroke(1.dp, AccentOrange)
                        ) {
                            Text(
                                text = code,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AccentOrange,
                                fontFamily = outfitFontFamily(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Button(
                            onClick = onBookWithOffer,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Use Offer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeWalletView(
    onAddMoneyClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "GoIndiaCab Wallet",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
        }

        // Wallet Balance Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0052CC),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "TOTAL WALLET BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 0.5.sp,
                        fontFamily = outfitFontFamily()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹1,250.00",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = outfitFontFamily()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Includes ₹350 GoCash Rewards",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontFamily = dmSansFontFamily()
                        )
                        Button(
                            onClick = onAddMoneyClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("+ Add Money", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0052CC))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Recent Transactions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
        }

        val txns = listOf(
            Triple("Advance Deposit Paid (DL → JAI)", "- ₹450", "12 Sep 2026 • UPI"),
            Triple("Wallet Top-up Added", "+ ₹1,000", "10 Sep 2026 • NetBanking"),
            Triple("Referral Bonus Credited", "+ ₹500", "05 Sep 2026 • GoCash")
        )

        items(txns) { (title, amount, date) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = date, fontSize = 11.sp, color = TextMuted)
                    }
                    Text(
                        text = amount,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (amount.startsWith("+")) Color(0xFF10B981) else TextDark
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeProfileView(
    onLogoutClick: () -> Unit,
    onSavedPlacesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile & Settings",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = outfitFontFamily(),
            color = TextDark
        )

        // Profile Avatar Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderGray),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0052CC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = outfitFontFamily()
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rahul Sharma",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "+91 98765 43210 • Gold Member",
                        fontSize = 13.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextMuted
                    )
                }
            }
        }

        // Options Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileOptionRow(title = "Saved Places", subtitle = "Home, Office, Airport", onClick = onSavedPlacesClick)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileOptionRow(title = "Emergency SOS Contacts", subtitle = "Family & friends contacts", onClick = {})
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileOptionRow(title = "Payment Methods", subtitle = "Google Pay, PhonePe, Cards", onClick = {})
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileOptionRow(title = "App Language", subtitle = "English (India)", onClick = {})
            }
        }

        // Logout Button
        Button(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Log Out",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB91C1C),
                fontFamily = outfitFontFamily()
            )
        }
    }
}

@Composable
private fun ProfileOptionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
        }
        Text("›", fontSize = 18.sp, color = TextMuted)
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    GoIndiaCabTheme {
        HomeScreen()
    }
}
