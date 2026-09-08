package com.example.goindiacab.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goindiacab.data.models.*
import com.example.goindiacab.data.repository.BookingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Screen 27 (Review Cab Details) and connected booking flow.
 */
sealed interface CabDetailUiState {
    data object Loading : CabDetailUiState

    @Immutable
    data class Success(
        val detail: CabDetailData,
        val isApplyingCoupon: Boolean = false,
        val userMessage: String? = null
    ) : CabDetailUiState

    data class Error(val message: String) : CabDetailUiState
}

/**
 * UI State for Screen 32 (Payment) and Screen 31 (Booking Confirmation).
 */
sealed interface PaymentUiState {
    data object Idle : PaymentUiState
    data object Processing : PaymentUiState

    @Immutable
    data class Success(val details: ConfirmedBookingDetails) : PaymentUiState

    data class Error(val message: String) : PaymentUiState
}

/**
 * UI State for Partner Searching Screen.
 */
sealed interface PartnerSearchUiState {
    data object Idle : PartnerSearchUiState
    data class Searching(val progressSeconds: Int = 45, val isFindingNearby: Boolean = true) : PartnerSearchUiState
    data class Found(val driver: DriverPartnerInfo) : PartnerSearchUiState
    data object Cancelled : PartnerSearchUiState
}

/**
 * User actions / intents for Cab Details & Booking flow.
 */
sealed interface CabDetailAction {
    data class LoadDetails(val vehicleOption: VehiclePartnerOption? = null) : CabDetailAction
    data class SelectVehicle(val vehicle: VehiclePartnerOption) : CabDetailAction
    data class ApplyCoupon(val couponCode: String) : CabDetailAction
    data class SelectCouponItem(val coupon: CouponItem) : CabDetailAction
    data object RemoveCoupon : CabDetailAction
    data class SelectPaymentMethod(val methodType: PaymentMethodType) : CabDetailAction
    data object ConfirmPayment : CabDetailAction
    data object ResetPaymentState : CabDetailAction
    data object ClearMessage : CabDetailAction
    data object Refresh : CabDetailAction
    data class SimulateFailure(val simulate: Boolean, val reason: PaymentFailureReason) : CabDetailAction
    data object StartPartnerSearch : CabDetailAction
    data object CancelPartnerSearch : CabDetailAction
    data object GenerateTicket : CabDetailAction
    data class SimulateNoPartner(val simulate: Boolean) : CabDetailAction
    data class PayMilestone(val milestoneId: String) : CabDetailAction
    data class VerifyOtp(val otp: String) : CabDetailAction
    data object LoadPaymentSchedule : CabDetailAction
    data object LoadRefundInvoice : CabDetailAction
}

/**
 * Production MVVM ViewModel coordinating Booking Session & Cab Details (Screen 27)
 * through downstream checkout (Screens 28, 29, 30, 32, 31).
 */
class BookingFlowViewModel(
    private val bookingRepository: BookingRepository,
    coroutineScope: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = coroutineScope ?: viewModelScope
    private val _uiState = MutableStateFlow<CabDetailUiState>(CabDetailUiState.Loading)
    val uiState: StateFlow<CabDetailUiState> = _uiState.asStateFlow()

    private val _availableCoupons = MutableStateFlow<List<CouponItem>>(CheckoutSeedData.AVAILABLE_COUPONS)
    val availableCoupons: StateFlow<List<CouponItem>> = _availableCoupons.asStateFlow()

    private val _paymentUiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentUiState: StateFlow<PaymentUiState> = _paymentUiState.asStateFlow()

    private val _confirmedBooking = MutableStateFlow<ConfirmedBookingDetails?>(null)
    val confirmedBooking: StateFlow<ConfirmedBookingDetails?> = _confirmedBooking.asStateFlow()

    private val _partnerSearchState = MutableStateFlow<PartnerSearchUiState>(PartnerSearchUiState.Idle)
    val partnerSearchState: StateFlow<PartnerSearchUiState> = _partnerSearchState.asStateFlow()

    private val _latestBookingTicket = MutableStateFlow<BookingTicket?>(null)
    val latestBookingTicket: StateFlow<BookingTicket?> = _latestBookingTicket.asStateFlow()

    private val _paymentFailureReason = MutableStateFlow(PaymentFailureReason.BANK_TIMEOUT)
    val paymentFailureReason: StateFlow<PaymentFailureReason> = _paymentFailureReason.asStateFlow()

    private val _paymentSchedule = MutableStateFlow(TripPaymentSchedule())
    val paymentSchedule: StateFlow<TripPaymentSchedule> = _paymentSchedule.asStateFlow()

    private val _refundInvoice = MutableStateFlow(RefundInvoice())
    val refundInvoice: StateFlow<RefundInvoice> = _refundInvoice.asStateFlow()

    private val _paymentOtpData = MutableStateFlow(PaymentOtpData())
    val paymentOtpData: StateFlow<PaymentOtpData> = _paymentOtpData.asStateFlow()

    private val _isSimulateNoPartner = MutableStateFlow(false)
    val isSimulateNoPartner: StateFlow<Boolean> = _isSimulateNoPartner.asStateFlow()

    val bookingSession: StateFlow<BookingSession> = bookingRepository.bookingSession

    init {
        loadCabDetails(bookingSession.value.selectedVehicle)
        loadCoupons()
        loadPaymentSchedule()
        loadRefundInvoice()
    }

    fun handleAction(action: CabDetailAction) {
        when (action) {
            is CabDetailAction.LoadDetails -> loadCabDetails(action.vehicleOption)
            is CabDetailAction.SelectVehicle -> {
                bookingRepository.updateSelectedVehicle(action.vehicle)
                loadCabDetails(action.vehicle)
            }
            is CabDetailAction.ApplyCoupon -> applyCoupon(action.couponCode)
            is CabDetailAction.SelectCouponItem -> applyCoupon(action.coupon.code)
            is CabDetailAction.RemoveCoupon -> removeCoupon()
            is CabDetailAction.SelectPaymentMethod -> selectPaymentMethod(action.methodType)
            is CabDetailAction.ConfirmPayment -> confirmPayment()
            is CabDetailAction.ResetPaymentState -> {
                _paymentUiState.value = PaymentUiState.Idle
            }
            is CabDetailAction.ClearMessage -> {
                _uiState.update { current ->
                    if (current is CabDetailUiState.Success) {
                        current.copy(userMessage = null)
                    } else current
                }
            }
            is CabDetailAction.Refresh -> loadCabDetails(bookingSession.value.selectedVehicle)
            is CabDetailAction.SimulateFailure -> setSimulatePaymentFailure(action.simulate, action.reason)
            is CabDetailAction.StartPartnerSearch -> startPartnerSearch()
            is CabDetailAction.CancelPartnerSearch -> cancelPartnerSearch()
            is CabDetailAction.GenerateTicket -> generateBookingTicket()
            is CabDetailAction.SimulateNoPartner -> setSimulateNoPartner(action.simulate)
            is CabDetailAction.PayMilestone -> payMilestone(action.milestoneId)
            is CabDetailAction.VerifyOtp -> verifyOtp(action.otp)
            is CabDetailAction.LoadPaymentSchedule -> loadPaymentSchedule()
            is CabDetailAction.LoadRefundInvoice -> loadRefundInvoice()
        }
    }

    fun updateRouteDetails(
        pickup: String,
        drop: String,
        stops: List<String> = emptyList(),
        distanceKm: Int? = null,
        fare: Int? = null
    ) {
        bookingRepository.updateRouteDetails(pickup, drop, stops, distanceKm, fare)
        loadCabDetails(bookingSession.value.selectedVehicle)
    }

    fun updateScheduleDetails(travelDate: String, travelTime: String) {
        bookingRepository.updateScheduleDetails(travelDate, travelTime)
    }

    private fun loadCabDetails(vehicle: VehiclePartnerOption?) {
        scope.launch {
            _uiState.value = CabDetailUiState.Loading
            bookingRepository.getCabDetailData(vehicle).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.value = CabDetailUiState.Success(detail = result.data)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = CabDetailUiState.Error(result.message)
                    }
                    is NetworkResult.Exception -> {
                        _uiState.value = CabDetailUiState.Error(result.throwable.message ?: "An unexpected error occurred")
                    }
                }
            }
        }
    }

    private fun loadCoupons() {
        scope.launch {
            bookingRepository.getAvailableCoupons().collect { coupons ->
                _availableCoupons.value = coupons
            }
        }
    }

    private fun applyCoupon(code: String) {
        scope.launch {
            val currentState = _uiState.value as? CabDetailUiState.Success
            if (currentState != null) {
                _uiState.value = currentState.copy(isApplyingCoupon = true)
            }

            bookingRepository.applyCoupon(code).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val updatedFare = result.data
                        if (currentState != null) {
                            val updatedDetail = currentState.detail.copy(fareBreakdown = updatedFare)
                            _uiState.value = currentState.copy(
                                detail = updatedDetail,
                                isApplyingCoupon = false,
                                userMessage = "Coupon ${code.uppercase()} applied successfully! Saved ₹${updatedFare.discountAmount}"
                            )
                        }
                        loadCoupons()
                    }
                    is NetworkResult.Error -> {
                        if (currentState != null) {
                            _uiState.value = currentState.copy(
                                isApplyingCoupon = false,
                                userMessage = result.message
                            )
                        }
                    }
                    is NetworkResult.Exception -> {
                        if (currentState != null) {
                            _uiState.value = currentState.copy(
                                isApplyingCoupon = false,
                                userMessage = result.throwable.message ?: "Failed to apply coupon"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun removeCoupon() {
        val currentState = _uiState.value as? CabDetailUiState.Success
        bookingRepository.removeCoupon()
        if (currentState != null) {
            val currentFare = currentState.detail.fareBreakdown.copy(
                discountAmount = 0,
                couponCode = null
            )
            val updatedDetail = currentState.detail.copy(fareBreakdown = currentFare)
            _uiState.value = currentState.copy(
                detail = updatedDetail,
                userMessage = "Coupon removed"
            )
        }
        loadCoupons()
    }

    private fun selectPaymentMethod(type: PaymentMethodType) {
        bookingRepository.selectPaymentMethod(type)
    }

    private fun confirmPayment() {
        scope.launch {
            _paymentUiState.value = PaymentUiState.Processing
            bookingRepository.processAdvancePayment().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _confirmedBooking.value = result.data
                        _paymentUiState.value = PaymentUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _paymentUiState.value = PaymentUiState.Error(result.message)
                    }
                    is NetworkResult.Exception -> {
                        _paymentUiState.value = PaymentUiState.Error(result.throwable.message ?: "Payment processing failed")
                    }
                }
            }
        }
    }

    fun startPartnerSearch() {
        scope.launch {
            _partnerSearchState.value = PartnerSearchUiState.Searching(45, true)
            bookingRepository.searchDriverPartner().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _partnerSearchState.value = PartnerSearchUiState.Found(result.data)
                        generateBookingTicket(result.data)
                    }
                    is NetworkResult.Error -> {
                        _partnerSearchState.value = PartnerSearchUiState.Idle
                    }
                    is NetworkResult.Exception -> {
                        _partnerSearchState.value = PartnerSearchUiState.Idle
                    }
                }
            }
        }
    }

    fun cancelPartnerSearch() {
        scope.launch {
            bookingRepository.cancelPartnerSearch().collect {
                _partnerSearchState.value = PartnerSearchUiState.Cancelled
            }
        }
    }

    fun generateBookingTicket(driver: DriverPartnerInfo? = null) {
        scope.launch {
            bookingRepository.getBookingTicket().collect { result ->
                if (result is NetworkResult.Success) {
                    val ticket = if (driver != null) result.data.copy(driver = driver) else result.data
                    _latestBookingTicket.value = ticket
                }
            }
        }
    }

    fun setSimulatePaymentFailure(simulate: Boolean, reason: PaymentFailureReason = PaymentFailureReason.BANK_TIMEOUT) {
        _paymentFailureReason.value = reason
        bookingRepository.setSimulatePaymentFailure(simulate, reason)
    }

    fun processPayment() {
        confirmPayment()
    }

    fun retryPayment() {
        _paymentUiState.value = PaymentUiState.Idle
        confirmPayment()
    }

    fun loadPaymentSchedule() {
        scope.launch {
            bookingRepository.getTripPaymentSchedule().collect { result ->
                if (result is NetworkResult.Success) {
                    _paymentSchedule.value = result.data
                }
            }
        }
    }

    fun loadRefundInvoice() {
        scope.launch {
            bookingRepository.getRefundInvoice().collect { result ->
                if (result is NetworkResult.Success) {
                    _refundInvoice.value = result.data
                }
            }
        }
    }

    fun setSimulateNoPartner(simulate: Boolean) {
        _isSimulateNoPartner.value = simulate
        bookingRepository.setSimulateNoPartner(simulate)
    }

    fun payMilestone(milestoneId: String) {
        scope.launch {
            bookingRepository.markMilestonePaid(milestoneId).collect { result ->
                if (result is NetworkResult.Success) {
                    _paymentSchedule.value = result.data
                }
            }
        }
    }

    fun verifyOtp(otp: String, onResult: (Boolean) -> Unit = {}) {
        scope.launch {
            bookingRepository.verifyPaymentOtp(otp).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        loadPaymentSchedule()
                        onResult(true)
                    }
                    else -> onResult(false)
                }
            }
        }
    }

    fun resendOtp(onSent: () -> Unit = {}) {
        scope.launch {
            bookingRepository.resendPaymentOtp().collect {
                onSent()
            }
        }
    }
}
