package com.example.goindiacab.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Technical vehicle specifications for Review Cab Details screen.
 */
@Immutable
@Serializable
data class VehicleSpecs(
    val fuelType: String,
    val transmission: String,
    val luggageCapacity: String,
    val acType: String,
    val seatingCapacity: Int
)

/**
 * Trip inclusion item (e.g. 250 km limit included, State tax & tolls included).
 */
@Immutable
@Serializable
data class InclusionItem(
    val id: String,
    val text: String
)

/**
 * Trip exclusion item (e.g. Extra km charge, night allowance, parking fees).
 */
@Immutable
@Serializable
data class ExclusionItem(
    val id: String,
    val text: String
)

/**
 * Hourly package rental charges for outstation/local standby.
 */
@Immutable
@Serializable
data class HourlyCharge(
    val durationText: String,
    val fareAmount: Int
)

/**
 * Verified fleet passenger review.
 */
@Immutable
@Serializable
data class DriverReview(
    val reviewerName: String,
    val timeAgoText: String,
    val rating: Float,
    val comment: String,
    val avatarDrawableKey: String = "img_driver_portrait"
)

/**
 * Itemized fare breakdown with tax, discounts, and deposit calculations.
 */
@Immutable
@Serializable
data class ItemizedFareBreakdown(
    val baseFare: Int,
    val baseKmLimit: Int = 250,
    val tollAndStateTaxIncluded: Boolean = true,
    val tollAndStateTaxAmount: Int = 0,
    val gstAndBookingFees: Int = 700,
    val discountAmount: Int = 0,
    val couponCode: String? = null
) {
    val totalEstimatedFare: Int
        get() = (baseFare + (if (tollAndStateTaxIncluded) 0 else tollAndStateTaxAmount) + gstAndBookingFees - discountAmount)
            .coerceAtLeast(0)

    val advanceDepositPercent: Int = 10

    val advanceDepositAmount: Int
        get() = (totalEstimatedFare * advanceDepositPercent) / 100

    val remainingPayableAtPickup: Int
        get() = totalEstimatedFare - advanceDepositAmount
}

/**
 * Comprehensive Cab Detail Domain Model for Screen 27 (Review Cab Details).
 */
@Immutable
@Serializable
data class CabDetailData(
    val vehicle: VehiclePartnerOption,
    val specs: VehicleSpecs,
    val inclusions: List<InclusionItem>,
    val exclusions: List<ExclusionItem>,
    val hourlyCharges: List<HourlyCharge>,
    val cancellationPolicy: String,
    val review: DriverReview,
    val fareBreakdown: ItemizedFareBreakdown,
    val paymentTermsText: String
)

/**
 * Central Booking Session State shared across downstream booking flow.
 * Retains selection from Screen 25 -> Screen 27 -> Screen 28 -> Screen 29 -> Screen 30 -> Screen 32 -> Screen 31.
 */
@Immutable
@Serializable
data class BookingSession(
    val bookingId: String = "GIC-849201",
    val selectedVehicle: VehiclePartnerOption? = null,
    val pickupLocation: String = "",
    val stopLocations: List<String> = emptyList(),
    val dropLocation: String = "",
    val routeDistanceKm: Int = 580,
    val tripDurationDays: Int = 3,
    val travelDate: String = "Wed, 12 Sep 2026",
    val travelTime: String = "09:15 AM",
    val fareBreakdown: ItemizedFareBreakdown = ItemizedFareBreakdown(baseFare = 18500),
    val appliedCoupon: CouponItem? = null,
    val contactName: String = "Dhruva",
    val contactPhone: String = "+91 98765 43210",
    val selectedPaymentMethod: PaymentMethodType = PaymentMethodType.GOOGLE_PAY,
    val paymentStatus: String = "PENDING"
)

/**
 * Coupon discount item for Screen 29 (Apply Coupon).
 */
@Immutable
@Serializable
data class CouponItem(
    val code: String,
    val title: String,
    val subtitle: String,
    val discountAmount: Int,
    val isApplied: Boolean = false
)

/**
 * Milestone payment breakdown item for Screen 30 and Screen 32.
 */
@Immutable
@Serializable
data class PaymentMilestone(
    val title: String,
    val percentage: Int,
    val amount: Int
)

/**
 * Supported payment method types for Screen 32.
 */
enum class PaymentMethodType {
    GOOGLE_PAY,
    PHONE_PE,
    PAYTM,
    WALLET
}

/**
 * Payment method selectable item.
 */
@Immutable
@Serializable
data class PaymentMethodOption(
    val type: PaymentMethodType,
    val name: String,
    val isSelected: Boolean = false
)

/**
 * Confirmed booking domain model for Screen 31 (Review Booking / Confirmed Status).
 */
@Immutable
@Serializable
data class ConfirmedBookingDetails(
    val bookingId: String,
    val vehicleName: String,
    val vehicleCategoryName: String,
    val vehicleNumber: String,
    val driverName: String,
    val driverRating: Float,
    val driverRatingText: String = "Excellent driver",
    val driverPhone: String = "+91 98765 12345",
    val pickupLocation: String,
    val dropLocation: String,
    val travelDate: String,
    val travelTime: String,
    val distanceText: String,
    val tripTypeBadge: String = "One-Way",
    val baseFare: Int,
    val taxes: Int,
    val toll: Int,
    val couponDiscount: Int,
    val totalAmount: Int,
    val advancePaidAmount: Int,
    val remainingPayableAmount: Int,
    val appliedCouponCode: String?,
    val isConfirmed: Boolean = true
)

/**
 * Seed data for available promotional coupons.
 */
object CheckoutSeedData {
    val AVAILABLE_COUPONS = listOf(
        CouponItem(
            code = "FIRST50",
            title = "50% off on your first ride booked with GoIndiaCab",
            subtitle = "Max discount ₹200 • Valid for new users only | Expires 31 Dec 2026",
            discountAmount = 200
        ),
        CouponItem(
            code = "AIRPORT200",
            title = "Flat ₹200 off on flat rate airport drop-offs",
            subtitle = "Valid on Sedan & SUV types | Expires 15 Nov 2026",
            discountAmount = 200
        ),
        CouponItem(
            code = "WEEKEND30",
            title = "Get 30% off on premium outstation travel",
            subtitle = "Max discount ₹300 • Weekend bookings only | Expires 30 Nov 2026",
            discountAmount = 300
        ),
        CouponItem(
            code = "REFER100",
            title = "Referral reward discount applied instantly",
            subtitle = "No minimum booking requirement | No expiration date",
            discountAmount = 100
        )
    )

    val PAYMENT_METHODS = listOf(
        PaymentMethodOption(PaymentMethodType.GOOGLE_PAY, "Google Pay", isSelected = true),
        PaymentMethodOption(PaymentMethodType.PHONE_PE, "PhonePe", isSelected = false),
        PaymentMethodOption(PaymentMethodType.PAYTM, "Paytm", isSelected = false)
    )
}

/**
 * Driver partner assigned to passenger for ride execution.
 */
@Immutable
@Serializable
data class DriverPartnerInfo(
    val id: String = "drv_92145",
    val name: String = "Rajesh Kumar",
    val rating: Float = 4.9f,
    val totalTrips: Int = 2500,
    val phone: String = "+91 98765 43210",
    val vehicleModel: String = "Toyota Innova Crysta",
    val vehicleNumber: String = "DL 01 AB 1234",
    val vehicleColor: String = "White",
    val vehicleRating: Float = 4.7f,
    val vehicleSpecsText: String = "Diesel • 6 Seater",
    val speaksLanguages: String = "Hindi, English",
    val isVerified: Boolean = true,
    val departureScheduledText: String = "Tomorrow at 09:15 AM",
    val nextPaymentDueText: String = "Next Payment: ₹8,000 (40%) due at trip start.",
    val startOtp: String = "4829",
    val etaMinutes: Int = 4,
    val photoUrl: String? = null
)

/**
 * Reasons for payment processing failure.
 */
enum class PaymentFailureReason(val title: String, val description: String) {
    BANK_TIMEOUT(
        "Bank Server Timed Out",
        "Your bank server took too long to respond. No money was deducted from your account."
    ),
    INSUFFICIENT_FUNDS(
        "Insufficient Funds",
        "Your account did not have sufficient balance for this advance deposit transaction."
    ),
    DECLINED_BY_BANK(
        "Card / UPI Declined",
        "Transaction was declined by issuing bank. Please retry or choose another payment method."
    ),
    USER_CANCELLED(
        "Payment Cancelled",
        "Payment authentication was cancelled by user. You can retry with a different method."
    )
}

/**
 * Production-ready Booking Ticket model for Screen 35 (Booking ID Confirmation).
 */
@Immutable
@Serializable
data class BookingTicket(
    val bookingId: String = "GIC-2026-09145",
    val confirmationTime: String = "Today, 06:45 PM",
    val origin: String = "Delhi Airport Terminal 3",
    val destination: String = "Agra Cantt, Agra",
    val stops: List<String> = emptyList(),
    val travelDate: String = "20 Sep 2026",
    val travelTime: String = "06:00 PM",
    val tripType: String = "One-Way Outstation",
    val distanceKm: Int = 230,
    val vehicleCategory: String = "Sedan",
    val vehicleModel: String = "Maruti Dzire or equivalent",
    val totalFare: Int = 2499,
    val advancePaid: Int = 250,
    val balanceRemaining: Int = 2249,
    val driver: DriverPartnerInfo = DriverPartnerInfo(),
    val paymentMethod: PaymentMethodType = PaymentMethodType.GOOGLE_PAY,
    val paymentTxnId: String = "TXN-9842109823",
    val isConfirmed: Boolean = true
)

/**
 * Refund details model for Screen: Refund Status (refund-initiated.svg).
 */
@Immutable
@Serializable
data class RefundInvoice(
    val amountPaid: Int = 1800,
    val refundAmount: Int = 1800,
    val timelineText: String = "Within 12 hours",
    val refundingToMethod: String = "Google Pay",
    val statusText: String = "Refund Initiated",
    val statusSubtext: String = "Processing",
    val progressPercent: Float = 0.4f,
    val failureReasonTitle: String = "No Partner Available",
    val failureReasonDescription: String = "We could not find an available partner for your trip."
)

/**
 * Status of individual payment schedule milestones.
 */
enum class MilestoneStatus {
    PAID,
    DUE,
    UPCOMING
}

/**
 * Single payment milestone item for payment schedule stepper.
 */
@Immutable
@Serializable
data class PaymentScheduleMilestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Int,
    val percentage: Int,
    val status: MilestoneStatus
)

/**
 * Domain model for Screen: Payment Schedule (trip-payment-schedule.svg).
 */
@Immutable
@Serializable
data class TripPaymentSchedule(
    val routeSummary: String = "Delhi → Jaipur Multi-Stop",
    val durationAndDistance: String = "3 Days • 580 km",
    val totalAmount: Int = 20000,
    val milestones: List<PaymentScheduleMilestone> = listOf(
        PaymentScheduleMilestone(
            id = "m1",
            title = "Booking Advance (10%)",
            subtitle = "₹2,000 • Paid via UPI",
            amount = 2000,
            percentage = 10,
            status = MilestoneStatus.PAID
        ),
        PaymentScheduleMilestone(
            id = "m2",
            title = "Trip Start (Day 1) (40%)",
            subtitle = "₹8,000 • Due Tomorrow",
            amount = 8000,
            percentage = 40,
            status = MilestoneStatus.DUE
        ),
        PaymentScheduleMilestone(
            id = "m3",
            title = "Mid Trip (Day 5) (30%)",
            subtitle = "₹6,000 • Scheduled",
            amount = 6000,
            percentage = 30,
            status = MilestoneStatus.UPCOMING
        ),
        PaymentScheduleMilestone(
            id = "m4",
            title = "Trip End (20%)",
            subtitle = "₹4,000 • Scheduled",
            amount = 4000,
            percentage = 20,
            status = MilestoneStatus.UPCOMING
        )
    ),
    val securityNote: String = "Partner will verify each payment via secure OTP."
)

/**
 * Domain model for Screen: Verify Payment OTP (payment-otp-verification.svg).
 */
@Immutable
@Serializable
data class PaymentOtpData(
    val amount: Int = 8000,
    val stageLabel: String = "40% – Trip Start Payment",
    val otpCode: String = "7429",
    val validityDurationSeconds: Int = 600,
    val driverName: String = "Rajesh Kumar",
    val vehicleDetails: String = "Toyota Innova Crysta • DL 01 AB 1234",
    val progressText: String = "Progress: 2 of 4 payments verified"
)


