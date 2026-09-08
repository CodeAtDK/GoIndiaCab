package com.example.goindiacab.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Multiplatform Map component.
 * On Android, this renders a live GoogleMap using Google Play Services Maps SDK.
 * On other platforms, it provides an interactive fallback.
 */
@Composable
expect fun PlatformMapView(
    modifier: Modifier = Modifier,
    latitude: Double = 28.6139,
    longitude: Double = 77.2090,
    zoom: Float = 16f,
    isMyLocationEnabled: Boolean = true,
    recenterTrigger: Int = 0,
    onCameraIdle: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    onMapLoaded: () -> Unit = {}
)
