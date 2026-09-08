package com.example.goindiacab.data.repository

import com.example.goindiacab.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Repository interface for Booking Session management and Cab Detail querying.
 */
interface BookingRepository {
    val bookingSession: StateFlow<BookingSession>

    fun updateRouteDetails(
        pickup: String,
        drop: String,
        stops: List<String> = emptyList(),
        distanceKm: Int? = null,
        fare: Int? = null
    )
    fun updateScheduleDetails(travelDate: String, travelTime: String)
    fun updateSelectedVehicle(vehicle: VehiclePartnerOption)
    fun getCabDetailData(vehicleOption: VehiclePartnerOption?): Flow<NetworkResult<CabDetailData>>
    fun getAvailableCoupons(): Flow<List<CouponItem>>
    fun applyCoupon(code: String): Flow<NetworkResult<ItemizedFareBreakdown>>
    fun removeCoupon()
    fun selectPaymentMethod(type: PaymentMethodType)
    fun processAdvancePayment(): Flow<NetworkResult<ConfirmedBookingDetails>>
    fun getConfirmedBooking(): Flow<NetworkResult<ConfirmedBookingDetails>>
    fun setSimulatePaymentFailure(simulate: Boolean, reason: PaymentFailureReason = PaymentFailureReason.BANK_TIMEOUT)
    fun searchDriverPartner(): Flow<NetworkResult<DriverPartnerInfo>>
    fun cancelPartnerSearch(): Flow<NetworkResult<Boolean>>
    fun getBookingTicket(): Flow<NetworkResult<BookingTicket>>
    fun getRefundInvoice(): Flow<NetworkResult<RefundInvoice>>
    fun getTripPaymentSchedule(): Flow<NetworkResult<TripPaymentSchedule>>
    fun verifyPaymentOtp(otp: String): Flow<NetworkResult<Boolean>>
    fun resendPaymentOtp(): Flow<NetworkResult<Boolean>>
    fun setSimulateNoPartner(simulate: Boolean)
    fun markMilestonePaid(milestoneId: String): Flow<NetworkResult<TripPaymentSchedule>>
}

/**
 * Production implementation of BookingRepository.
 */
class BookingRepositoryImpl : BookingRepository {

    private val _bookingSession = MutableStateFlow(
        BookingSession(
            selectedVehicle = VehicleSeedData.VEHICLE_OPTIONS.find { it.id == "veh_innova_crysta" }
                ?: VehicleSeedData.VEHICLE_OPTIONS.first(),
            fareBreakdown = ItemizedFareBreakdown(
                baseFare = 4500,
                baseKmLimit = 250,
                tollAndStateTaxIncluded = true,
                gstAndBookingFees = 700
            )
        )
    )
    override val bookingSession: StateFlow<BookingSession> = _bookingSession.asStateFlow()

    private val _availableCoupons = MutableStateFlow(CheckoutSeedData.AVAILABLE_COUPONS)

    override fun updateRouteDetails(
        pickup: String,
        drop: String,
        stops: List<String>,
        distanceKm: Int?,
        fare: Int?
    ) {
        val current = _bookingSession.value
        val newDistance = distanceKm ?: current.routeDistanceKm
        val newFareBreakdown = if (fare != null) {
            val baseFare = (fare - current.fareBreakdown.gstAndBookingFees).coerceAtLeast(1000)
            current.fareBreakdown.copy(baseFare = baseFare)
        } else current.fareBreakdown

        _bookingSession.value = current.copy(
            pickupLocation = pickup,
            dropLocation = drop,
            stopLocations = if (stops.isNotEmpty()) stops else current.stopLocations,
            routeDistanceKm = newDistance,
            fareBreakdown = newFareBreakdown
        )
    }

    override fun updateScheduleDetails(travelDate: String, travelTime: String) {
        _bookingSession.value = _bookingSession.value.copy(
            travelDate = travelDate,
            travelTime = travelTime
        )
    }

    override fun updateSelectedVehicle(vehicle: VehiclePartnerOption) {
        val baseFare = (vehicle.allInclusiveFare - 700).coerceAtLeast(1000)
        val updatedFare = _bookingSession.value.fareBreakdown.copy(
            baseFare = baseFare,
            baseKmLimit = vehicle.freeKmLimit
        )
        _bookingSession.value = _bookingSession.value.copy(
            selectedVehicle = vehicle,
            fareBreakdown = updatedFare
        )
    }

    override fun getCabDetailData(vehicleOption: VehiclePartnerOption?): Flow<NetworkResult<CabDetailData>> = flow {
        val vehicle = vehicleOption ?: _bookingSession.value.selectedVehicle
            ?: VehicleSeedData.VEHICLE_OPTIONS.find { it.id == "veh_innova_crysta" }
            ?: VehicleSeedData.VEHICLE_OPTIONS.first()

        val specs = when (vehicle.category) {
            VehicleCategory.SUV, VehicleCategory.PREMIUM -> VehicleSpecs(
                fuelType = "Diesel",
                transmission = "Manual",
                luggageCapacity = "3 large bags",
                acType = "Rear AC",
                seatingCapacity = vehicle.seatingCapacity
            )
            VehicleCategory.GROUP_TRAVEL -> VehicleSpecs(
                fuelType = "Diesel",
                transmission = "Manual",
                luggageCapacity = "6+ large bags",
                acType = "Roof AC Vents",
                seatingCapacity = vehicle.seatingCapacity
            )
            else -> VehicleSpecs(
                fuelType = "Petrol/CNG",
                transmission = "Manual",
                luggageCapacity = "2 bags",
                acType = "Standard AC",
                seatingCapacity = vehicle.seatingCapacity
            )
        }

        val inclusions = listOf(
            InclusionItem("inc_1", "${vehicle.freeKmLimit} km limit included"),
            InclusionItem("inc_2", "Fuel charges & Driver allowance"),
            InclusionItem("inc_3", "State tax & Toll taxes included"),
            InclusionItem("inc_4", "GST & Booking fees")
        )

        val exclusions = listOf(
            ExclusionItem("exc_1", "₹${vehicle.extraKmRate}/km after ${vehicle.freeKmLimit} km"),
            ExclusionItem("exc_2", "Night charges ₹${vehicle.nightCharges} (${vehicle.nightChargeWindow})"),
            ExclusionItem("exc_3", "Parking & entry fees"),
            ExclusionItem("exc_4", "Waiting charges ₹100/hr after 30 min free")
        )

        val hourlyCharges = listOf(
            HourlyCharge("1 hour", 499),
            HourlyCharge("2 hours", 899),
            HourlyCharge("4 hours", 1599),
            HourlyCharge("8 hours", 2999)
        )

        val review = DriverReview(
            reviewerName = "Rahul S.",
            timeAgoText = "2 days ago",
            rating = 5.0f,
            comment = "Clean vehicle, comfortable seating, and smooth ride. Recommended for long trips.",
            avatarDrawableKey = "img_driver_portrait"
        )

        val baseFare = (vehicle.allInclusiveFare - 700).coerceAtLeast(1000)
        val fareBreakdown = ItemizedFareBreakdown(
            baseFare = baseFare,
            baseKmLimit = vehicle.freeKmLimit,
            tollAndStateTaxIncluded = true,
            gstAndBookingFees = 700,
            discountAmount = _bookingSession.value.fareBreakdown.discountAmount,
            couponCode = _bookingSession.value.fareBreakdown.couponCode
        )

        val detail = CabDetailData(
            vehicle = vehicle,
            specs = specs,
            inclusions = inclusions,
            exclusions = exclusions,
            hourlyCharges = hourlyCharges,
            cancellationPolicy = "Free cancellation up to 6 hrs before departure.",
            review = review,
            fareBreakdown = fareBreakdown,
            paymentTermsText = "Partner assigned after 10% payment • 100% refund if no partner found"
        )

        emit(NetworkResult.Success(detail))
    }

    override fun getAvailableCoupons(): Flow<List<CouponItem>> = flow {
        val currentCode = _bookingSession.value.appliedCoupon?.code
        val updated = _availableCoupons.value.map { coupon ->
            coupon.copy(isApplied = (coupon.code.equals(currentCode, ignoreCase = true)))
        }
        emit(updated)
    }

    override fun applyCoupon(code: String): Flow<NetworkResult<ItemizedFareBreakdown>> = flow {
        val upperCode = code.trim().uppercase()
        val foundCoupon = _availableCoupons.value.find { it.code.equals(upperCode, ignoreCase = true) }
        val discount = when {
            foundCoupon != null -> foundCoupon.discountAmount
            upperCode == "GOINDIA" || upperCode == "WELCOME500" -> 500
            upperCode == "SUPER10" -> (_bookingSession.value.fareBreakdown.baseFare * 0.10).toInt()
            upperCode == "WEEKEND" -> 300
            upperCode == "AIRPORT200" -> 200
            upperCode == "FIRST50" -> 200
            upperCode == "REFER100" -> 100
            else -> 0
        }

        if (discount > 0) {
            val couponItem = foundCoupon ?: CouponItem(
                code = upperCode,
                title = "Discount of ₹$discount",
                subtitle = "Promotional offer applied",
                discountAmount = discount,
                isApplied = true
            )
            val updated = _bookingSession.value.fareBreakdown.copy(
                discountAmount = discount,
                couponCode = upperCode
            )
            _bookingSession.value = _bookingSession.value.copy(
                fareBreakdown = updated,
                appliedCoupon = couponItem.copy(isApplied = true)
            )
            emit(NetworkResult.Success(updated))
        } else {
            emit(NetworkResult.Error(code = 400, message = "Invalid or expired coupon code"))
        }
    }

    override fun removeCoupon() {
        val updated = _bookingSession.value.fareBreakdown.copy(
            discountAmount = 0,
            couponCode = null
        )
        _bookingSession.value = _bookingSession.value.copy(
            fareBreakdown = updated,
            appliedCoupon = null
        )
    }

    override fun selectPaymentMethod(type: PaymentMethodType) {
        _bookingSession.value = _bookingSession.value.copy(
            selectedPaymentMethod = type
        )
    }

    override fun processAdvancePayment(): Flow<NetworkResult<ConfirmedBookingDetails>> = flow {
        if (_shouldSimulateFailure) {
            kotlinx.coroutines.delay(1800)
            emit(NetworkResult.Error(code = 400, message = _failureReason.description))
            return@flow
        }
        val session = _bookingSession.value
        val vehicle = session.selectedVehicle
            ?: VehicleSeedData.VEHICLE_OPTIONS.find { it.id == "veh_innova_crysta" }
            ?: VehicleSeedData.VEHICLE_OPTIONS.first()

        val totalAmount = session.fareBreakdown.totalEstimatedFare
        val advanceAmount = session.fareBreakdown.advanceDepositAmount
        val remainingAmount = session.fareBreakdown.remainingPayableAtPickup

        val confirmedDetails = ConfirmedBookingDetails(
            bookingId = session.bookingId,
            vehicleName = vehicle.name,
            vehicleCategoryName = vehicle.category.displayName,
            vehicleNumber = "DL 1Y A 4872",
            driverName = "Sohan Singh",
            driverRating = 4.9f,
            driverRatingText = "Excellent driver",
            driverPhone = "+91 98123 45678",
            pickupLocation = session.pickupLocation,
            dropLocation = session.dropLocation,
            travelDate = session.travelDate,
            travelTime = session.travelTime,
            distanceText = "${session.routeDistanceKm} km",
            tripTypeBadge = "One-Way",
            baseFare = session.fareBreakdown.baseFare,
            taxes = session.fareBreakdown.gstAndBookingFees,
            toll = if (session.fareBreakdown.tollAndStateTaxIncluded) 0 else 50,
            couponDiscount = session.fareBreakdown.discountAmount,
            totalAmount = totalAmount,
            advancePaidAmount = advanceAmount,
            remainingPayableAmount = remainingAmount,
            appliedCouponCode = session.appliedCoupon?.code ?: session.fareBreakdown.couponCode,
            isConfirmed = true
        )

        _bookingSession.value = session.copy(
            paymentStatus = "CONFIRMED"
        )

        emit(NetworkResult.Success(confirmedDetails))
    }

    override fun getConfirmedBooking(): Flow<NetworkResult<ConfirmedBookingDetails>> = flow {
        val session = _bookingSession.value
        val vehicle = session.selectedVehicle
            ?: VehicleSeedData.VEHICLE_OPTIONS.find { it.id == "veh_innova_crysta" }
            ?: VehicleSeedData.VEHICLE_OPTIONS.first()

        val totalAmount = session.fareBreakdown.totalEstimatedFare
        val advanceAmount = session.fareBreakdown.advanceDepositAmount
        val remainingAmount = session.fareBreakdown.remainingPayableAtPickup

        val confirmedDetails = ConfirmedBookingDetails(
            bookingId = session.bookingId,
            vehicleName = vehicle.name,
            vehicleCategoryName = vehicle.category.displayName,
            vehicleNumber = "DL 1Y A 4872",
            driverName = "Sohan Singh",
            driverRating = 4.9f,
            driverRatingText = "Excellent driver",
            driverPhone = "+91 98123 45678",
            pickupLocation = session.pickupLocation,
            dropLocation = session.dropLocation,
            travelDate = session.travelDate,
            travelTime = session.travelTime,
            distanceText = "${session.routeDistanceKm} km",
            tripTypeBadge = "One-Way",
            baseFare = session.fareBreakdown.baseFare,
            taxes = session.fareBreakdown.gstAndBookingFees,
            toll = if (session.fareBreakdown.tollAndStateTaxIncluded) 0 else 50,
            couponDiscount = session.fareBreakdown.discountAmount,
            totalAmount = totalAmount,
            advancePaidAmount = advanceAmount,
            remainingPayableAmount = remainingAmount,
            appliedCouponCode = session.appliedCoupon?.code ?: session.fareBreakdown.couponCode,
            isConfirmed = true
        )

        emit(NetworkResult.Success(confirmedDetails))
    }

    private var _shouldSimulateFailure = false
    private var _failureReason: PaymentFailureReason = PaymentFailureReason.BANK_TIMEOUT
    private var _shouldSimulateNoPartner = false

    private val _paymentSchedule = MutableStateFlow(TripPaymentSchedule())

    override fun setSimulatePaymentFailure(simulate: Boolean, reason: PaymentFailureReason) {
        _shouldSimulateFailure = simulate
        _failureReason = reason
    }

    override fun setSimulateNoPartner(simulate: Boolean) {
        _shouldSimulateNoPartner = simulate
    }

    override fun searchDriverPartner(): Flow<NetworkResult<DriverPartnerInfo>> = flow {
        kotlinx.coroutines.delay(2200)
        if (_shouldSimulateNoPartner) {
            emit(NetworkResult.Error(code = 404, message = "No Partner Available"))
            return@flow
        }
        val vehicle = _bookingSession.value.selectedVehicle
        val driver = DriverPartnerInfo(
            id = "drv_92145",
            name = "Rajesh Kumar",
            rating = 4.9f,
            totalTrips = 2500,
            phone = "+91 98765 43210",
            vehicleModel = vehicle?.name ?: "Toyota Innova Crysta",
            vehicleNumber = "DL 01 AB 1234",
            vehicleColor = "White",
            vehicleRating = 4.7f,
            vehicleSpecsText = "Diesel • 6 Seater",
            speaksLanguages = "Hindi, English",
            isVerified = true,
            departureScheduledText = "Tomorrow at 09:15 AM",
            nextPaymentDueText = "Next Payment: ₹8,000 (40%) due at trip start.",
            startOtp = "4829",
            etaMinutes = 4
        )
        emit(NetworkResult.Success(driver))
    }

    override fun cancelPartnerSearch(): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Success(true))
    }

    override fun getBookingTicket(): Flow<NetworkResult<BookingTicket>> = flow {
        val session = _bookingSession.value
        val vehicle = session.selectedVehicle
            ?: VehicleSeedData.VEHICLE_OPTIONS.first()

        val totalAmount = session.fareBreakdown.totalEstimatedFare
        val advanceAmount = session.fareBreakdown.advanceDepositAmount
        val remainingAmount = session.fareBreakdown.remainingPayableAtPickup

        val ticket = BookingTicket(
            bookingId = session.bookingId,
            confirmationTime = "Today, 06:45 PM",
            origin = session.pickupLocation,
            destination = session.dropLocation,
            stops = session.stopLocations,
            travelDate = session.travelDate,
            travelTime = session.travelTime,
            tripType = "One-Way Outstation",
            distanceKm = session.routeDistanceKm,
            vehicleCategory = vehicle.category.displayName,
            vehicleModel = vehicle.name,
            totalFare = totalAmount,
            advancePaid = advanceAmount,
            balanceRemaining = remainingAmount,
            driver = DriverPartnerInfo(
                id = "drv_92145",
                name = "Rajesh Kumar",
                rating = 4.9f,
                totalTrips = 2500,
                phone = "+91 98765 43210",
                vehicleModel = vehicle.name,
                vehicleNumber = "DL 01 AB 1234",
                vehicleColor = "White",
                startOtp = "4829",
                etaMinutes = 4
            ),
            paymentMethod = session.selectedPaymentMethod,
            paymentTxnId = "TXN-9842109823",
            isConfirmed = true
        )

        emit(NetworkResult.Success(ticket))
    }

    override fun getRefundInvoice(): Flow<NetworkResult<RefundInvoice>> = flow {
        val session = _bookingSession.value
        val paidAmount = if (session.fareBreakdown.advanceDepositAmount > 0) {
            session.fareBreakdown.advanceDepositAmount
        } else {
            1800
        }
        val methodName = when (session.selectedPaymentMethod) {
            PaymentMethodType.GOOGLE_PAY -> "Google Pay"
            PaymentMethodType.PHONE_PE -> "PhonePe"
            PaymentMethodType.PAYTM -> "Paytm"
            PaymentMethodType.WALLET -> "GoIndia Wallet"
        }
        val refund = RefundInvoice(
            amountPaid = paidAmount,
            refundAmount = paidAmount,
            timelineText = "Within 12 hours",
            refundingToMethod = methodName,
            statusText = "Refund Initiated",
            statusSubtext = "Processing",
            progressPercent = 0.4f,
            failureReasonTitle = "No Partner Available",
            failureReasonDescription = "We could not find an available partner for your trip."
        )
        emit(NetworkResult.Success(refund))
    }

    override fun getTripPaymentSchedule(): Flow<NetworkResult<TripPaymentSchedule>> = flow {
        val session = _bookingSession.value
        val total = if (session.fareBreakdown.totalEstimatedFare > 0) session.fareBreakdown.totalEstimatedFare else 20000
        val advance = (total * 0.10).toInt().coerceAtLeast(1000)
        val tripStart = (total * 0.40).toInt()
        val midTrip = (total * 0.30).toInt()
        val tripEnd = total - advance - tripStart - midTrip

        val originCity = session.pickupLocation.substringBefore(",").substringBefore("(").trim().ifBlank { "Delhi" }
        val destCity = session.dropLocation.substringBefore(",").trim().ifBlank { "Jaipur" }

        val schedule = _paymentSchedule.value.copy(
            routeSummary = "$originCity → $destCity Multi-Stop",
            durationAndDistance = "${session.tripDurationDays} Days • ${session.routeDistanceKm} km",
            totalAmount = total,
            milestones = listOf(
                PaymentScheduleMilestone(
                    id = "m1",
                    title = "Booking Advance (10%)",
                    subtitle = "₹$advance • Paid via UPI",
                    amount = advance,
                    percentage = 10,
                    status = _paymentSchedule.value.milestones.find { it.id == "m1" }?.status ?: MilestoneStatus.PAID
                ),
                PaymentScheduleMilestone(
                    id = "m2",
                    title = "Trip Start (Day 1) (40%)",
                    subtitle = "₹$tripStart • Due Tomorrow",
                    amount = tripStart,
                    percentage = 40,
                    status = _paymentSchedule.value.milestones.find { it.id == "m2" }?.status ?: MilestoneStatus.DUE
                ),
                PaymentScheduleMilestone(
                    id = "m3",
                    title = "Mid Trip (Day 5) (30%)",
                    subtitle = "₹$midTrip • Scheduled",
                    amount = midTrip,
                    percentage = 30,
                    status = _paymentSchedule.value.milestones.find { it.id == "m3" }?.status ?: MilestoneStatus.UPCOMING
                ),
                PaymentScheduleMilestone(
                    id = "m4",
                    title = "Trip End (20%)",
                    subtitle = "₹$tripEnd • Scheduled",
                    amount = tripEnd,
                    percentage = 20,
                    status = _paymentSchedule.value.milestones.find { it.id == "m4" }?.status ?: MilestoneStatus.UPCOMING
                )
            )
        )
        _paymentSchedule.value = schedule
        emit(NetworkResult.Success(schedule))
    }

    override fun markMilestonePaid(milestoneId: String): Flow<NetworkResult<TripPaymentSchedule>> = flow {
        val current = _paymentSchedule.value
        val updatedMilestones = current.milestones.map { m ->
            if (m.id == milestoneId) m.copy(status = MilestoneStatus.PAID) else m
        }
        val updated = current.copy(milestones = updatedMilestones)
        _paymentSchedule.value = updated
        emit(NetworkResult.Success(updated))
    }

    override fun verifyPaymentOtp(otp: String): Flow<NetworkResult<Boolean>> = flow {
        kotlinx.coroutines.delay(1000)
        if (otp == "7429" || otp.length == 4) {
            val current = _paymentSchedule.value
            val updatedMilestones = current.milestones.map { m ->
                if (m.id == "m2") m.copy(status = MilestoneStatus.PAID) else m
            }
            _paymentSchedule.value = current.copy(milestones = updatedMilestones)
            emit(NetworkResult.Success(true))
        } else {
            emit(NetworkResult.Error(code = 400, message = "Invalid OTP. Please check the code."))
        }
    }

    override fun resendPaymentOtp(): Flow<NetworkResult<Boolean>> = flow {
        kotlinx.coroutines.delay(600)
        emit(NetworkResult.Success(true))
    }
}
