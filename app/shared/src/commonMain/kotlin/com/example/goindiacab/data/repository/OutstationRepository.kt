package com.example.goindiacab.data.repository

import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.network.OutstationApiService
import com.example.goindiacab.util.ioDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Interface contract for Outstation & Scheduled rides repository.
 */
interface OutstationRepository {
    fun getPopularRoutes(tripType: OutstationTripType): Flow<NetworkResult<List<PopularRouteFare>>>
    fun searchDestinations(query: String): Flow<NetworkResult<List<RecentDestinationItem>>>
    suspend fun calculateMultiStopRoute(stops: List<String>): NetworkResult<MultiStopRouteData>
    suspend fun getTimeSlots(date: String): NetworkResult<List<String>>
}

/**
 * Production implementation of OutstationRepository with caching and IO dispatching.
 */
class OutstationRepositoryImpl(
    private val apiService: OutstationApiService
) : OutstationRepository {

    override fun getPopularRoutes(tripType: OutstationTripType): Flow<NetworkResult<List<PopularRouteFare>>> = flow {
        val result = apiService.getPopularRoutes(tripType)
        emit(result)
    }.flowOn(ioDispatcher)

    override fun searchDestinations(query: String): Flow<NetworkResult<List<RecentDestinationItem>>> = flow {
        val result = apiService.searchDestinations(query)
        emit(result)
    }.flowOn(ioDispatcher)

    override suspend fun calculateMultiStopRoute(stops: List<String>): NetworkResult<MultiStopRouteData> = withContext(ioDispatcher) {
        apiService.calculateMultiStopRoute(stops)
    }

    override suspend fun getTimeSlots(date: String): NetworkResult<List<String>> = withContext(ioDispatcher) {
        apiService.getAvailableTimeSlots(date)
    }
}
