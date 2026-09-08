package com.example.goindiacab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goindiacab.data.models.GeoPoint
import com.example.goindiacab.data.models.LocationCategory
import com.example.goindiacab.data.models.LocationItem
import com.example.goindiacab.data.models.NetworkResult
import com.example.goindiacab.data.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Screen 17: Confirm Pickup Location Map.
 */
data class ConfirmPickupUiState(
    val targetPoint: GeoPoint = GeoPoint.CONNAUGHT_PLACE,
    val locationTitle: String = "",
    val fullAddress: String = "",
    val isDragging: Boolean = false,
    val isReverseGeocoding: Boolean = false,
    val isFavorite: Boolean = false,
    val mapOffsetX: Float = 0f,
    val mapOffsetY: Float = 0f,
    val mapZoom: Float = 16.0f,
    val recenterTrigger: Int = 0
)

/**
 * Production ViewModel managing the interactive map camera, pin elevation physics,
 * coordinate tracking, reverse geocoding, and pickup confirmation.
 */
class ConfirmPickupViewModel(
    private val repository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfirmPickupUiState())
    val uiState: StateFlow<ConfirmPickupUiState> = _uiState.asStateFlow()

    private var geocodeJob: Job? = null

    fun initializeWithLocation(item: LocationItem) {
        _uiState.update {
            it.copy(
                targetPoint = item.point,
                locationTitle = item.title,
                fullAddress = item.subtitle,
                isFavorite = item.isFavorite
            )
        }
    }

    fun onDragStarted() {
        _uiState.update { it.copy(isDragging = true) }
    }

    fun onMapPan(dragAmountX: Float, dragAmountY: Float) {
        _uiState.update { current ->
            val newX = (current.mapOffsetX + dragAmountX).coerceIn(-600f, 600f)
            val newY = (current.mapOffsetY + dragAmountY).coerceIn(-600f, 600f)

            // Approximate coordinate calculation based on pan offset
            val latDelta = -newY * 0.00002
            val lngDelta = newX * 0.00002
            val newPoint = GeoPoint(
                latitude = GeoPoint.CONNAUGHT_PLACE.latitude + latDelta,
                longitude = GeoPoint.CONNAUGHT_PLACE.longitude + lngDelta
            )

            current.copy(
                mapOffsetX = newX,
                mapOffsetY = newY,
                targetPoint = newPoint,
                isDragging = true
            )
        }
    }

    fun onDragEnded() {
        _uiState.update { it.copy(isDragging = false, isReverseGeocoding = true) }

        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch {
            delay(300) // Debounce reverse geocode
            val currentPoint = _uiState.value.targetPoint
            when (val res = repository.reverseGeocode(currentPoint)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            locationTitle = res.data.title,
                            fullAddress = res.data.fullAddress,
                            isReverseGeocoding = false
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isReverseGeocoding = false) }
                }
            }
        }
    }

    fun onCoordinatesMoved(latitude: Double, longitude: Double) {
        val newPoint = GeoPoint(latitude, longitude)
        _uiState.update { it.copy(targetPoint = newPoint, isReverseGeocoding = true) }

        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch {
            delay(300)
            when (val res = repository.reverseGeocode(newPoint)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            locationTitle = res.data.title,
                            fullAddress = res.data.fullAddress,
                            isReverseGeocoding = false
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isReverseGeocoding = false) }
                }
            }
        }
    }

    fun onZoomChange(zoomDelta: Float) {
        _uiState.update { current ->
            val newZoom = (current.mapZoom * zoomDelta).coerceIn(1.0f, 3.0f)
            current.copy(mapZoom = newZoom)
        }
    }

    fun recenterToGps() {
        _uiState.update {
            it.copy(
                targetPoint = GeoPoint.CONNAUGHT_PLACE,
                locationTitle = "Connaught Place",
                fullAddress = "Connaught Place, New Delhi, 110001",
                mapOffsetX = 0f,
                mapOffsetY = 0f,
                mapZoom = 1.0f,
                isDragging = false
            )
        }
    }

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun confirmPickup(onConfirmed: (LocationItem) -> Unit) {
        val state = _uiState.value
        val confirmedItem = LocationItem(
            id = "loc_${state.targetPoint.latitude}_${state.targetPoint.longitude}",
            title = state.locationTitle,
            subtitle = state.fullAddress,
            point = state.targetPoint,
            isFavorite = state.isFavorite,
            category = LocationCategory.LANDMARK
        )
        onConfirmed(confirmedItem)
    }
}
