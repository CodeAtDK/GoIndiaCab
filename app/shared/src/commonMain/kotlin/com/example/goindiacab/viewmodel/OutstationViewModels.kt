package com.example.goindiacab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.repository.OutstationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// 1. Outstation Route ViewModel (Screen 18)
// -------------------------------------------------------------
data class OutstationRouteUiState(
    val selectedTripType: OutstationTripType = OutstationTripType.ONE_WAY,
    val fromCity: String = "",
    val toCity: String = "",
    val popularCities: List<PopularCityItem> = OutstationSeedData.POPULAR_CITIES,
    val popularRoutes: List<PopularRouteFare> = OutstationSeedData.POPULAR_ONE_WAY_ROUTES,
    val isLoading: Boolean = false
)

class OutstationRouteViewModel(
    private val repository: OutstationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OutstationRouteUiState())
    val uiState: StateFlow<OutstationRouteUiState> = _uiState.asStateFlow()

    fun selectTripType(type: OutstationTripType) {
        _uiState.update { it.copy(selectedTripType = type) }
        viewModelScope.launch {
            repository.getPopularRoutes(type).collect { res ->
                if (res is NetworkResult.Success) {
                    _uiState.update { it.copy(popularRoutes = res.data) }
                }
            }
        }
    }

    fun selectCity(city: PopularCityItem) {
        _uiState.update { it.copy(toCity = city.name) }
    }

    fun selectCity(cityName: String) {
        _uiState.update { it.copy(toCity = cityName) }
    }

    fun setFromCity(city: String) {
        _uiState.update { it.copy(fromCity = city) }
    }

    fun selectRoute(route: PopularRouteFare) {
        _uiState.update {
            it.copy(
                fromCity = route.fromCity,
                toCity = route.toCity
            )
        }
    }

    fun selectRoute(fromCity: String, toCity: String) {
        _uiState.update {
            it.copy(
                fromCity = fromCity,
                toCity = toCity
            )
        }
    }
}

// -------------------------------------------------------------
// 2. Search Destination ViewModel (Screen 19)
// -------------------------------------------------------------
data class SearchDestinationUiState(
    val query: String = "",
    val recentSearches: List<RecentDestinationItem> = OutstationSeedData.RECENT_SEARCHES,
    val popularGetaways: List<PopularGetawayItem> = OutstationSeedData.POPULAR_GETAWAYS,
    val isSearching: Boolean = false
)

class SearchDestinationViewModel(
    private val repository: OutstationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchDestinationUiState())
    val uiState: StateFlow<SearchDestinationUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, isSearching = true) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(200)
            repository.searchDestinations(newQuery).collect { res ->
                if (res is NetworkResult.Success) {
                    _uiState.update { it.copy(recentSearches = res.data, isSearching = false) }
                } else {
                    _uiState.update { it.copy(isSearching = false) }
                }
            }
        }
    }

    fun clearQuery() {
        _uiState.update { it.copy(query = "", recentSearches = OutstationSeedData.RECENT_SEARCHES) }
    }
}

// -------------------------------------------------------------
// 3. Multi-Stop Route ViewModel (Screen 21)
// -------------------------------------------------------------
data class MultiStopRouteUiState(
    val routeData: MultiStopRouteData = MultiStopRouteData(
        pickupPoint = "",
        stops = emptyList(),
        dropoffPoint = "",
        totalDistanceKm = 580,
        estDurationText = "~10 Hours",
        totalEstimateInr = 12400
    ),
    val isScheduleOnly: Boolean = true
)

class MultiStopRouteViewModel(
    private val repository: OutstationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultiStopRouteUiState())
    val uiState: StateFlow<MultiStopRouteUiState> = _uiState.asStateFlow()

    fun addStop(name: String, subtitle: String) {
        val currentStops = _uiState.value.routeData.stops
        val newStop = RouteStopItem(
            id = "stop_${currentStops.size + 1}",
            stopNumber = currentStops.size + 1,
            name = name,
            locationSubtitle = subtitle,
            isDeletable = true
        )
        val updatedStops = currentStops + newStop
        val newDistance = 580 + (updatedStops.size - 2) * 75
        val newDuration = 10 + (updatedStops.size - 2) * 2
        val newFare = 12400 + (updatedStops.size - 2) * 1500

        _uiState.update {
            it.copy(
                routeData = it.routeData.copy(
                    stops = updatedStops,
                    totalDistanceKm = newDistance,
                    estDurationText = "~$newDuration Hours",
                    totalEstimateInr = newFare
                )
            )
        }
    }

    fun removeStop(stopId: String) {
        val currentStops = _uiState.value.routeData.stops.filter { it.id != stopId }
        val reindexed = currentStops.mapIndexed { index, stop ->
            stop.copy(stopNumber = index + 1)
        }
        val newDistance = 580 + (reindexed.size - 2).coerceAtLeast(0) * 75
        val newDuration = 10 + (reindexed.size - 2).coerceAtLeast(0) * 2
        val newFare = 12400 + (reindexed.size - 2).coerceAtLeast(0) * 1500

        _uiState.update {
            it.copy(
                routeData = it.routeData.copy(
                    stops = reindexed,
                    totalDistanceKm = newDistance,
                    estDurationText = "~$newDuration Hours",
                    totalEstimateInr = newFare
                )
            )
        }
    }

    fun setPickup(pickup: String) {
        _uiState.update { it.copy(routeData = it.routeData.copy(pickupPoint = pickup)) }
    }

    fun setDropoff(dropoff: String) {
        _uiState.update { it.copy(routeData = it.routeData.copy(dropoffPoint = dropoff)) }
    }

    fun setRoute(pickup: String, dropoff: String, distanceKm: Int? = null, fare: Int? = null) {
        _uiState.update {
            it.copy(
                routeData = it.routeData.copy(
                    pickupPoint = pickup,
                    dropoffPoint = dropoff,
                    totalDistanceKm = distanceKm ?: it.routeData.totalDistanceKm,
                    totalEstimateInr = fare ?: it.routeData.totalEstimateInr
                )
            )
        }
    }
}

// -------------------------------------------------------------
// 4. Schedule Ride ViewModel (Screens 22, 23, 24)
// -------------------------------------------------------------
data class ScheduleRideUiState(
    val rideData: ScheduleRideData = ScheduleRideData(),
    val availableTimeSlots: List<String> = OutstationSeedData.TIME_SLOTS,
    val isScheduledConfirmed: Boolean = false
)

class ScheduleRideViewModel(
    private val repository: OutstationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleRideUiState())
    val uiState: StateFlow<ScheduleRideUiState> = _uiState.asStateFlow()

    fun updateDate(day: Int, month: String = "October 2026") {
        _uiState.update {
            it.copy(
                rideData = it.rideData.copy(
                    selectedDate = day,
                    selectedMonth = month
                )
            )
        }
    }

    fun updateTime(timeString: String) {
        // e.g. "06:00 PM"
        val parts = timeString.split(" ")
        val timeParts = parts.getOrNull(0)?.split(":")
        val hour = timeParts?.getOrNull(0) ?: "06"
        val minute = timeParts?.getOrNull(1) ?: "00"
        val period = parts.getOrNull(1) ?: "PM"

        _uiState.update {
            it.copy(
                rideData = it.rideData.copy(
                    selectedHour = hour,
                    selectedMinute = minute,
                    selectedPeriod = period
                )
            )
        }
    }

    fun updateVehicle(vehicle: String) {
        val (minFare, maxFare) = when (vehicle) {
            "SUV" -> 2400 to 2900
            "Premium" -> 3200 to 3800
            else -> 1800 to 2200 // Sedan
        }
        _uiState.update {
            it.copy(
                rideData = it.rideData.copy(
                    selectedVehicle = vehicle,
                    estimatedFareMin = minFare,
                    estimatedFareMax = maxFare
                )
            )
        }
    }

    fun setLocations(pickup: String, dropoff: String) {
        _uiState.update {
            it.copy(
                rideData = it.rideData.copy(
                    pickupLocation = pickup,
                    dropoffLocation = dropoff
                )
            )
        }
    }

    fun confirmSchedule(onSuccess: (ScheduleRideData) -> Unit) {
        _uiState.update { it.copy(isScheduledConfirmed = true) }
        onSuccess(_uiState.value.rideData)
    }
}
