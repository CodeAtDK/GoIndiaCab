package com.example.goindiacab.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Sealed interface representing exhaustive UI state for Vehicle Selection.
 * Leverages Kotlin sealed interfaces and @Immutable annotations for Compose runtime efficiency.
 */
sealed interface VehicleSelectionUiState {
    data object Loading : VehicleSelectionUiState

    @Immutable
    data class Success(
        val routeHeader: RouteSummaryHeader,
        val selectedCategory: VehicleCategory = VehicleCategory.ALL,
        val allVehicles: List<VehiclePartnerOption>,
        val filteredVehicles: List<VehiclePartnerOption>,
        val selectedVehicleId: String,
        val isProcessingBooking: Boolean = false
    ) : VehicleSelectionUiState {
        val selectedVehicle: VehiclePartnerOption?
            get() = allVehicles.find { it.id == selectedVehicleId } ?: allVehicles.firstOrNull()

        val standardVehicles: List<VehiclePartnerOption>
            get() = filteredVehicles.filter { !it.isGroupTravel }

        val groupTravelVehicles: List<VehiclePartnerOption>
            get() = filteredVehicles.filter { it.isGroupTravel }

        val hasGroupTravel: Boolean
            get() = groupTravelVehicles.isNotEmpty()
    }

    data class Error(val message: String) : VehicleSelectionUiState
}

/**
 * Sealed interface representing UI user actions / intents (UDF pattern).
 */
sealed interface VehicleSelectionAction {
    data class SelectCategory(val category: VehicleCategory) : VehicleSelectionAction
    data class SelectVehicle(val vehicleId: String) : VehicleSelectionAction
    data class BookVehicle(val vehicleId: String, val onContinue: (VehiclePartnerOption) -> Unit) : VehicleSelectionAction
    data object Retry : VehicleSelectionAction
    data object Refresh : VehicleSelectionAction
}

/**
 * Production-grade MVVM ViewModel for Screen 25: Choose Your Vehicle.
 */
class VehicleSelectionViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VehicleSelectionUiState>(VehicleSelectionUiState.Loading)
    val uiState: StateFlow<VehicleSelectionUiState> = _uiState.asStateFlow()

    init {
        loadVehicleData()
    }

    fun handleAction(action: VehicleSelectionAction) {
        when (action) {
            is VehicleSelectionAction.SelectCategory -> selectCategory(action.category)
            is VehicleSelectionAction.SelectVehicle -> selectVehicle(action.vehicleId)
            is VehicleSelectionAction.BookVehicle -> bookVehicle(action.vehicleId, action.onContinue)
            is VehicleSelectionAction.Retry -> loadVehicleData()
            is VehicleSelectionAction.Refresh -> loadVehicleData()
        }
    }

    fun selectCategory(category: VehicleCategory) {
        val current = _uiState.value
        if (current is VehicleSelectionUiState.Success) {
            val filtered = current.allVehicles.filter { it.matchesCategory(category) }

            // If selected vehicle is not in filtered list, auto-select first available or preserve
            val newSelectedId = if (filtered.any { it.id == current.selectedVehicleId }) {
                current.selectedVehicleId
            } else {
                filtered.firstOrNull()?.id ?: current.selectedVehicleId
            }

            _uiState.update {
                current.copy(
                    selectedCategory = category,
                    filteredVehicles = filtered,
                    selectedVehicleId = newSelectedId
                )
            }
        }
    }

    fun selectVehicle(vehicleId: String) {
        val current = _uiState.value
        if (current is VehicleSelectionUiState.Success) {
            _uiState.update {
                current.copy(selectedVehicleId = vehicleId)
            }
        }
    }

    fun bookVehicle(vehicleId: String, onContinue: (VehiclePartnerOption) -> Unit) {
        val current = _uiState.value
        if (current is VehicleSelectionUiState.Success) {
            val vehicle = current.allVehicles.find { it.id == vehicleId } ?: current.selectedVehicle
            if (vehicle != null) {
                _uiState.update { current.copy(selectedVehicleId = vehicle.id) }
                onContinue(vehicle)
            }
        }
    }

    fun updateRoute(
        origin: String,
        destination: String,
        distanceKm: Int? = null,
        tripType: String? = null,
        departureTime: String? = null
    ) {
        val cleanOrigin = origin.substringBefore(",").substringBefore("(").trim()
        val cleanDest = destination.substringBefore(",").substringBefore("(").trim()
        loadVehicleData(cleanOrigin, cleanDest, distanceKm, tripType, departureTime)
    }

    private fun loadVehicleData(
        origin: String = "Delhi",
        destination: String = "Agra",
        distanceKm: Int? = null,
        tripType: String? = null,
        departureTime: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = VehicleSelectionUiState.Loading
            try {
                repository.getVehicleOptions(origin, destination).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            val vehicles = result.data
                            val defaultSelectedId = vehicles.firstOrNull { it.isRecommended }?.id
                                ?: vehicles.firstOrNull()?.id
                                ?: ""

                            val header = VehicleSeedData.DEFAULT_ROUTE.copy(
                                origin = origin,
                                destination = destination,
                                distanceKm = distanceKm ?: VehicleSeedData.DEFAULT_ROUTE.distanceKm,
                                tripType = tripType ?: VehicleSeedData.DEFAULT_ROUTE.tripType,
                                departureTime = departureTime ?: VehicleSeedData.DEFAULT_ROUTE.departureTime
                            )

                            _uiState.value = VehicleSelectionUiState.Success(
                                routeHeader = header,
                                selectedCategory = VehicleCategory.ALL,
                                allVehicles = vehicles,
                                filteredVehicles = vehicles,
                                selectedVehicleId = defaultSelectedId
                            )
                        }
                        is NetworkResult.Error -> {
                            _uiState.value = VehicleSelectionUiState.Error(
                                result.message
                            )
                        }
                        is NetworkResult.Exception -> {
                            _uiState.value = VehicleSelectionUiState.Error(
                                result.throwable.message ?: "Failed to load vehicle options. Please check network."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = VehicleSelectionUiState.Error(
                    e.message ?: "An unexpected error occurred."
                )
            }
        }
    }
}
