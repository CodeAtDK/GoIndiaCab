package com.example.goindiacab.data.network

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data models for Google Maps Places Autocomplete API response.
 */
@Immutable
@Serializable
data class GooglePlacesAutocompleteResponse(
    val status: String = "",
    val predictions: List<GooglePlacePrediction> = emptyList(),
    @SerialName("error_message") val errorMessage: String? = null
)

@Immutable
@Serializable
data class GooglePlacePrediction(
    @SerialName("place_id") val placeId: String = "",
    val description: String = "",
    @SerialName("structured_formatting") val structuredFormatting: GoogleStructuredFormatting? = null
)

@Immutable
@Serializable
data class GoogleStructuredFormatting(
    @SerialName("main_text") val mainText: String = "",
    @SerialName("secondary_text") val secondaryText: String = ""
)

@Immutable
@Serializable
data class GoogleGeocodeResponse(
    val status: String = "",
    val results: List<GoogleGeocodeResult> = emptyList(),
    @SerialName("error_message") val errorMessage: String? = null
)

@Immutable
@Serializable
data class GoogleGeocodeResult(
    @SerialName("formatted_address") val formattedAddress: String = "",
    @SerialName("address_components") val addressComponents: List<GoogleAddressComponent> = emptyList()
)

@Immutable
@Serializable
data class GoogleAddressComponent(
    @SerialName("long_name") val longName: String = "",
    @SerialName("short_name") val shortName: String = "",
    val types: List<String> = emptyList()
)

