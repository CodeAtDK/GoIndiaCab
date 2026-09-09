package com.example.goindiacab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.screens.*
import com.example.goindiacab.theme.*

import androidx.compose.animation.core.tween

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.components.rememberAppExiter
import com.example.goindiacab.components.rememberPlatformToast

enum class AppScreen(val displayName: String) {
    SPLASH("1. Splash Screen"),
    SESSION_CHECK("2. Session Check"),
    ONBOARDING("3. Onboarding"),
    LOGIN("4. Login Screen"),
    OTP_NORMAL("7. OTP Verification"),
    OTP_EXPIRED("8. Resend OTP"),
    OTP_ERROR("9. OTP Error"),
    LOCATION_PERMISSION("13. Location Permission"),
    HOME("15. Home Screen"),
    PICKUP_LOCATION_SELECTION("16. Pickup Location Selection"),
    CONFIRM_PICKUP_MAP("17. Confirm Pickup Location - Map"),
    CITY_ROUTE_SELECTION("18. City Route Selection"),
    SEARCH_DESTINATION("19. Search Destination"),
    ROUTE_CONFIRMATION("21. Pickup-Drop Confirmation"),
    SCHEDULE_RIDE("22. Schedule Ride"),
    DATE_SELECTION("23. Date Selection"),
    TIME_SELECTION("24. Time Selection"),
    VEHICLE_OPTIONS("25. Vehicle Partner Options"),
    CAB_DETAIL("27. Cab Detail"),
    FARE_DETAILS("28. Fare Details"),
    BOOKING_SUMMARY("30. Booking Summary"),
    APPLY_COUPON("29. Apply Coupon"),
    BOOKING_CONFIRMATION("31. Booking Confirmation"),
    PAYMENT_SCREEN("32. Payment Screen"),
    PAYMENT_PROCESSING("33. Payment Processing"),
    PAYMENT_FAILED("34. Payment Failed"),
    BOOKING_ID_CONFIRMATION("35. Booking ID Confirmation"),
    PARTNER_SEARCHING("Partner Searching"),
    PARTNER_ASSIGNED("Partner Assigned"),
    REFUND_INITIATED("Refund Initiated"),
    TRIP_PAYMENT_SCHEDULE("Trip Payment Schedule"),
    PAYMENT_OTP_VERIFICATION("Trip Start Payment (40%)"),
    PROFILE("Profile"),
    EDIT_PROFILE("Edit Profile"),
    CUSTOMER_SUPPORT("Help & Support"),
    RATE_US("Rate Us"),
    REFER_EARN("Refer & Earn"),
    SETTINGS("Settings"),
    SAVED_PLACES("Saved Places"),
    ABOUT("About GoIndiaCab"),
    PRIVACY_POLICY("Privacy Policy")
}

@Composable
@Preview
fun App() {
    GoIndiaCabTheme {
        val homeViewModel = remember { com.example.goindiacab.di.AppContainer.createHomeViewModel() }
        val scheduleRideViewModel = remember { com.example.goindiacab.di.AppContainer.createScheduleRideViewModel() }
        val bookingFlowViewModel = remember { com.example.goindiacab.di.AppContainer.createBookingFlowViewModel() }
        val outstationRouteViewModel = remember { com.example.goindiacab.di.AppContainer.createOutstationRouteViewModel() }
        val multiStopRouteViewModel = remember { com.example.goindiacab.di.AppContainer.createMultiStopRouteViewModel() }
        val vehicleSelectionViewModel = remember { com.example.goindiacab.di.AppContainer.createVehicleSelectionViewModel() }

        val screenBackStack = remember { mutableStateListOf(AppScreen.SPLASH) }
        val currentScreen = screenBackStack.lastOrNull() ?: AppScreen.HOME

        var enteredPhoneNumber by remember { mutableStateOf("+91 98765 43210") }
        var showScreenPicker by remember { mutableStateOf(false) }
        var selectedLocationItem by remember { mutableStateOf<com.example.goindiacab.data.models.LocationItem?>(null) }

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        val toast = rememberPlatformToast()
        val exitApp = rememberAppExiter()
        val coroutineScope = rememberCoroutineScope()
        var backPressedOnce by remember { mutableStateOf(false) }

        /**
         * Ordered sequence of linear booking funnel screens (16 to 35).
         * Used to enforce single-top transitions and eliminate destination/station loops.
         */
        val FunnelOrder = listOf(
            AppScreen.PICKUP_LOCATION_SELECTION,
            AppScreen.CONFIRM_PICKUP_MAP,
            AppScreen.CITY_ROUTE_SELECTION,
            AppScreen.SEARCH_DESTINATION,
            AppScreen.ROUTE_CONFIRMATION,
            AppScreen.SCHEDULE_RIDE,
            AppScreen.DATE_SELECTION,
            AppScreen.TIME_SELECTION,
            AppScreen.VEHICLE_OPTIONS,
            AppScreen.CAB_DETAIL,
            AppScreen.FARE_DETAILS,
            AppScreen.BOOKING_SUMMARY,
            AppScreen.APPLY_COUPON,
            AppScreen.BOOKING_CONFIRMATION,
            AppScreen.PAYMENT_SCREEN,
            AppScreen.PAYMENT_PROCESSING,
            AppScreen.PAYMENT_FAILED,
            AppScreen.PARTNER_SEARCHING,
            AppScreen.PARTNER_ASSIGNED,
            AppScreen.REFUND_INITIATED,
            AppScreen.TRIP_PAYMENT_SCHEDULE,
            AppScreen.PAYMENT_OTP_VERIFICATION,
            AppScreen.BOOKING_ID_CONFIRMATION
        )

        /**
         * Navigates forward to the specified screen.
         * Guarantees single-top behavior and prunes downstream/stale instances
         * to prevent destination station backstack loops.
         *
         * @param screen The target AppScreen to push or unwind to
         * @param clearStack When true, clears the entire existing backstack (for root transitions)
         */
        fun navigateTo(screen: AppScreen, clearStack: Boolean = false) {
            if (clearStack) {
                screenBackStack.clear()
                screenBackStack.add(screen)
                return
            }
            if (showScreenPicker) {
                showScreenPicker = false
            }

            val targetFunnelIdx = FunnelOrder.indexOf(screen)
            if (targetFunnelIdx != -1) {
                // 1. SingleTop: If screen already in stack, unwind back to it
                val existingIndex = screenBackStack.indexOf(screen)
                if (existingIndex != -1) {
                    while (screenBackStack.size > existingIndex + 1) {
                        screenBackStack.removeAt(screenBackStack.lastIndex)
                    }
                    return
                }

                // 2. Loop Protection: Prune any downstream funnel screens currently at the top
                // (e.g. if user is at ROUTE_CONFIRMATION, goes back to SEARCH_DESTINATION,
                // and selects a new destination, prune old ROUTE_CONFIRMATION before adding new)
                while (screenBackStack.isNotEmpty()) {
                    val last = screenBackStack.last()
                    val lastIdx = FunnelOrder.indexOf(last)
                    if (lastIdx >= targetFunnelIdx) {
                        screenBackStack.removeAt(screenBackStack.lastIndex)
                    } else {
                        break
                    }
                }
            }

            screenBackStack.add(screen)
        }

        /**
         * Pops the topmost screen from the backstack (standard backward navigation).
         * Dismisses the floating developer previewer if currently expanded.
         * @return true if a screen was popped or modal dismissed; false if already at root.
         */
        fun popBackStack(): Boolean {
            if (showScreenPicker) {
                showScreenPicker = false
                return true
            }
            if (screenBackStack.size > 1) {
                screenBackStack.removeAt(screenBackStack.lastIndex)
                return true
            }
            return false
        }

        /**
         * Pops all screens back to a specific target screen in the backstack.
         * Prevents backstack leaks and duplicate screen accumulation.
         *
         * @param targetScreen The target AppScreen to unwind the stack back to.
         */
        fun popBackTo(targetScreen: AppScreen) {
            if (showScreenPicker) {
                showScreenPicker = false
            }
            val existingIndex = screenBackStack.indexOf(targetScreen)
            if (existingIndex != -1) {
                while (screenBackStack.size > existingIndex + 1) {
                    screenBackStack.removeAt(screenBackStack.lastIndex)
                }
            } else {
                popBackStack()
            }
        }

        /**
         * Centralized back-navigation dispatcher for top app bar back buttons and Android hardware/gesture back events.
         * Enforces the exact 1-to-35 user flow sequence with zero loops when destinations are changed.
         */
        fun handleScreenBack() {
            if (showScreenPicker) {
                showScreenPicker = false
                return
            }

            when (currentScreen) {
                AppScreen.HOME -> {
                    // Double-tap back button within 2 seconds to safely exit the application
                    if (backPressedOnce) {
                        exitApp()
                    } else {
                        backPressedOnce = true
                        toast("Press back again to exit")
                        coroutineScope.launch {
                            delay(2000)
                            backPressedOnce = false
                        }
                    }
                }
                AppScreen.SPLASH,
                AppScreen.SESSION_CHECK -> {
                    exitApp()
                }
                AppScreen.ONBOARDING -> {
                    if (backPressedOnce) {
                        exitApp()
                    } else {
                        backPressedOnce = true
                        toast("Press back again to exit")
                        coroutineScope.launch {
                            delay(2000)
                            backPressedOnce = false
                        }
                    }
                }
                AppScreen.LOGIN -> {
                    popBackTo(AppScreen.ONBOARDING)
                }
                AppScreen.OTP_NORMAL,
                AppScreen.OTP_EXPIRED,
                AppScreen.OTP_ERROR -> {
                    popBackTo(AppScreen.LOGIN)
                }
                AppScreen.LOCATION_PERMISSION -> {
                    popBackTo(AppScreen.LOGIN)
                }
                AppScreen.PICKUP_LOCATION_SELECTION -> {
                    popBackTo(AppScreen.HOME)
                }
                AppScreen.CONFIRM_PICKUP_MAP -> {
                    popBackTo(AppScreen.PICKUP_LOCATION_SELECTION)
                }
                AppScreen.CITY_ROUTE_SELECTION -> {
                    if (screenBackStack.contains(AppScreen.CONFIRM_PICKUP_MAP)) {
                        popBackTo(AppScreen.CONFIRM_PICKUP_MAP)
                    } else {
                        popBackTo(AppScreen.HOME)
                    }
                }
                AppScreen.SEARCH_DESTINATION -> {
                    popBackTo(AppScreen.CITY_ROUTE_SELECTION)
                }
                AppScreen.ROUTE_CONFIRMATION -> {
                    if (screenBackStack.contains(AppScreen.SEARCH_DESTINATION)) {
                        popBackTo(AppScreen.SEARCH_DESTINATION)
                    } else if (screenBackStack.contains(AppScreen.CITY_ROUTE_SELECTION)) {
                        popBackTo(AppScreen.CITY_ROUTE_SELECTION)
                    } else {
                        popBackTo(AppScreen.HOME)
                    }
                }
                AppScreen.SCHEDULE_RIDE -> {
                    popBackTo(AppScreen.ROUTE_CONFIRMATION)
                }
                AppScreen.DATE_SELECTION,
                AppScreen.TIME_SELECTION -> {
                    popBackTo(AppScreen.SCHEDULE_RIDE)
                }
                AppScreen.VEHICLE_OPTIONS -> {
                    popBackTo(AppScreen.SCHEDULE_RIDE)
                }
                AppScreen.CAB_DETAIL -> {
                    popBackTo(AppScreen.VEHICLE_OPTIONS)
                }
                AppScreen.FARE_DETAILS -> {
                    popBackTo(AppScreen.CAB_DETAIL)
                }
                AppScreen.BOOKING_SUMMARY -> {
                    popBackTo(AppScreen.CAB_DETAIL)
                }
                AppScreen.APPLY_COUPON -> {
                    popBackTo(AppScreen.BOOKING_SUMMARY)
                }
                AppScreen.BOOKING_CONFIRMATION -> {
                    popBackTo(AppScreen.BOOKING_SUMMARY)
                }
                AppScreen.PAYMENT_SCREEN -> {
                    popBackTo(AppScreen.BOOKING_CONFIRMATION)
                }
                AppScreen.PAYMENT_PROCESSING -> {
                    // Handled locally by PaymentProcessingScreen's cancel confirmation alert dialog
                }
                AppScreen.PAYMENT_FAILED -> {
                    // From payment failure, back unwinds cleanly to payment screen
                    popBackTo(AppScreen.PAYMENT_SCREEN)
                }
                AppScreen.PARTNER_SEARCHING -> {
                    // Canceling driver search unwinds cleanly back to booking summary
                    popBackTo(AppScreen.BOOKING_SUMMARY)
                }
                AppScreen.PARTNER_ASSIGNED -> {
                    // Once partner is assigned, back returns to Home with active trip card
                    popBackTo(AppScreen.HOME)
                }
                AppScreen.REFUND_INITIATED -> {
                    // From refund status screen, back returns safely to Home with cleared backstack
                    navigateTo(AppScreen.HOME, clearStack = true)
                }
                AppScreen.TRIP_PAYMENT_SCHEDULE -> {
                    popBackTo(AppScreen.PARTNER_ASSIGNED)
                }
                AppScreen.PAYMENT_OTP_VERIFICATION -> {
                    popBackTo(AppScreen.TRIP_PAYMENT_SCHEDULE)
                }
                AppScreen.BOOKING_ID_CONFIRMATION -> {
                    // Confirmed booking receipts return cleanly to Home root
                    navigateTo(AppScreen.HOME, clearStack = true)
                }
                AppScreen.PROFILE -> {
                    popBackTo(AppScreen.HOME)
                }
                AppScreen.EDIT_PROFILE -> {
                    popBackTo(AppScreen.PROFILE)
                }
                AppScreen.CUSTOMER_SUPPORT,
                AppScreen.RATE_US,
                AppScreen.REFER_EARN,
                AppScreen.SETTINGS,
                AppScreen.SAVED_PLACES,
                AppScreen.ABOUT,
                AppScreen.PRIVACY_POLICY -> {
                    popBackStack()
                }
            }
        }

        // Android Hardware & Gesture Back Button Handler with Double-Tap to Exit Protection
        PlatformBackHandler(enabled = true) {
            handleScreenBack()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Main Screen Content with smooth animated transitions
            Crossfade(
                targetState = currentScreen,
                animationSpec = tween(400),
                modifier = Modifier.fillMaxSize()
            ) { screen ->
                when (screen) {
                    // -------------------------------------------------------------
                    // Phase 1: Authentication, Onboarding & User Session Lifecycle
                    // -------------------------------------------------------------
                    AppScreen.SPLASH -> {
                        // App launch splash screen; animates brand identity then checks auth session
                        SplashScreen(
                            onNavigateNext = { navigateTo(AppScreen.SESSION_CHECK, clearStack = true) }
                        )
                    }
                    AppScreen.SESSION_CHECK -> {
                        // Verifies stored JWT auth token; redirects to Onboarding or Home
                        SessionCheckScreen(
                            onSessionVerified = { navigateTo(AppScreen.ONBOARDING, clearStack = true) }
                        )
                    }
                    AppScreen.ONBOARDING -> {
                        // Interactive 3-slide value proposition carousel; page 0 allows double-tap exit
                        OnboardingScreen(
                            onGetStartedClick = { navigateTo(AppScreen.LOGIN) }
                        )
                    }
                    AppScreen.LOGIN -> {
                        // Phone number entry with country code validation (+91)
                        LoginScreen(
                            onContinueClick = { phone ->
                                enteredPhoneNumber = if (phone.isNotBlank()) {
                                    val cleaned = phone.filter { it.isDigit() }
                                    if (cleaned.length == 10) {
                                        "+91 ${cleaned.substring(0, 5)} ${cleaned.substring(5)}"
                                    } else {
                                        "+91 $cleaned"
                                    }
                                } else {
                                    "+91 98765 43210"
                                }
                                navigateTo(AppScreen.OTP_NORMAL)
                            },
                            onSkipClick = { navigateTo(AppScreen.LOCATION_PERMISSION) }
                        )
                    }
                    AppScreen.OTP_NORMAL -> {
                        // 6-digit SMS OTP verification (default active mode)
                        OtpVerificationScreen(
                            phoneNumber = enteredPhoneNumber,
                            initialMode = OtpScreenMode.NORMAL,
                            onBackClick = { handleScreenBack() },
                            onVerifySuccess = { navigateTo(AppScreen.LOCATION_PERMISSION) },
                            onEditPhoneClick = { handleScreenBack() }
                        )
                    }
                    AppScreen.OTP_EXPIRED -> {
                        // OTP resend & expiration handling mode
                        OtpVerificationScreen(
                            phoneNumber = enteredPhoneNumber,
                            initialMode = OtpScreenMode.EXPIRED,
                            onBackClick = { handleScreenBack() },
                            onVerifySuccess = { navigateTo(AppScreen.LOCATION_PERMISSION) },
                            onEditPhoneClick = { handleScreenBack() }
                        )
                    }
                    AppScreen.OTP_ERROR -> {
                        // OTP error state with inline error badge and retry counter
                        OtpVerificationScreen(
                            phoneNumber = enteredPhoneNumber,
                            initialMode = OtpScreenMode.ERROR,
                            onBackClick = { handleScreenBack() },
                            onVerifySuccess = { navigateTo(AppScreen.LOCATION_PERMISSION) },
                            onEditPhoneClick = { handleScreenBack() }
                        )
                    }
                    AppScreen.LOCATION_PERMISSION -> {
                        // System location permission rationale screen; clears stack on granting
                        LocationPermissionScreen(
                            onAllowLocationClick = { navigateTo(AppScreen.HOME, clearStack = true) },
                            onEnterManuallyClick = { navigateTo(AppScreen.HOME, clearStack = true) }
                        )
                    }

                    // -------------------------------------------------------------
                    // Phase 2: Home Dashboard & Booking Entry Hub
                    // -------------------------------------------------------------
                    AppScreen.HOME -> {
                        // Main landing dashboard with service switcher, blank pickup by default,
                        // popular routes, promo banners, and one-tap re-booking.
                        HomeScreen(
                            viewModel = homeViewModel,
                            onLogoutClick = { navigateTo(AppScreen.LOGIN, clearStack = true) },
                            onViewAllRoutesClick = { navigateTo(AppScreen.CITY_ROUTE_SELECTION) },
                            onSelectPickupClick = {
                                navigateTo(AppScreen.PICKUP_LOCATION_SELECTION)
                            },
                            onSelectDropClick = {
                                val pickup = homeViewModel.uiState.value.pickupLocation
                                if (pickup.isBlank()) {
                                    // Step 1: If pickup location is not chosen, select pickup location first
                                    navigateTo(AppScreen.PICKUP_LOCATION_SELECTION)
                                } else {
                                    outstationRouteViewModel.setFromCity(pickup)
                                    multiStopRouteViewModel.setPickup(pickup)
                                    navigateTo(AppScreen.CITY_ROUTE_SELECTION)
                                }
                            },
                            onOutstationClick = {
                                val service = homeViewModel.uiState.value.selectedService
                                val tripType = when (service) {
                                    com.example.goindiacab.data.models.ServiceType.OUTSTATION_ROUND_TRIP -> com.example.goindiacab.data.models.OutstationTripType.ROUND_TRIP
                                    com.example.goindiacab.data.models.ServiceType.AIRPORT_TRANSFERS -> com.example.goindiacab.data.models.OutstationTripType.AIRPORT
                                    com.example.goindiacab.data.models.ServiceType.HOURLY_RENTALS -> com.example.goindiacab.data.models.OutstationTripType.HOURLY
                                    else -> com.example.goindiacab.data.models.OutstationTripType.ONE_WAY
                                }
                                outstationRouteViewModel.selectTripType(tripType)

                                val pickup = homeViewModel.uiState.value.pickupLocation
                                val drop = homeViewModel.uiState.value.dropLocation

                                if (pickup.isBlank()) {
                                    // Step 1: Must select pickup location first
                                    navigateTo(AppScreen.PICKUP_LOCATION_SELECTION)
                                } else if (drop.isBlank()) {
                                    // Step 2: Pickup is chosen, now select city route / destination
                                    outstationRouteViewModel.setFromCity(pickup)
                                    multiStopRouteViewModel.setPickup(pickup)
                                    navigateTo(AppScreen.CITY_ROUTE_SELECTION)
                                } else {
                                    // Step 3: Both are set, proceed to route confirmation
                                    outstationRouteViewModel.setFromCity(pickup)
                                    outstationRouteViewModel.selectCity(drop)
                                    multiStopRouteViewModel.setRoute(pickup, drop)
                                    scheduleRideViewModel.setLocations(pickup, drop)
                                    bookingFlowViewModel.updateRouteDetails(pickup, drop)
                                    vehicleSelectionViewModel.updateRoute(origin = pickup, destination = drop)
                                    navigateTo(AppScreen.ROUTE_CONFIRMATION)
                                }
                            },
                            onPopularRouteClick = { route ->
                                outstationRouteViewModel.selectRoute(route.fromCity, route.toCity)
                                multiStopRouteViewModel.setRoute(route.fromCity, route.toCity, route.distanceKm, route.priceInr)
                                scheduleRideViewModel.setLocations(route.fromCity, route.toCity)
                                bookingFlowViewModel.updateRouteDetails(route.fromCity, route.toCity, distanceKm = route.distanceKm, fare = route.priceInr)
                                vehicleSelectionViewModel.updateRoute(origin = route.fromCity, destination = route.toCity, distanceKm = route.distanceKm)
                                navigateTo(AppScreen.ROUTE_CONFIRMATION)
                            },
                            onPromoBannerClick = { promo ->
                                homeViewModel.selectService(com.example.goindiacab.data.models.ServiceType.AIRPORT_TRANSFERS)
                                outstationRouteViewModel.selectTripType(com.example.goindiacab.data.models.OutstationTripType.AIRPORT)
                                val from = "IGI Airport T3, New Delhi"
                                val to = "Connaught Place, New Delhi"
                                outstationRouteViewModel.selectRoute(from, to)
                                multiStopRouteViewModel.setRoute(from, to, 18, 899)
                                scheduleRideViewModel.setLocations(from, to)
                                bookingFlowViewModel.updateRouteDetails(from, to, distanceKm = 18, fare = 899)
                                vehicleSelectionViewModel.updateRoute(origin = from, destination = to, distanceKm = 18)
                                navigateTo(AppScreen.ROUTE_CONFIRMATION)
                            },
                            onBookAgainClick = { trip ->
                                val parts = trip.routeTitle.split("→", "->").map { it.trim() }
                                val from = if (parts.isNotEmpty() && parts[0].isNotBlank()) parts[0] else "Delhi"
                                val to = if (parts.size > 1 && parts[1].isNotBlank()) parts[1] else "Agra"
                                outstationRouteViewModel.selectRoute(from, to)
                                multiStopRouteViewModel.setRoute(from, to, 230, trip.fareInr)
                                scheduleRideViewModel.setLocations(from, to)
                                bookingFlowViewModel.updateRouteDetails(from, to, distanceKm = 230, fare = trip.fareInr)
                                vehicleSelectionViewModel.updateRoute(origin = from, destination = to, distanceKm = 230)
                                navigateTo(AppScreen.ROUTE_CONFIRMATION)
                            },
                            onProfileClick = { navigateTo(AppScreen.PROFILE) },
                            onCustomerSupportClick = { navigateTo(AppScreen.CUSTOMER_SUPPORT) },
                            onRateUsClick = { navigateTo(AppScreen.RATE_US) },
                            onReferEarnClick = { navigateTo(AppScreen.REFER_EARN) },
                            onSavedPlacesClick = { navigateTo(AppScreen.SAVED_PLACES) },
                            onSettingsClick = { navigateTo(AppScreen.SETTINGS) },
                            onAboutClick = { navigateTo(AppScreen.ABOUT) },
                            onPrivacyPolicyClick = { navigateTo(AppScreen.PRIVACY_POLICY) }
                        )
                    }
                    // -------------------------------------------------------------
                    // Phase 3: Pickup Location Search & Map Pin Confirmation
                    // -------------------------------------------------------------
                    AppScreen.PICKUP_LOCATION_SELECTION -> {
                        PickupLocationSelectionScreen(
                            onBackClick = { handleScreenBack() },
                            onLocationSelected = { location ->
                                selectedLocationItem = location
                                navigateTo(AppScreen.CONFIRM_PICKUP_MAP)
                            }
                        )
                    }
                    AppScreen.CONFIRM_PICKUP_MAP -> {
                        ConfirmPickupLocationMapScreen(
                            initialLocation = selectedLocationItem,
                            onBackClick = { handleScreenBack() },
                            onConfirmPickup = { confirmed ->
                                // Save confirmed pickup location and advance to City Route Selection (Screen 18)
                                homeViewModel.updatePickupLocation(confirmed.title)
                                outstationRouteViewModel.setFromCity(confirmed.title)
                                multiStopRouteViewModel.setPickup(confirmed.title)
                                navigateTo(AppScreen.CITY_ROUTE_SELECTION)
                            }
                        )
                    }
                    // -------------------------------------------------------------
                    // Phase 4: Outstation Route Selection & Dynamic Destination Search
                    // -------------------------------------------------------------
                    AppScreen.CITY_ROUTE_SELECTION -> {
                        // Outstation popular city routes, trip mode tabs (One Way, Round Trip, Airport),
                        // and origin-destination route cards.
                        CityRouteSelectionScreen(
                            viewModel = outstationRouteViewModel,
                            onBackClick = { handleScreenBack() },
                            onSelectPickupClick = { navigateTo(AppScreen.PICKUP_LOCATION_SELECTION) },
                            onSelectDestinationClick = { navigateTo(AppScreen.SEARCH_DESTINATION) },
                            onRouteSelected = { route ->
                                val actualFrom = outstationRouteViewModel.uiState.value.fromCity.ifBlank {
                                    homeViewModel.uiState.value.pickupLocation.ifBlank { route.fromCity }
                                }
                                outstationRouteViewModel.selectRoute(actualFrom, route.toCity)
                                multiStopRouteViewModel.setRoute(actualFrom, route.toCity, route.distanceKm, route.priceInr)
                                scheduleRideViewModel.setLocations(actualFrom, route.toCity)
                                bookingFlowViewModel.updateRouteDetails(actualFrom, route.toCity, distanceKm = route.distanceKm, fare = route.priceInr)
                                vehicleSelectionViewModel.updateRoute(origin = actualFrom, destination = route.toCity, distanceKm = route.distanceKm)
                                navigateTo(AppScreen.ROUTE_CONFIRMATION)
                            }
                        )
                    }
                    AppScreen.SEARCH_DESTINATION -> {
                        // Search Destination: provides live search, recent lookups, and popular getaways.
                        // Arrived from CITY_ROUTE_SELECTION when passenger taps "TO CITY".
                        // Back navigation returns cleanly to CITY_ROUTE_SELECTION.
                        SearchDestinationScreen(
                            onBackClick = { handleScreenBack() },
                            onDestinationSelected = { dest ->
                                val fromCity = outstationRouteViewModel.uiState.value.fromCity.ifBlank {
                                    homeViewModel.uiState.value.pickupLocation.ifBlank { "Current Location" }
                                }
                                homeViewModel.updateDropLocation(dest)
                                outstationRouteViewModel.selectCity(dest)
                                outstationRouteViewModel.setFromCity(fromCity)

                                multiStopRouteViewModel.setRoute(fromCity, dest)
                                scheduleRideViewModel.setLocations(fromCity, dest)
                                bookingFlowViewModel.updateRouteDetails(fromCity, dest)
                                vehicleSelectionViewModel.updateRoute(origin = fromCity, destination = dest)
                                navigateTo(AppScreen.ROUTE_CONFIRMATION)
                            }
                        )
                    }

                    // -------------------------------------------------------------
                    // Phase 5: Multi-Stop Route Confirmation & Scheduling Engine
                    // -------------------------------------------------------------
                    AppScreen.ROUTE_CONFIRMATION -> {
                        // Plan Your Route screen: timeline waypoint stops, distance/duration metrics,
                        // and add-stop capabilities.
                        MultiStopRouteConfirmationScreen(
                            viewModel = multiStopRouteViewModel,
                            onBackClick = { handleScreenBack() },
                            onChangeDestinationClick = {
                                if (screenBackStack.contains(AppScreen.SEARCH_DESTINATION)) {
                                    popBackTo(AppScreen.SEARCH_DESTINATION)
                                } else {
                                    screenBackStack.remove(AppScreen.ROUTE_CONFIRMATION)
                                    navigateTo(AppScreen.SEARCH_DESTINATION)
                                }
                            },
                            onContinueClick = {
                                val route = multiStopRouteViewModel.uiState.value.routeData
                                scheduleRideViewModel.setLocations(route.pickupPoint, route.dropoffPoint)
                                bookingFlowViewModel.updateRouteDetails(
                                    pickup = route.pickupPoint,
                                    drop = route.dropoffPoint,
                                    stops = route.stops.map { it.name },
                                    distanceKm = route.totalDistanceKm,
                                    fare = route.totalEstimateInr
                                )
                                vehicleSelectionViewModel.updateRoute(
                                    origin = route.pickupPoint,
                                    destination = route.dropoffPoint,
                                    distanceKm = route.totalDistanceKm
                                )
                                navigateTo(AppScreen.SCHEDULE_RIDE)
                            }
                        )
                    }
                    AppScreen.SCHEDULE_RIDE -> {
                        // Schedule a Ride: horizontal calendar dates, pickup time slot chips,
                        // and advance booking confirmation.
                        ScheduleRideScreen(
                            viewModel = scheduleRideViewModel,
                            onBackClick = { handleScreenBack() },
                            onChangeMonthClick = { navigateTo(AppScreen.DATE_SELECTION) },
                            onSelectTimeClick = { navigateTo(AppScreen.TIME_SELECTION) },
                            onScheduleConfirmed = { rideData ->
                                val travelDate = "${rideData.selectedDate} ${rideData.selectedMonth}"
                                val travelTime = "${rideData.selectedHour}:${rideData.selectedMinute} ${rideData.selectedPeriod}"
                                bookingFlowViewModel.updateScheduleDetails(travelDate, travelTime)
                                vehicleSelectionViewModel.updateRoute(
                                    origin = rideData.pickupLocation,
                                    destination = rideData.dropoffLocation,
                                    departureTime = "$travelDate, $travelTime"
                                )
                                navigateTo(AppScreen.VEHICLE_OPTIONS)
                            }
                        )
                    }
                    AppScreen.DATE_SELECTION -> {
                        // Full calendar date picker modal; pops back immediately on date selection
                        DateSelectionScreen(
                            initialDate = scheduleRideViewModel.uiState.value.rideData.selectedDate,
                            onBackClick = { handleScreenBack() },
                            onDateConfirmed = { day ->
                                scheduleRideViewModel.updateDate(day)
                                handleScreenBack()
                            }
                        )
                    }
                    AppScreen.TIME_SELECTION -> {
                        // Pickup time slot picker modal with 4-hour advance booking rule
                        val currentSlot = "${scheduleRideViewModel.uiState.value.rideData.selectedHour}:${scheduleRideViewModel.uiState.value.rideData.selectedMinute} ${scheduleRideViewModel.uiState.value.rideData.selectedPeriod}"
                        TimeSelectionScreen(
                            initialTimeSlot = currentSlot,
                            onBackClick = { handleScreenBack() },
                            onTimeConfirmed = { slot ->
                                scheduleRideViewModel.updateTime(slot)
                                handleScreenBack()
                            }
                        )
                    }

                    // -------------------------------------------------------------
                    // Phase 6: Vehicle Fleet Selection, Cab Details & Fare Breakdown
                    // -------------------------------------------------------------
                    AppScreen.VEHICLE_OPTIONS -> {
                        // Vehicle Fleet Selection: Hatchback, Sedan, SUV, Innova Crysta, Tempo Traveller
                        VehiclePartnerOptionsScreen(
                            viewModel = vehicleSelectionViewModel,
                            onBackClick = { handleScreenBack() },
                            onVehicleSelected = { vehicle ->
                                bookingFlowViewModel.handleAction(
                                    com.example.goindiacab.viewmodel.CabDetailAction.SelectVehicle(vehicle)
                                )
                                navigateTo(AppScreen.CAB_DETAIL)
                            }
                        )
                    }
                    AppScreen.CAB_DETAIL -> {
                        // Vehicle specifications, amenities, policy breakdown, and booking initiation
                        CabDetailScreen(
                            viewModel = bookingFlowViewModel,
                            onBackClick = { handleScreenBack() },
                            onBookNowClick = { detail ->
                                navigateTo(AppScreen.BOOKING_SUMMARY)
                            },
                            onViewFareDetailsClick = {
                                navigateTo(AppScreen.FARE_DETAILS)
                            }
                        )
                    }
                    AppScreen.FARE_DETAILS -> {
                        // Itemized fare breakdown: base rate, driver allowance, tolls, taxes, discounts
                        FareDetailsScreen(
                            viewModel = bookingFlowViewModel,
                            onBackClick = { handleScreenBack() },
                            onContinueClick = { navigateTo(AppScreen.BOOKING_SUMMARY) }
                        )
                    }
                    AppScreen.BOOKING_SUMMARY -> {
                        // Comprehensive pre-payment itinerary review, coupon trigger, and payment schedule
                        BookingSummaryScreen(
                            viewModel = bookingFlowViewModel,
                            onBackClick = { handleScreenBack() },
                            onApplyCouponClick = { navigateTo(AppScreen.APPLY_COUPON) },
                            onProceedToPayClick = { navigateTo(AppScreen.BOOKING_CONFIRMATION) }
                        )
                    }
                    AppScreen.APPLY_COUPON -> {
                        // Promo code validation and discount application modal sheet
                        ApplyCouponScreen(
                            viewModel = bookingFlowViewModel,
                            onBackClick = { handleScreenBack() }
                        )
                    }
                    AppScreen.PAYMENT_SCREEN -> {
                        // 10% advance deposit selection: UPI (GPay, PhonePe, Paytm), Cards, NetBanking
                        PaymentScreen(
                            viewModel = bookingFlowViewModel,
                            onBackClick = { handleScreenBack() },
                            onProceedToProcessing = { navigateTo(AppScreen.PAYMENT_PROCESSING) }
                        )
                    }
                    // -------------------------------------------------------------
                    // Phase 7: Payment Processing, Gateway Handshake & Error Recovery
                    // -------------------------------------------------------------
                    AppScreen.PAYMENT_PROCESSING -> {
                        PaymentProcessingScreen(
                            viewModel = bookingFlowViewModel,
                            onPaymentSuccess = { navigateTo(AppScreen.PARTNER_SEARCHING) },
                            onPaymentFailed = { navigateTo(AppScreen.PAYMENT_FAILED) }
                        )
                    }
                    AppScreen.PAYMENT_FAILED -> {
                        PaymentFailedScreen(
                            viewModel = bookingFlowViewModel,
                            onRetryPayment = {
                                // Pop failed state and restart gateway processing
                                popBackTo(AppScreen.PAYMENT_SCREEN)
                                navigateTo(AppScreen.PAYMENT_PROCESSING)
                            },
                            onChangePaymentMethod = {
                                // Unwind back to Payment Selection (Screen 32)
                                popBackTo(AppScreen.PAYMENT_SCREEN)
                            },
                            onBackClick = {
                                // Unwind back to Payment Selection (Screen 32)
                                popBackTo(AppScreen.PAYMENT_SCREEN)
                            }
                        )
                    }

                    // -------------------------------------------------------------
                    // Phase 8: Radar Driver Matching & Staged Fulfillment Lifecycle
                    // -------------------------------------------------------------
                    AppScreen.PARTNER_SEARCHING -> {
                        // Real-time concentric radar scan matching with local driver partners.
                        // Back navigation cancels search and unwinds cleanly to Booking Summary.
                        PartnerSearchingScreen(
                            viewModel = bookingFlowViewModel,
                            onPartnerFound = { navigateTo(AppScreen.PARTNER_ASSIGNED) },
                            onNoPartnerFound = { navigateTo(AppScreen.REFUND_INITIATED) },
                            onCancelSearch = {
                                // Unwind back to Booking Summary, cleanly popping intermediate payment spinners
                                popBackTo(AppScreen.BOOKING_SUMMARY)
                            }
                        )
                    }
                    AppScreen.PARTNER_ASSIGNED -> {
                        // Driver and vehicle assigned successfully with OTP and live vehicle details.
                        // Back navigation returns directly to Home screen with active booking session.
                        PartnerAssignedScreen(
                            viewModel = bookingFlowViewModel,
                            onViewTripDetailsClick = { navigateTo(AppScreen.TRIP_PAYMENT_SCHEDULE) },
                            onBackClick = {
                                // Once trip is confirmed, back returns to Home with active trip session
                                popBackTo(AppScreen.HOME)
                            }
                        )
                    }
                    AppScreen.REFUND_INITIATED -> {
                        RefundInitiatedScreen(
                            viewModel = bookingFlowViewModel,
                            onTryDifferentDateClick = {
                                // Rewind backstack to Schedule Ride to pick another date/time
                                popBackTo(AppScreen.SCHEDULE_RIDE)
                            },
                            onTryDifferentVehicleClick = {
                                // Rewind backstack to Vehicle Options to select a different category
                                popBackTo(AppScreen.VEHICLE_OPTIONS)
                            },
                            onBackClick = {
                                // Return to Home and reset navigation stack
                                navigateTo(AppScreen.HOME, clearStack = true)
                            }
                        )
                    }
                    AppScreen.TRIP_PAYMENT_SCHEDULE -> {
                        // 4-stage milestone payment tracking: Advance (10%), Pickup (40%), Mid-Trip (30%), Completion (20%)
                        TripPaymentScheduleScreen(
                            viewModel = bookingFlowViewModel,
                            onPayMilestoneClick = { _, _ -> navigateTo(AppScreen.PAYMENT_OTP_VERIFICATION) },
                            onBackClick = { popBackStack() }
                        )
                    }
                    AppScreen.PAYMENT_OTP_VERIFICATION -> {
                        // Pay 40% Trip Start Milestone & UPI confirmation (trip-start-payment.svg)
                        TripStartPaymentScreen(
                            viewModel = bookingFlowViewModel,
                            onPaymentConfirmed = {
                                toast("✓ Milestone Payment Verified Successfully!")
                                navigateTo(AppScreen.BOOKING_ID_CONFIRMATION)
                            },
                            onBackClick = { popBackStack() }
                        )
                    }
                    AppScreen.BOOKING_ID_CONFIRMATION -> {
                        // Official booking voucher with trip start OTP, QR code, and live vehicle tracking trigger
                        BookingIdConfirmationScreen(
                            viewModel = bookingFlowViewModel,
                            onBackToHomeClick = { navigateTo(AppScreen.HOME, clearStack = true) },
                            onTrackRideClick = {
                                navigateTo(AppScreen.HOME, clearStack = true)
                                toast("Ride tracking active: Driver Rajesh Kumar is 4 mins away")
                            }
                        )
                    }
                    AppScreen.BOOKING_CONFIRMATION -> {
                        // Post-payment receipt & review summary screen; back returns cleanly to Home root
                        BookingConfirmationScreen(
                            viewModel = bookingFlowViewModel,
                            onBackClick = { navigateTo(AppScreen.HOME, clearStack = true) },
                            onBackToHomeClick = { navigateTo(AppScreen.HOME, clearStack = true) },
                            onConfirmAndPayClick = { navigateTo(AppScreen.PAYMENT_SCREEN) }
                        )
                    }
                    AppScreen.PROFILE -> {
                        ProfileScreen(
                            onEditProfileClick = { navigateTo(AppScreen.EDIT_PROFILE) },
                            onMyBookingsClick = { navigateTo(AppScreen.HOME) },
                            onLocationClick = { navigateTo(AppScreen.SAVED_PLACES) },
                            onOffersCouponsClick = { navigateTo(AppScreen.APPLY_COUPON) },
                            onReferEarnClick = { navigateTo(AppScreen.REFER_EARN) },
                            onSettingsClick = { navigateTo(AppScreen.SETTINGS) },
                            onHelpSupportClick = { navigateTo(AppScreen.CUSTOMER_SUPPORT) },
                            onLogoutClick = { navigateTo(AppScreen.LOGIN, clearStack = true) },
                            onHomeTabClick = { navigateTo(AppScreen.HOME, clearStack = true) },
                            onTripsTabClick = { navigateTo(AppScreen.HOME) },
                            onOffersTabClick = { navigateTo(AppScreen.HOME) }
                        )
                    }
                    AppScreen.EDIT_PROFILE -> {
                        EditProfileScreen(
                            onBackClick = { handleScreenBack() },
                            onSaveClick = { _, _, _, _, _ ->
                                toast("Profile updated successfully")
                                popBackTo(AppScreen.PROFILE)
                            }
                        )
                    }
                    AppScreen.CUSTOMER_SUPPORT -> {
                        CustomerSupportScreen(
                            onBackClick = { handleScreenBack() },
                            onCallSupportClick = { toast("Calling GoIndiaCab Support: 1800-123-4567") },
                            onEmailSupportClick = { toast("Opening email to support@goindiacab.com") },
                            onSosClick = { toast("Connecting to Emergency Helpline 112...") },
                            onCategoryClick = { category -> toast("Opened: $category") }
                        )
                    }
                    AppScreen.RATE_US -> {
                        RateUsScreen(
                            onBackClick = { handleScreenBack() },
                            onRatePlayStoreClick = { stars ->
                                toast("Opening Play Store to rate $stars stars...")
                                popBackStack()
                            },
                            onMaybeLaterClick = { popBackStack() }
                        )
                    }
                    AppScreen.REFER_EARN -> {
                        ReferEarnScreen(
                            onBackClick = { handleScreenBack() },
                            onWhatsAppShareClick = { msg ->
                                toast("Sharing referral link via WhatsApp...")
                            },
                            onSmsShareClick = { msg ->
                                toast("Opening SMS to invite friend...")
                            }
                        )
                    }
                    AppScreen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = com.example.goindiacab.di.AppContainer.createSettingsViewModel(),
                            onBackClick = { handleScreenBack() }
                        )
                    }
                    AppScreen.SAVED_PLACES -> {
                        SavedPlacesScreen(
                            viewModel = com.example.goindiacab.di.AppContainer.createSavedPlacesViewModel(),
                            onBackClick = { handleScreenBack() },
                            onSelectPlace = { place ->
                                toast("Selected: ${place.title}")
                                handleScreenBack()
                            }
                        )
                    }
                    AppScreen.ABOUT -> {
                        AboutScreen(
                            viewModel = com.example.goindiacab.di.AppContainer.createAboutViewModel(),
                            onBackClick = { handleScreenBack() },
                            onPrivacyPolicyClick = { navigateTo(AppScreen.PRIVACY_POLICY) },
                            onTermsClick = { toast("Terms & Conditions") }
                        )
                    }
                    AppScreen.PRIVACY_POLICY -> {
                        PrivacyPolicyScreen(
                            onBackClick = { handleScreenBack() },
                            onContactOfficerClick = { toast("Opening security@goindiacab.com") }
                        )
                    }
                }
            }

            // Discreet Draggable Collapsible QA / Screen Previewer
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 64.dp, end = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Collapsed Minimal Chip
                Surface(
                    onClick = { showScreenPicker = !showScreenPicker },
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.7f),
                    contentColor = Color.White,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(BrandOrange)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentScreen.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily()
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showScreenPicker) "✕" else "⚙",
                            fontSize = 11.sp
                        )
                    }
                }

                // Expanded Chip Selector
                AnimatedVisibility(
                    visible = showScreenPicker,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.Black.copy(alpha = 0.9f),
                        shadowElevation = 14.dp,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .widthIn(max = 380.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AppScreen.entries.forEach { screen ->
                                val isSelected = screen == currentScreen
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (screen == AppScreen.HOME) {
                                            navigateTo(AppScreen.HOME, clearStack = true)
                                        } else {
                                            navigateTo(screen)
                                        }
                                        showScreenPicker = false
                                    },
                                    label = {
                                        Text(
                                            text = screen.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = outfitFontFamily(),
                                            color = if (isSelected) Color.White else Color(0xFFD1D5DB)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandOrange,
                                        containerColor = Color.White.copy(alpha = 0.12f)
                                    ),
                                    border = null,
                                    shape = RoundedCornerShape(50)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}