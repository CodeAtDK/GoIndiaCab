package com.example.goindiacab.data.repository

import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.network.HomeApiService
import com.example.goindiacab.util.ioDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Repository interface for Home feed and cab booking operations.
 */
interface HomeRepository {
    val cachedFeed: Flow<HomeFeedData?>
    suspend fun getHomeFeed(forceRefresh: Boolean = false): NetworkResult<HomeFeedData>
    suspend fun searchCabs(query: CabSearchQuery): NetworkResult<Boolean>
    suspend fun bookAgain(tripId: String): NetworkResult<String>
}

/**
 * Production implementation of HomeRepository with in-memory caching and offline-first support.
 */
class HomeRepositoryImpl(
    private val apiService: HomeApiService
) : HomeRepository {

    private val _cachedFeed = MutableStateFlow<HomeFeedData?>(null)
    override val cachedFeed: Flow<HomeFeedData?> = _cachedFeed.asStateFlow()

    override suspend fun getHomeFeed(forceRefresh: Boolean): NetworkResult<HomeFeedData> = withContext(ioDispatcher) {
        // Return cached feed immediately if available and not forcing refresh
        val current = _cachedFeed.value
        if (!forceRefresh && current != null) {
            return@withContext NetworkResult.Success(current)
        }

        // Fetch fresh data from API
        when (val result = apiService.getHomeFeed()) {
            is NetworkResult.Success -> {
                _cachedFeed.value = result.data
                result
            }
            is NetworkResult.Error -> {
                if (current != null) NetworkResult.Success(current) else result
            }
            is NetworkResult.Exception -> {
                if (current != null) NetworkResult.Success(current) else result
            }
        }
    }

    override suspend fun searchCabs(query: CabSearchQuery): NetworkResult<Boolean> = withContext(ioDispatcher) {
        apiService.searchCabs(query)
    }

    override suspend fun bookAgain(tripId: String): NetworkResult<String> = withContext(ioDispatcher) {
        apiService.bookAgain(tripId)
    }
}
