package com.example.goindiacab.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Result wrapper for network and repository operations.
 */
sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>
    data class Exception(val throwable: Throwable) : NetworkResult<Nothing>
}

inline fun <T> NetworkResult<T>.onSuccess(action: (value: T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onError(action: (code: Int, message: String) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(code, message)
    return this
}

inline fun <T> NetworkResult<T>.onException(action: (throwable: Throwable) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Exception) action(throwable)
    return this
}

/**
 * Service Type enumeration representing supported ride categories.
 */
@Serializable
enum class ServiceType(val title: String, val isNewBadge: Boolean = false) {
    @SerialName("outstation_one_way")
    OUTSTATION_ONE_WAY("Outstation One-Way"),

    @SerialName("outstation_round_trip")
    OUTSTATION_ROUND_TRIP("Outstation Round-Trip"),

    @SerialName("airport_transfers")
    AIRPORT_TRANSFERS("Airport Transfers"),

    @SerialName("hourly_rentals")
    HOURLY_RENTALS("Hourly Rentals", isNewBadge = true),

    @SerialName("group_travel")
    GROUP_TRAVEL("Group Travel"),

    @SerialName("online_cab_booking")
    ONLINE_CAB_BOOKING("Online Cab Booking")
}

/**
 * Popular Route domain model.
 */
@Immutable
@Serializable
data class PopularRoute(
    val id: String,
    val fromCity: String,
    val toCity: String,
    val distanceKm: Int,
    val durationText: String,
    val priceInr: Int,
    val imageUrl: String? = null,
    val isFeatured: Boolean = false
) {
    val formattedPrice: String get() = "₹$priceInr"
    val formattedDistanceDuration: String get() = "$distanceKm km • $durationText"
    val routeTitle: String get() = "$fromCity → $toCity"
}

/**
 * Recent Trip domain model.
 */
@Immutable
@Serializable
data class RecentTrip(
    val id: String,
    val fromLocation: String,
    val toLocation: String,
    val formattedDate: String,
    val fareInr: Int,
    val statusColorHex: String = "#10B981"
) {
    val formattedFare: String get() = "₹$fareInr"
    val routeTitle: String get() = "$fromLocation → $toLocation"
}

/**
 * Promotional Discount Banner.
 */
@Immutable
@Serializable
data class PromoBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val promoCode: String,
    val validTill: String,
    val discountPercent: Int
)

/**
 * Why Choose Us feature item.
 */
@Immutable
@Serializable
data class WhyChooseUsItem(
    val id: String,
    val title: String,
    val iconKey: String
)

/**
 * Complete Home Feed aggregate payload.
 */
@Immutable
@Serializable
data class HomeFeedData(
    val popularRoutes: List<PopularRoute>,
    val recentTrips: List<RecentTrip>,
    val promoBanner: PromoBanner,
    val whyFeatures: List<WhyChooseUsItem>,
    val defaultPickup: String = "",
    val defaultDropPlaceholder: String = "Where do you want to go?",
    val defaultDateTime: String = "Today, 10:30 AM",
    val defaultVehicleCategory: String = "Sedan / SUV"
)

/**
 * Query model for searching cabs.
 */
@Immutable
@Serializable
data class CabSearchQuery(
    val serviceType: ServiceType,
    val pickupLocation: String,
    val dropLocation: String,
    val pickupDateTime: String,
    val vehicleType: String
)
