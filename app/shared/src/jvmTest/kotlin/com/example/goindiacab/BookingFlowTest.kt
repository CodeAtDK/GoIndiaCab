package com.example.goindiacab

import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.repository.BookingRepositoryImpl
import com.example.goindiacab.viewmodel.BookingFlowViewModel
import com.example.goindiacab.viewmodel.CabDetailAction
import com.example.goindiacab.viewmodel.CabDetailUiState
import com.example.goindiacab.viewmodel.PaymentUiState
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BookingFlowTest {

    @Test
    fun testDefaultBookingSessionVehicleIsInnovaCrysta() {
        val repository = BookingRepositoryImpl()
        val session = repository.bookingSession.value

        val selected = session.selectedVehicle
        assertNotNull(selected)
        assertEquals("veh_innova_crysta", selected.id)
        assertEquals("Toyota Innova Crysta", selected.name)
        assertEquals(5200, session.fareBreakdown.totalEstimatedFare)
    }

    @Test
    fun testUpdateSelectedVehicleRecalculatesFare() {
        val repository = BookingRepositoryImpl()
        val sedan = VehicleSeedData.VEHICLE_OPTIONS.first { it.id == "veh_swift_dzire" }

        repository.updateSelectedVehicle(sedan)
        val session = repository.bookingSession.value

        assertEquals("veh_swift_dzire", session.selectedVehicle?.id)
        assertEquals(2750, session.fareBreakdown.baseFare)
        assertEquals(3450, session.fareBreakdown.totalEstimatedFare)
    }

    @Test
    fun testCabDetailGenerationIncludesSpecsAndInclusions() = runBlocking {
        val repository = BookingRepositoryImpl()
        val result = repository.getCabDetailData(null).first()

        assertTrue(result is NetworkResult.Success)
        val detail = result.data
        assertEquals("Toyota Innova Crysta", detail.vehicle.name)
        assertEquals("Diesel", detail.specs.fuelType)
        assertEquals("Manual", detail.specs.transmission)
        assertEquals("3 large bags", detail.specs.luggageCapacity)
        assertEquals("Rear AC", detail.specs.acType)

        assertTrue(detail.inclusions.any { it.text.contains("250 km limit") })
        assertTrue(detail.exclusions.any { it.text.contains("₹12/km") })
        assertEquals(4, detail.hourlyCharges.size)
        assertEquals("Rahul S.", detail.review.reviewerName)
    }

    @Test
    fun testCouponApplicationAndRemoval() = runBlocking {
        val repository = BookingRepositoryImpl()

        // Valid coupon
        val validResult = repository.applyCoupon("WELCOME500").first()
        assertTrue(validResult is NetworkResult.Success)
        val updatedFare = validResult.data
        assertEquals(500, updatedFare.discountAmount)
        assertEquals("WELCOME500", updatedFare.couponCode)
        assertEquals(4700, updatedFare.totalEstimatedFare)

        // Invalid coupon
        val invalidResult = repository.applyCoupon("INVALID_CODE").first()
        assertTrue(invalidResult is NetworkResult.Error)

        // Remove coupon
        repository.removeCoupon()
        assertEquals(0, repository.bookingSession.value.fareBreakdown.discountAmount)
        assertEquals(null, repository.bookingSession.value.fareBreakdown.couponCode)
        assertEquals(5200, repository.bookingSession.value.fareBreakdown.totalEstimatedFare)
    }

    @Test
    fun testBookingFlowViewModelActions() = runBlocking {
        val repository = BookingRepositoryImpl()
        val viewModel = BookingFlowViewModel(bookingRepository = repository, coroutineScope = this)

        val readyState = viewModel.uiState.filterIsInstance<CabDetailUiState.Success>().first()
        assertEquals("Toyota Innova Crysta", readyState.detail.vehicle.name)

        // Select different vehicle
        val sedan = VehicleSeedData.VEHICLE_OPTIONS.first { it.id == "veh_swift_dzire" }
        viewModel.handleAction(CabDetailAction.SelectVehicle(sedan))

        val updatedState = viewModel.uiState.filterIsInstance<CabDetailUiState.Success>()
            .first { it.detail.vehicle.id == "veh_swift_dzire" }
        assertEquals("Swift Dzire", updatedState.detail.vehicle.name)

        // Clear message
        viewModel.handleAction(CabDetailAction.ClearMessage)
        val clearMsgState = viewModel.uiState.value as CabDetailUiState.Success
        assertEquals(null, clearMsgState.userMessage)
    }

    @Test
    fun testAvailableCouponsAndApplication() = runBlocking {
        val repository = BookingRepositoryImpl()
        val coupons = repository.getAvailableCoupons().first()
        assertEquals(4, coupons.size)
        assertTrue(coupons.any { it.code == "AIRPORT200" })
        assertTrue(coupons.any { it.code == "FIRST50" })

        // Apply AIRPORT200
        val applyResult = repository.applyCoupon("AIRPORT200").first()
        assertTrue(applyResult is NetworkResult.Success)
        assertEquals(200, applyResult.data.discountAmount)

        val updatedCoupons = repository.getAvailableCoupons().first()
        val airportCoupon = updatedCoupons.first { it.code == "AIRPORT200" }
        assertTrue(airportCoupon.isApplied)
    }

    @Test
    fun testPaymentMethodSelectionAndAdvancePayment() = runBlocking {
        val repository = BookingRepositoryImpl()
        repository.selectPaymentMethod(PaymentMethodType.PHONE_PE)
        assertEquals(PaymentMethodType.PHONE_PE, repository.bookingSession.value.selectedPaymentMethod)

        // Process advance payment
        val paymentResult = repository.processAdvancePayment().first()
        assertTrue(paymentResult is NetworkResult.Success)
        val confirmed = paymentResult.data
        assertEquals("CONFIRMED", repository.bookingSession.value.paymentStatus)
        assertEquals("Sohan Singh", confirmed.driverName)
        assertEquals("DL 1Y A 4872", confirmed.vehicleNumber)
        assertEquals(4.9f, confirmed.driverRating)
        assertTrue(confirmed.isConfirmed)
    }

    @Test
    fun testViewModelDownstreamCheckoutFlow() = runBlocking {
        val repository = BookingRepositoryImpl()
        val viewModel = BookingFlowViewModel(bookingRepository = repository, coroutineScope = this)

        // Initial payment state
        assertEquals(PaymentUiState.Idle, viewModel.paymentUiState.value)

        // Select payment method
        viewModel.handleAction(CabDetailAction.SelectPaymentMethod(PaymentMethodType.GOOGLE_PAY))
        assertEquals(PaymentMethodType.GOOGLE_PAY, viewModel.bookingSession.value.selectedPaymentMethod)

        // Confirm payment
        viewModel.handleAction(CabDetailAction.ConfirmPayment)
        val successState = viewModel.paymentUiState.filterIsInstance<PaymentUiState.Success>().first()
        assertEquals("Sohan Singh", successState.details.driverName)
        assertEquals("Toyota Innova Crysta", successState.details.vehicleName)
        assertEquals("CONFIRMED", viewModel.bookingSession.value.paymentStatus)
    }
}
