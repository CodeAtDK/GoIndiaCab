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
