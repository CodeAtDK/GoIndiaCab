package com.example.goindiacab.di

import com.example.goindiacab.data.network.HomeApiService
import com.example.goindiacab.data.network.HomeApiServiceImpl
import com.example.goindiacab.data.network.HttpClientFactory
import com.example.goindiacab.data.repository.HomeRepository
import com.example.goindiacab.data.repository.HomeRepositoryImpl
import com.example.goindiacab.viewmodel.HomeViewModel
import com.example.goindiacab.data.network.LocationApiService
import com.example.goindiacab.data.network.LocationApiServiceImpl
import com.example.goindiacab.data.repository.LocationRepository
import com.example.goindiacab.data.repository.LocationRepositoryImpl
import com.example.goindiacab.viewmodel.ConfirmPickupViewModel
import com.example.goindiacab.viewmodel.LocationSelectionViewModel
import io.ktor.client.*

/**
 * Multiplatform Dependency Injection Container.
 * Provides thread-safe, lazily initialized singletons and factory methods.
 */
object AppContainer {

    // Ktor HttpClient singleton
    val httpClient: HttpClient by lazy {
        HttpClientFactory.create()
    }

    // Home API Service
    val homeApiService: HomeApiService by lazy {
        HomeApiServiceImpl(httpClient = httpClient)
    }

    // Home Repository
    val homeRepository: HomeRepository by lazy {
        HomeRepositoryImpl(apiService = homeApiService)
    }

    // Location API Service
    val locationApiService: LocationApiService by lazy {
        LocationApiServiceImpl(httpClient = httpClient)
    }

    // Location Repository
    val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(apiService = locationApiService)
    }

    // Outstation API Service
    val outstationApiService: com.example.goindiacab.data.network.OutstationApiService by lazy {
        com.example.goindiacab.data.network.OutstationApiServiceImpl(httpClient = httpClient)
    }

    // Outstation Repository
    val outstationRepository: com.example.goindiacab.data.repository.OutstationRepository by lazy {
        com.example.goindiacab.data.repository.OutstationRepositoryImpl(apiService = outstationApiService)
    }

    // Vehicle API Service
    val vehicleApiService: com.example.goindiacab.data.network.VehicleApiService by lazy {
        com.example.goindiacab.data.network.VehicleApiServiceImpl(httpClient = httpClient)
    }

    // Vehicle Repository
    val vehicleRepository: com.example.goindiacab.data.repository.VehicleRepository by lazy {
        com.example.goindiacab.data.repository.VehicleRepositoryImpl(apiService = vehicleApiService)
    }

    /**
     * Factory to instantiate a fresh HomeViewModel with injected dependencies.
     */
    fun createHomeViewModel(): HomeViewModel {
        return HomeViewModel(repository = homeRepository)
    }

    /**
     * Factory to instantiate LocationSelectionViewModel with injected dependencies.
     */
    fun createLocationSelectionViewModel(): LocationSelectionViewModel {
        return LocationSelectionViewModel(repository = locationRepository)
    }

    /**
     * Factory to instantiate ConfirmPickupViewModel with injected dependencies.
     */
    fun createConfirmPickupViewModel(): ConfirmPickupViewModel {
        return ConfirmPickupViewModel(repository = locationRepository)
    }

    /**
     * Factory for OutstationRouteViewModel.
     */
    fun createOutstationRouteViewModel(): com.example.goindiacab.viewmodel.OutstationRouteViewModel {
        return com.example.goindiacab.viewmodel.OutstationRouteViewModel(repository = outstationRepository)
    }

    /**
     * Factory for SearchDestinationViewModel.
     */
    fun createSearchDestinationViewModel(): com.example.goindiacab.viewmodel.SearchDestinationViewModel {
        return com.example.goindiacab.viewmodel.SearchDestinationViewModel(repository = outstationRepository)
    }

    /**
     * Factory for MultiStopRouteViewModel.
     */
    fun createMultiStopRouteViewModel(): com.example.goindiacab.viewmodel.MultiStopRouteViewModel {
        return com.example.goindiacab.viewmodel.MultiStopRouteViewModel(repository = outstationRepository)
    }

    /**
     * Factory for ScheduleRideViewModel.
     */
    fun createScheduleRideViewModel(): com.example.goindiacab.viewmodel.ScheduleRideViewModel {
        return com.example.goindiacab.viewmodel.ScheduleRideViewModel(repository = outstationRepository)
    }

    /**
     * Factory for VehicleSelectionViewModel.
     */
    fun createVehicleSelectionViewModel(): com.example.goindiacab.viewmodel.VehicleSelectionViewModel {
        return com.example.goindiacab.viewmodel.VehicleSelectionViewModel(repository = vehicleRepository)
    }

    // Booking Session & Checkout Repository
    val bookingRepository: com.example.goindiacab.data.repository.BookingRepository by lazy {
        com.example.goindiacab.data.repository.BookingRepositoryImpl()
    }

    /**
     * Factory for BookingFlowViewModel (Screen 27 Cab Details and downstream checkout).
     */
    fun createBookingFlowViewModel(): com.example.goindiacab.viewmodel.BookingFlowViewModel {
        return com.example.goindiacab.viewmodel.BookingFlowViewModel(bookingRepository = bookingRepository)
    }

    /**
     * Factory for RateUsViewModel.
     */
    fun createRateUsViewModel(): com.example.goindiacab.viewmodel.RateUsViewModel {
        return com.example.goindiacab.viewmodel.RateUsViewModel()
    }

    /**
     * Factory for ReferEarnViewModel.
     */
    fun createReferEarnViewModel(): com.example.goindiacab.viewmodel.ReferEarnViewModel {
        return com.example.goindiacab.viewmodel.ReferEarnViewModel()
    }
}
