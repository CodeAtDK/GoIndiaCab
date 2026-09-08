package com.example.goindiacab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun PlatformMapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Float,
    isMyLocationEnabled: Boolean,
    recenterTrigger: Int,
    onCameraIdle: (latitude: Double, longitude: Double) -> Unit,
    onMapLoaded: () -> Unit
) {
    LaunchedEffect(Unit) {
        onMapLoaded()
        onCameraIdle(latitude, longitude)
    }

    Box(modifier = modifier.background(Color(0xFFE8ECEF)))
}
