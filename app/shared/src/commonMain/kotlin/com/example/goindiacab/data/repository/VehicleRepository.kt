package com.example.goindiacab.data.repository

import com.example.goindiacab.data.models.NetworkResult
import com.example.goindiacab.data.models.RouteSummaryHeader
import com.example.goindiacab.data.models.VehiclePartnerOption
import com.example.goindiacab.data.network.VehicleApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository interface defining vehicle fleet querying and route pricing contracts.
 */
interface VehicleRepository {
    fun getVehicleOptions(origin: String, destination: String): Flow<NetworkResult<List<VehiclePartnerOption>>>
    fun getRouteSummary(origin: String, destination: String): Flow<NetworkResult<RouteSummaryHeader>>
}

/**
 * Production implementation of VehicleRepository.
 * Emits reactive Flow streams with loading and success/error states.
 */
class VehicleRepositoryImpl(
    private val apiService: VehicleApiService
) : VehicleRepository {

    override fun getVehicleOptions(
        origin: String,
        destination: String
    ): Flow<NetworkResult<List<VehiclePartnerOption>>> = flow {
        emit(apiService.getVehicleOptions(origin, destination))
    }

    override fun getRouteSummary(
        origin: String,
        destination: String
    ): Flow<NetworkResult<RouteSummaryHeader>> = flow {
        emit(apiService.getRouteSummary(origin, destination))
    }
}
