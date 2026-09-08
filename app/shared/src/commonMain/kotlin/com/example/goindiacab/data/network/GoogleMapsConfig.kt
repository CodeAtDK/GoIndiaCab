package com.example.goindiacab.data.network

/**
 * Centralized Google Maps & Places API Configuration.
 *
 * To use live Google Maps & Google Places data for all cities, airports, and street locations:
 * 1. Open Google Cloud Console: https://console.cloud.google.com/
 * 2. Ensure "Places API", "Places API (New)", and "Geocoding API" are enabled.
 * 3. Link an active Billing Account (Google requires billing for Places API requests).
 * 4. Paste your API key in [API_KEY] below and in `AndroidManifest.xml`.
 *
 * When billing is enabled on the key, the app queries Google Places Autocomplete in real-time.
 * When billing is pending or offline, the app seamlessly falls back to our exhaustive
 * built-in catalog of 80+ Indian cities, airports, and landmarks.
 */
object GoogleMapsConfig {
    // Paste your Google Cloud Maps API key here
    var API_KEY: String = "AIzaSyDYLWkpdUw4M4-aJr5DqYibO2BP1_VR2s4"

    const val PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json"
    const val PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json"
    const val GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json"
}
