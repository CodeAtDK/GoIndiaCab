package com.example.goindiacab.domain

import com.example.goindiacab.data.models.MilestoneStatus
import com.example.goindiacab.data.models.PaymentOtpData
import com.example.goindiacab.data.models.PaymentScheduleMilestone
import com.example.goindiacab.data.models.TripPaymentSchedule
import kotlin.math.ceil
import kotlin.math.max

/**
 * Dynamic Payment Schedule Engine enforcing the core business rule:
 * "Every payment is in advance before each trip segment commences."
 *
 * Automatically and adaptively calculates payment milestones, percentages, amounts,
 * and schedule dates based on the trip's duration (number of days) and departure/return dates.
 */
object DynamicPaymentScheduleEngine {

    /**
     * Returns the booking advance percentage required at initial confirmation
     * based on trip duration:
     * - 1 day: 20%
     * - 2 days: 15%
     * - 3+ days: 10%
     */
    fun getAdvancePercentage(tripDurationDays: Int): Int {
        val days = max(1, tripDurationDays)
        return when (days) {
            1 -> 20
            2 -> 15
            else -> 10
        }
    }

    /**
     * Generates an adaptive, 100% advance milestone payment schedule.
     *
     * @param totalFare Total trip fare in INR.
     * @param tripDurationDays Duration of the trip in days (default 4 if unspecified).
     * @param pickupDate Start date of trip as string (e.g. "Tomorrow", "15 Oct 2024").
     * @param routeSummary Origin to destination summary string.
     * @param distanceKm Total route distance in kilometers.
     * @return Fully calculated [TripPaymentSchedule] where all milestones are advance payments.
     */
    fun generateSchedule(
        totalFare: Int,
        tripDurationDays: Int = 4,
        pickupDate: String = "Tomorrow",
        routeSummary: String = "Delhi → Jaipur Multi-Stop",
        distanceKm: Int = 580
    ): TripPaymentSchedule {
        val days = max(1, tripDurationDays)
        val fare = max(1000, totalFare)

        val milestones = when (days) {
            1 -> {
                // 1-Day Trip (Local / Airport / Same-day outstation): 2 Advance stages
                // Milestone 1: 20% Booking Advance (collected at confirmation)
                // Milestone 2: 80% Trip Start Advance (collected before vehicle departure)
                val m1Amount = (fare * 0.20).toInt()
                val m2Amount = fare - m1Amount
                listOf(
                    PaymentScheduleMilestone(
                        id = "m1",
                        title = "Booking Advance (20%)",
                        subtitle = "₹$m1Amount • Paid via UPI",
                        amount = m1Amount,
                        percentage = 20,
                        status = MilestoneStatus.PAID
                    ),
                    PaymentScheduleMilestone(
                        id = "m2",
                        title = "Trip Start Advance (80%)",
                        subtitle = "₹$m2Amount • Due before departure",
                        amount = m2Amount,
                        percentage = 80,
                        status = MilestoneStatus.DUE
                    )
                )
            }

            2 -> {
                // 2-Day Trip: 3 Advance stages
                // Milestone 1: 15% Booking Advance (at confirmation)
                // Milestone 2: 45% Trip Start Advance (Day 1 - before departure)
                // Milestone 3: 40% Day 2 Advance (before Day 2 journey begins)
                val m1Amount = (fare * 0.15).toInt()
                val m2Amount = (fare * 0.45).toInt()
                val m3Amount = fare - m1Amount - m2Amount
                listOf(
                    PaymentScheduleMilestone(
                        id = "m1",
                        title = "Booking Advance (15%)",
                        subtitle = "₹$m1Amount • Paid via UPI",
                        amount = m1Amount,
                        percentage = 15,
                        status = MilestoneStatus.PAID
                    ),
                    PaymentScheduleMilestone(
                        id = "m2",
                        title = "Trip Start Advance (Day 1) (45%)",
                        subtitle = "₹$m2Amount • Due before Day 1 departure",
                        amount = m2Amount,
                        percentage = 45,
                        status = MilestoneStatus.DUE
                    ),
                    PaymentScheduleMilestone(
                        id = "m3",
                        title = "Day 2 Advance (40%)",
                        subtitle = "₹$m3Amount • Due before Day 2 starts",
                        amount = m3Amount,
                        percentage = 40,
                        status = MilestoneStatus.UPCOMING
                    )
                )
            }

            3 -> {
                // 3-Day Trip: 4 Advance stages (10% + 40% + 30% + 20%)
                val m1Amount = (fare * 0.10).toInt()
                val m2Amount = (fare * 0.40).toInt()
                val m3Amount = (fare * 0.30).toInt()
                val m4Amount = fare - m1Amount - m2Amount - m3Amount
                listOf(
                    PaymentScheduleMilestone(
                        id = "m1",
                        title = "Booking Advance (10%)",
                        subtitle = "₹$m1Amount • Paid via UPI",
                        amount = m1Amount,
                        percentage = 10,
                        status = MilestoneStatus.PAID
                    ),
                    PaymentScheduleMilestone(
                        id = "m2",
                        title = "Trip Start Advance (Day 1) (40%)",
                        subtitle = "₹$m2Amount • Due before departure",
                        amount = m2Amount,
                        percentage = 40,
                        status = MilestoneStatus.DUE
                    ),
                    PaymentScheduleMilestone(
                        id = "m3",
                        title = "Mid-Trip Advance (Day 2) (30%)",
                        subtitle = "₹$m3Amount • Due before Day 2 starts",
                        amount = m3Amount,
                        percentage = 30,
                        status = MilestoneStatus.UPCOMING
                    ),
                    PaymentScheduleMilestone(
                        id = "m4",
                        title = "Final Leg Advance (Day 3) (20%)",
                        subtitle = "₹$m4Amount • Due before return leg",
                        amount = m4Amount,
                        percentage = 20,
                        status = MilestoneStatus.UPCOMING
                    )
                )
            }

            else -> {
                // 4-Day & Multi-Day Tours (days >= 4): 4 Advance stages (10% + 40% + 30% + 20%)
                // Mid-trip milestone occurs on Day ceil(days / 2.0)
                val midTripDay = ceil(days / 2.0).toInt().coerceAtLeast(2)
                val finalDay = days

                val m1Amount = (fare * 0.10).toInt()
                val m2Amount = (fare * 0.40).toInt()
                val m3Amount = (fare * 0.30).toInt()
                val m4Amount = fare - m1Amount - m2Amount - m3Amount

                listOf(
                    PaymentScheduleMilestone(
                        id = "m1",
                        title = "Booking Advance (10%)",
                        subtitle = "₹$m1Amount • Paid via UPI",
                        amount = m1Amount,
                        percentage = 10,
                        status = MilestoneStatus.PAID
                    ),
                    PaymentScheduleMilestone(
                        id = "m2",
                        title = "Trip Start Advance (Day 1) (40%)",
                        subtitle = "₹$m2Amount • Due before departure",
                        amount = m2Amount,
                        percentage = 40,
                        status = MilestoneStatus.DUE
                    ),
                    PaymentScheduleMilestone(
                        id = "m3",
                        title = "Mid-Trip Advance (Day $midTripDay) (30%)",
                        subtitle = "₹$m3Amount • Due before Day $midTripDay starts",
                        amount = m3Amount,
                        percentage = 30,
                        status = MilestoneStatus.UPCOMING
                    ),
                    PaymentScheduleMilestone(
                        id = "m4",
                        title = "Final Leg Advance (Day $finalDay) (20%)",
                        subtitle = "₹$m4Amount • Due before return leg",
                        amount = m4Amount,
                        percentage = 20,
                        status = MilestoneStatus.UPCOMING
                    )
                )
            }
        }

        return TripPaymentSchedule(
            routeSummary = routeSummary,
            durationAndDistance = "$days Days • $distanceKm km",
            totalAmount = fare,
            milestones = milestones,
            securityNote = "All payments are collected in advance before each trip segment begins. Partner will verify via secure OTP."
        )
    }

    /**
     * Generates [PaymentOtpData] for a specific active milestone stage.
     */
    fun createOtpDataForMilestone(
        milestone: PaymentScheduleMilestone,
        schedule: TripPaymentSchedule,
        driverName: String = "Rajesh Kumar",
        vehicleDetails: String = "Toyota Innova Crysta • DL 01 AB 1234"
    ): PaymentOtpData {
        val currentIndex = schedule.milestones.indexOfFirst { it.id == milestone.id }.coerceAtLeast(0)
        val stageNumber = currentIndex + 1
        val totalStages = schedule.milestones.size

        return PaymentOtpData(
            amount = milestone.amount,
            stageLabel = "${milestone.percentage}% – ${milestone.title}",
            otpCode = "7429",
            validityDurationSeconds = 600,
            driverName = driverName,
            vehicleDetails = vehicleDetails,
            progressText = "Progress: $stageNumber of $totalStages advance payments verified"
        )
    }
}
