package com.example.goindiacab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goindiacab.data.models.LocationItem
import com.example.goindiacab.data.models.LocationSeedData
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
 * UI State for Screen 16: Pickup Location Selection.
 */
data class LocationSelectionUiState(
    val fromLocation: String = "",
    val toQuery: String = "",
    val searchResults: List<LocationItem> = LocationSeedData.DEFAULT_SEARCH_RESULTS,
    val favoriteIds: Set<String> = emptySet(),
    val isSearching: Boolean = false,
    val isGpsLocating: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Production ViewModel managing location search, query debouncing, favorites, and GPS resolution.
 */
class LocationSelectionViewModel(
    private val repository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationSelectionUiState())
    val uiState: StateFlow<LocationSelectionUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Observe repository favorites
        viewModelScope.launch {
            repository.getFavoriteIds().collect { favs ->
                _uiState.update { it.copy(favoriteIds = favs) }
            }
        }
    }

    fun onFromLocationChanged(from: String) {
        _uiState.update { it.copy(fromLocation = from) }
    }

    fun onToQueryChanged(query: String) {
        _uiState.update { it.copy(toQuery = query, isSearching = true) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(250) // 250ms debounce
            repository.searchLocations(query).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                searchResults = result.data,
                                isSearching = false,
                                errorMessage = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(isSearching = false, errorMessage = result.message)
                        }
                    }
                    is NetworkResult.Exception -> {
                        _uiState.update {
                            it.copy(isSearching = false, errorMessage = result.throwable.message)
                        }
                    }
                }
            }
        }
    }

    fun clearToQuery() {
        _uiState.update {
            it.copy(
                toQuery = "",
                searchResults = LocationSeedData.DEFAULT_SEARCH_RESULTS,
                isSearching = false
            )
        }
    }

    fun swapLocations() {
        _uiState.update { current ->
            if (current.toQuery.isNotBlank()) {
                current.copy(
                    fromLocation = current.toQuery,
                    toQuery = current.fromLocation
                )
            } else {
                current
            }
        }
    }

    fun toggleFavorite(locationId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(locationId)
        }
    }

    fun useCurrentLocation(onResolved: (LocationItem) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGpsLocating = true) }
            when (val res = repository.getCurrentLocation()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isGpsLocating = false) }
                    onResolved(res.data)
                }
                else -> {
                    _uiState.update { it.copy(isGpsLocating = false) }
                    // Fallback to current location item
                    val fallback = LocationItem(
                        id = "loc_current_gps",
                        title = "Current Location",
                        subtitle = "Detected via GPS",
                        point = com.example.goindiacab.data.models.GeoPoint(28.6139, 77.2090),
                        category = com.example.goindiacab.data.models.LocationCategory.LANDMARK
                    )
                    onResolved(fallback)
                }
            }
        }
    }
}
