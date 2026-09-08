package com.example.goindiacab.data.repository

import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.network.LocationApiService
import com.example.goindiacab.util.ioDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Interface contract for location data operations.
 */
interface LocationRepository {
    fun searchLocations(query: String): Flow<NetworkResult<List<LocationItem>>>
    suspend fun reverseGeocode(point: GeoPoint): NetworkResult<ReverseGeocodeResult>
    suspend fun getCurrentLocation(): NetworkResult<LocationItem>
    suspend fun toggleFavorite(locationId: String): Boolean
    fun getFavoriteIds(): StateFlow<Set<String>>
}

/**
 * Production implementation of LocationRepository with caching and reactive state.
 */
class LocationRepositoryImpl(
    private val apiService: LocationApiService
) : LocationRepository {

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    override fun getFavoriteIds(): StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    override fun searchLocations(query: String): Flow<NetworkResult<List<LocationItem>>> = flow {
        val result = apiService.searchPlaces(query)
        emit(result)
    }.flowOn(ioDispatcher)

    override suspend fun reverseGeocode(point: GeoPoint): NetworkResult<ReverseGeocodeResult> = withContext(ioDispatcher) {
        apiService.reverseGeocode(point)
    }

    override suspend fun getCurrentLocation(): NetworkResult<LocationItem> = withContext(ioDispatcher) {
        apiService.getCurrentGpsLocation()
    }

    override suspend fun toggleFavorite(locationId: String): Boolean = withContext(ioDispatcher) {
        val current = _favoriteIds.value
        val isNowFavorite = if (current.contains(locationId)) {
            _favoriteIds.value = current - locationId
            false
        } else {
            _favoriteIds.value = current + locationId
            true
        }
        isNowFavorite
    }
}
