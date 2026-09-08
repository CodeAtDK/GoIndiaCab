package com.example.goindiacab.data.models

import kotlinx.serialization.Serializable

/**
 * Geographic coordinate representation (WGS 84).
 */
@Serializable
data class GeoPoint(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        val CONNAUGHT_PLACE = GeoPoint(28.6329, 77.2195)
        val NEW_DELHI_CENTER = GeoPoint(28.6139, 77.2090)
        val IGI_AIRPORT_T3 = GeoPoint(28.5562, 77.0864)
    }
}

/**
 * Represents a categorized point of interest or search result.
 */
@Serializable
enum class LocationCategory {
    TRANSIT,
    AIRPORT,
    HIGHWAY,
    LANDMARK,
    RESIDENTIAL
}

/**
 * Clean data model for location search results and selected points.
 */
@Serializable
data class LocationItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val point: GeoPoint,
    val isFavorite: Boolean = false,
    val category: LocationCategory = LocationCategory.TRANSIT
)

/**
 * Reverse geocoding result when dropping or moving the pin.
 */
@Serializable
data class ReverseGeocodeResult(
    val title: String,
    val fullAddress: String,
    val point: GeoPoint,
    val city: String = "New Delhi",
    val postalCode: String = "110001"
)

/**
 * Canonical high-fidelity seed locations matching GoIndiaCab design specifications.
 */
object LocationSeedData {
    val DEFAULT_SEARCH_RESULTS = listOf(
        LocationItem(
            id = "loc_sarai_rohilla",
            title = "Delhi Sarai Rohilla Railway Station",
            subtitle = "Guru Gobind Singh Marg, Railway Officers Colony",
            point = GeoPoint(28.6605, 77.1852),
            category = LocationCategory.TRANSIT
        ),
        LocationItem(
            id = "loc_mumbai_exp",
            title = "Delhi - Mumbai Expressway",
            subtitle = "Anagad, Gujarat, India",
            point = GeoPoint(28.4595, 77.0266),
            category = LocationCategory.HIGHWAY
        ),
        LocationItem(
            id = "loc_dehradun_exp",
            title = "Delhi - Dehradun Expressway",
            subtitle = "Gandhi Nagar, Seelampur, Shahdara",
            point = GeoPoint(28.6692, 77.2655),
            category = LocationCategory.HIGHWAY
        ),
        LocationItem(
            id = "loc_igi_t3",
            title = "Terminal 3, Indira Gandhi International Airport",
            subtitle = "Terminal 3 Delhi Airport, IGI",
            point = GeoPoint(28.5562, 77.0864),
            category = LocationCategory.AIRPORT
        ),
        LocationItem(
            id = "loc_igi_t2",
            title = "Terminal 2, Indira Gandhi International Airport",
            subtitle = "Terminal 2 Delhi Airport, IGI",
            point = GeoPoint(28.5583, 77.0898),
            category = LocationCategory.AIRPORT
        ),
        LocationItem(
            id = "loc_igi_t1c",
            title = "Terminal 1C - Arrival, IGI Airport",
            subtitle = "Terminal 1 Delhi Airport, IGI",
            point = GeoPoint(28.5670, 77.1120),
            category = LocationCategory.AIRPORT
        ),
        LocationItem(
            id = "loc_hindon_airport",
            title = "Hindon Airport (Ghaziabad)",
            subtitle = "Ghaziabad, Uttar Pradesh",
            point = GeoPoint(28.7061, 77.3592),
            category = LocationCategory.AIRPORT
        ),
        LocationItem(
            id = "loc_connaught_place",
            title = "Connaught Place",
            subtitle = "Connaught Place, New Delhi, 110001",
            point = GeoPoint(28.6329, 77.2195),
            category = LocationCategory.LANDMARK
        ),
        LocationItem(
            id = "loc_rajiv_chowk",
            title = "Rajiv Chowk Metro Station",
            subtitle = "Block B, Connaught Place, New Delhi",
            point = GeoPoint(28.6328, 77.2185),
            category = LocationCategory.TRANSIT
        ),
        LocationItem(
            id = "loc_india_gate",
            title = "India Gate",
            subtitle = "Rajpath, India Gate, New Delhi, 110001",
            point = GeoPoint(28.6129, 77.2295),
            category = LocationCategory.LANDMARK
        )
    )
}
