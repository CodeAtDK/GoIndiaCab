package com.example.goindiacab.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Vehicle categorization categories matching filter chips.
 */
@Serializable
enum class VehicleCategory(val displayName: String) {
    ALL("All"),
    SEDAN("Sedan"),
    SUV("SUV"),
    PREMIUM("Premium"),
    GROUP_TRAVEL("Group Travel");

    /**
     * Kotlin pattern matching helper for category filtering.
     */
    fun matches(target: VehicleCategory): Boolean {
        return this == ALL || this == target
    }
}

/**
 * Domain & presentation model representing an available cab or partner vehicle option.
 * Annotated with @Immutable for Compose compiler optimization and recomposition skipping.
 */
@Immutable
@Serializable
data class VehiclePartnerOption(
    val id: String,
    val name: String,
    val category: VehicleCategory,
    val typeDescription: String,
    val seatingCapacity: Int,
    val hasAc: Boolean = true,
    val rating: Float = 4.5f,
    val isRecommended: Boolean = false,
    val badgeText: String? = null,
    val allInclusiveFare: Int,
    val perKmRate: Int? = null,
    val extraKmRate: Int = 12,
    val freeKmLimit: Int = 250,
    val nightCharges: Int = 250,
    val nightChargeWindow: String = "10PM–6AM",
    val imageDrawableKey: String,
    val features: List<String> = emptyList(),
    val secondaryCategories: Set<VehicleCategory> = emptySet()
) {
    val isGroupTravel: Boolean
        get() = category == VehicleCategory.GROUP_TRAVEL

    /**
     * Advanced category matching supporting primary and secondary tags.
     */
    fun matchesCategory(filter: VehicleCategory): Boolean {
        if (filter == VehicleCategory.ALL) return true
        return category == filter || filter in secondaryCategories
    }
}

/**
 * Route summary context displayed at the top of the vehicle selection screen.
 */
@Immutable
@Serializable
data class RouteSummaryHeader(
    val origin: String = "Delhi",
    val destination: String = "Agra",
    val distanceKm: Int = 230,
    val tripType: String = "One Way",
    val departureTime: String = "Today, 10:30 AM",
    val basePrice: Int = 3450
)

/**
 * Extension function for idiomatic INR currency formatting with commas (e.g. 3450 -> ₹3,450).
 */
fun Int.formatInr(): String {
    val str = this.toString()
    if (str.length <= 3) return "₹$str"
    val lastThree = str.takeLast(3)
    val remainder = str.dropLast(3)
    val formattedRemainder = remainder.reversed()
        .chunked(2)
        .joinToString(",")
        .reversed()
    return "₹$formattedRemainder,$lastThree"
}

/**
 * Canonical seed data extracted from Screen 25 (`vehicle-partner-options.svg`).
 * Exhaustively represents all 7 vehicle tiers with 100% fidelity to the Figma design.
 */
object VehicleSeedData {

    val DEFAULT_ROUTE = RouteSummaryHeader(
        origin = "Delhi",
        destination = "Agra",
        distanceKm = 230,
        tripType = "One Way",
        departureTime = "Today, 10:30 AM",
        basePrice = 3450
    )

    val VEHICLE_OPTIONS = listOf(
        // 1. Recommended Sedan: Swift Dzire
        VehiclePartnerOption(
            id = "veh_swift_dzire",
            name = "Swift Dzire",
            category = VehicleCategory.SEDAN,
            secondaryCategories = setOf(VehicleCategory.SEDAN),
            typeDescription = "Sedan • 4 Seater • AC",
            seatingCapacity = 4,
            hasAc = true,
            rating = 4.5f,
            isRecommended = true,
            badgeText = "RECOMMENDED",
            allInclusiveFare = 3450,
            extraKmRate = 12,
            freeKmLimit = 250,
            nightCharges = 250,
            nightChargeWindow = "10PM–6AM",
            imageDrawableKey = "img_car_swift_dzire",
            features = listOf("4 seats", "Petrol/CNG", "AC", "Boot Space 378L")
        ),
        // 2. Sedan: Honda City
        VehiclePartnerOption(
            id = "veh_honda_city",
            name = "Honda City",
            category = VehicleCategory.SEDAN,
            secondaryCategories = setOf(VehicleCategory.SEDAN, VehicleCategory.PREMIUM),
            typeDescription = "Sedan • 4 Seater • AC",
            seatingCapacity = 4,
            hasAc = true,
            rating = 4.3f,
            isRecommended = false,
            badgeText = null,
            allInclusiveFare = 3800,
            extraKmRate = 12,
            freeKmLimit = 250,
            nightCharges = 250,
            nightChargeWindow = "10PM–6AM",
            imageDrawableKey = "img_car_swift_dzire",
            features = listOf("4 seats", "Petrol", "AC", "Premium Comfort")
        ),
        // 3. Premium SUV: Toyota Innova Crysta
        VehiclePartnerOption(
            id = "veh_innova_crysta",
            name = "Toyota Innova Crysta",
            category = VehicleCategory.SUV,
            secondaryCategories = setOf(VehicleCategory.SUV, VehicleCategory.PREMIUM),
            typeDescription = "Premium SUV • 6 Seater • AC",
            seatingCapacity = 6,
            hasAc = true,
            rating = 4.9f,
            isRecommended = false,
            badgeText = null,
            allInclusiveFare = 5200,
            extraKmRate = 12,
            freeKmLimit = 250,
            nightCharges = 250,
            nightChargeWindow = "10PM–6AM",
            imageDrawableKey = "img_car_innova_crysta",
            features = listOf("6 seats", "Diesel", "AC", "Captain Seats")
        ),
        // 4. Group Travel: 9 Seater Tempo Traveller
        VehiclePartnerOption(
            id = "veh_tempo_9",
            name = "9 Seater Tempo Traveller",
            category = VehicleCategory.GROUP_TRAVEL,
            secondaryCategories = setOf(VehicleCategory.GROUP_TRAVEL),
            typeDescription = "Tempo Traveller • 9 Seater • AC",
            seatingCapacity = 9,
            hasAc = true,
            rating = 4.5f,
            isRecommended = false,
            badgeText = null,
            allInclusiveFare = 5290,
            perKmRate = 23,
            extraKmRate = 10,
            freeKmLimit = 250,
            nightCharges = 300,
            nightChargeWindow = "10PM–6AM",
            imageDrawableKey = "ic_tempo_traveller",
            features = listOf("9 seats", "Diesel", "AC")
        ),
        // 5. Group Travel: 12 Seater Tempo Traveller (Best for families)
        VehiclePartnerOption(
            id = "veh_tempo_12",
            name = "12 Seater Tempo Traveller",
            category = VehicleCategory.GROUP_TRAVEL,
            secondaryCategories = setOf(VehicleCategory.GROUP_TRAVEL),
            typeDescription = "Tempo Traveller • 12 Seater • AC",
            seatingCapacity = 12,
            hasAc = true,
            rating = 4.6f,
            isRecommended = false,
            badgeText = "BEST FOR FAMILIES",
            allInclusiveFare = 5520,
            perKmRate = 24,
            extraKmRate = 12,
            freeKmLimit = 250,
            nightCharges = 350,
            nightChargeWindow = "10PM–6AM",
            imageDrawableKey = "ic_tempo_traveller",
            features = listOf("12 seats", "Diesel", "AC", "Push-back seats")
        ),
        // 6. Group Travel: 16 Seater Tempo Traveller
        VehiclePartnerOption(
            id = "veh_tempo_16",
            name = "16 Seater Tempo Traveller",
            category = VehicleCategory.GROUP_TRAVEL,
            secondaryCategories = setOf(VehicleCategory.GROUP_TRAVEL),
            typeDescription = "Tempo Traveller • 16 Seater • AC",
            seatingCapacity = 16,
            hasAc = true,
            rating = 4.4f,
            isRecommended = false,
            badgeText = null,
            allInclusiveFare = 5980,
            perKmRate = 26,
            extraKmRate = 15,
            freeKmLimit = 250,
            nightCharges = 400,
            nightChargeWindow = "10PM–6AM",
            imageDrawableKey = "ic_tempo_traveller",
            features = listOf("16 seats", "Diesel", "AC", "Touring Audio")
        ),
        // 7. Group Travel: 26 Seater Tempo Traveller
        VehiclePartnerOption(
            id = "veh_tempo_26",
            name = "26 Seater Tempo Traveller",
            category = VehicleCategory.GROUP_TRAVEL,
            secondaryCategories = setOf(VehicleCategory.GROUP_TRAVEL),
            typeDescription = "Tempo Traveller • 26 Seater • AC",
            seatingCapacity = 26,
            hasAc = true,
            rating = 4.7f,
            isRecommended = false,
            badgeText = null,
            allInclusiveFare = 6900,
            perKmRate = 30,
            extraKmRate = 20,
            freeKmLimit = 250,
            nightCharges = 500,
            nightChargeWindow = "10PM–6AM",
            imageDrawableKey = "ic_tempo_traveller",
            features = listOf("26 seats", "Diesel", "AC", "Full Luxury Coach")
        )
    )
}
