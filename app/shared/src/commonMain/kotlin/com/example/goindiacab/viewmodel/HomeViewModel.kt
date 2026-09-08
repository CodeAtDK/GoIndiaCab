package com.example.goindiacab.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Immutable UI state for the Home Screen.
 */
@Immutable
data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val feedData: HomeFeedData? = null,
    val selectedService: ServiceType = ServiceType.AIRPORT_TRANSFERS,
    val pickupLocation: String = "",
    val dropLocation: String = "",
    val pickupDateTime: String = "Today, 10:30 AM",
    val vehicleType: String = "Sedan / SUV",
    val userMessage: String? = null,
    val errorMessage: String? = null
)

/**
 * Production ViewModel managing the Home Screen state via Coroutines and Flow.
 */
class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFeed(forceRefresh = false)
    }

    /**
     * Loads the Home Feed data asynchronously.
     */
    fun loadFeed(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else if (_uiState.value.feedData == null) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            when (val result = repository.getHomeFeed(forceRefresh = forceRefresh)) {
                is NetworkResult.Success -> {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            feedData = result.data,
                            pickupLocation = current.pickupLocation.ifBlank { result.data.defaultPickup },
                            pickupDateTime = result.data.defaultDateTime,
                            vehicleType = result.data.defaultVehicleCategory,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = "Error ${result.code}: ${result.message}"
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.throwable.message ?: "An unexpected error occurred"
                        )
                    }
                }
            }
        }
    }

    /**
     * Trigger pull-to-refresh.
     */
    fun refresh() {
        loadFeed(forceRefresh = true)
    }

    /**
     * Updates selected Service Type radio chip.
     */
    fun selectService(service: ServiceType) {
        _uiState.update { it.copy(selectedService = service) }
    }

    /**
     * Updates pickup location text.
     */
    fun updatePickupLocation(location: String) {
        _uiState.update { it.copy(pickupLocation = location) }
    }

    /**
     * Updates drop location text.
     */
    fun updateDropLocation(location: String) {
        _uiState.update { it.copy(dropLocation = location) }
    }

    /**
     * Executes Cab Search with current query parameters.
     */
    fun searchCabs(onSuccess: () -> Unit = {}) {
        val current = _uiState.value
        val query = CabSearchQuery(
            serviceType = current.selectedService,
            pickupLocation = current.pickupLocation,
            dropLocation = current.dropLocation,
            pickupDateTime = current.pickupDateTime,
            vehicleType = current.vehicleType
        )

        viewModelScope.launch {
            repository.searchCabs(query)
            _uiState.update {
                it.copy(userMessage = "Searching ${current.selectedService.title} cabs for ${query.pickupLocation}...")
            }
            onSuccess()
        }
    }

    /**
     * Rebooks a recent trip.
     */
    fun bookAgain(trip: RecentTrip) {
        viewModelScope.launch {
            repository.bookAgain(trip.id)
            val parts = trip.routeTitle.split("→", "->").map { it.trim() }
            _uiState.update {
                it.copy(
                    pickupLocation = if (parts.isNotEmpty() && parts[0].isNotBlank()) parts[0] else it.pickupLocation,
                    dropLocation = if (parts.size > 1 && parts[1].isNotBlank()) parts[1] else it.dropLocation,
                    userMessage = "Rebooking ride: ${trip.routeTitle}"
                )
            }
        }
    }

    /**
     * Clears toast/snackbar message.
     */
    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    /**
     * Clears error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
