package com.example.goindiacab.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng

@SuppressLint("MissingPermission")
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }
    var googleMapRef by remember { mutableStateOf<GoogleMap?>(null) }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                mapView.onDestroy()
            } catch (_: Throwable) {}
        }
    }

    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasLocationPermission = fineGranted || coarseGranted

    // Handle recenter when trigger changes or initial load
    LaunchedEffect(recenterTrigger, googleMapRef) {
        val map = googleMapRef ?: return@LaunchedEffect
        if (recenterTrigger > 0) {
            if (hasLocationPermission) {
                try {
                    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedClient.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            val userLatLng = LatLng(loc.latitude, loc.longitude)
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16.5f))
                            onCameraIdle(loc.latitude, loc.longitude)
                        } else {
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom)
                            )
                        }
                    }
                } catch (_: SecurityException) {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom)
                    )
                }
            } else {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom)
                )
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.apply {
                getMapAsync { gMap ->
                    googleMapRef = gMap
                    gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    gMap.uiSettings.isMyLocationButtonEnabled = false
                    gMap.uiSettings.isCompassEnabled = true
                    gMap.uiSettings.isZoomControlsEnabled = false
                    gMap.uiSettings.isRotateGesturesEnabled = true
                    gMap.uiSettings.isTiltGesturesEnabled = true

                    if (hasLocationPermission && isMyLocationEnabled) {
                        try {
                            gMap.isMyLocationEnabled = true
                        } catch (_: SecurityException) {}
                    }

                    // Initial camera position
                    val initialTarget = LatLng(latitude, longitude)
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialTarget, zoom))

                    gMap.setOnCameraIdleListener {
                        val pos = gMap.cameraPosition.target
                        onCameraIdle(pos.latitude, pos.longitude)
                    }

                    gMap.setOnMapLoadedCallback {
                        onMapLoaded()
                    }
                }
            }
        },
        update = {
            googleMapRef?.let { gMap ->
                if (hasLocationPermission && isMyLocationEnabled) {
                    try {
                        if (!gMap.isMyLocationEnabled) {
                            gMap.isMyLocationEnabled = true
                        }
                    } catch (_: SecurityException) {}
                }
            }
        }
    )
}
