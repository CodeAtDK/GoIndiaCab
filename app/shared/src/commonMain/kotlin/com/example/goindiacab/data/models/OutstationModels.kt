package com.example.goindiacab.data.models

import kotlinx.serialization.Serializable

/**
 * Trip modes for outstation booking.
 */
@Serializable
enum class OutstationTripType(val title: String) {
    ONE_WAY("One Way"),
    ROUND_TRIP("Round Trip"),
    AIRPORT("Airport"),
    HOURLY("Hourly")
}

/**
 * Popular city chip in outstation route selection.
 */
@Serializable
data class PopularCityItem(
    val id: String,
    val name: String,
    val iconKey: String
)

/**
 * Popular one-way route item with distance and pricing.
 */
@Serializable
data class PopularRouteFare(
    val id: String,
    val fromCity: String,
    val toCity: String,
    val distanceKm: Int,
    val priceInr: Int
)

/**
 * Recent destination search entry.
 */
@Serializable
data class RecentDestinationItem(
    val id: String,
    val title: String,
    val state: String,
    val distanceKm: Int
)

/**
 * Popular weekend getaway destination card.
 */
@Serializable
data class PopularGetawayItem(
    val id: String,
    val city: String,
    val landmark: String,
    val imageDrawableKey: String
)

/**
 * Intermediate or endpoint stop in a multi-stop itinerary.
 */
@Serializable
data class RouteStopItem(
    val id: String,
    val stopNumber: Int,
    val name: String,
    val locationSubtitle: String,
    val isDeletable: Boolean = true
)

/**
 * Aggregate summary for multi-stop route planning.
 */
@Serializable
data class MultiStopRouteData(
    val pickupPoint: String = "",
    val stops: List<RouteStopItem> = emptyList(),
    val dropoffPoint: String = "",
    val totalDistanceKm: Int = 0,
    val estDurationText: String = "",
    val totalEstimateInr: Int = 0
)

/**
 * Configuration payload for a scheduled ride.
 */
@Serializable
data class ScheduleRideData(
    val pickupLocation: String = "",
    val dropoffLocation: String = "",
    val selectedDate: Int = 24,
    val selectedMonth: String = "October 2026",
    val selectedHour: String = "04",
    val selectedMinute: String = "30",
    val selectedPeriod: String = "PM",
    val selectedVehicle: String = "Sedan",
    val estimatedFareMin: Int = 1800,
    val estimatedFareMax: Int = 2200
)

/**
 * Pre-seeded canonical data matching SVGs 18, 19, 21, 22.
 */
object OutstationSeedData {
    val POPULAR_CITIES = listOf(
        PopularCityItem("city_delhi", "Delhi", "monument"),
        PopularCityItem("city_mumbai", "Mumbai", "gateway"),
        PopularCityItem("city_bengaluru", "Bengaluru", "tech"),
        PopularCityItem("city_chennai", "Chennai", "temple"),
        PopularCityItem("city_hyderabad", "Hyderabad", "charminar"),
        PopularCityItem("city_kolkata", "Kolkata", "bridge")
    )

    val POPULAR_ONE_WAY_ROUTES = listOf(
        PopularRouteFare("route_delhi_agra", "Delhi", "Agra", 230, 2499),
        PopularRouteFare("route_delhi_jaipur", "Delhi", "Jaipur", 270, 2999),
        PopularRouteFare("route_mumbai_pune", "Mumbai", "Pune", 150, 1999)
    )

    val RECENT_SEARCHES = listOf(
        RecentDestinationItem("dest_agra", "Agra", "Uttar Pradesh", 230),
        RecentDestinationItem("dest_jaipur", "Jaipur", "Rajasthan", 270),
        RecentDestinationItem("dest_chd", "Chandigarh", "Punjab", 250)
    )

    val POPULAR_GETAWAYS = listOf(
        PopularGetawayItem("getaway_agra", "Agra", "Taj Mahal", "img_getaway_agra"),
        PopularGetawayItem("getaway_jaipur", "Jaipur", "Forts", "img_getaway_jaipur"),
        PopularGetawayItem("getaway_haridwar", "Haridwar", "Ganges", "img_getaway_haridwar")
    )

    val DEFAULT_MULTI_STOPS = listOf(
        RouteStopItem("stop_khatu", 1, "Khatu Shyam Temple", "Rajasthan", isDeletable = true),
        RouteStopItem("stop_salasar", 2, "Salasar Balaji", "Rajasthan", isDeletable = true)
    )

    val TIME_SLOTS = listOf(
        "05:00 PM", "05:30 PM",
        "06:00 PM", "06:30 PM",
        "07:00 PM", "07:30 PM",
        "08:00 PM", "08:30 PM"
    )
}
