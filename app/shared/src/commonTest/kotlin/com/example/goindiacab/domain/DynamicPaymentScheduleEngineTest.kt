package com.example.goindiacab.domain

import com.example.goindiacab.data.models.MilestoneStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DynamicPaymentScheduleEngineTest {

    @Test
    fun testOneDayTripSchedule() {
        val totalFare = 5000
        val schedule = DynamicPaymentScheduleEngine.generateSchedule(
            totalFare = totalFare,
            tripDurationDays = 1,
            pickupDate = "Tomorrow",
            routeSummary = "Delhi → Agra",
            distanceKm = 240
        )

        assertEquals(2, schedule.milestones.size)
        assertEquals(20, schedule.milestones[0].percentage)
        assertEquals(80, schedule.milestones[1].percentage)
        assertEquals(100, schedule.milestones.sumOf { it.percentage })
        assertEquals(totalFare, schedule.milestones.sumOf { it.amount })
        assertEquals(MilestoneStatus.PAID, schedule.milestones[0].status)
        assertEquals(MilestoneStatus.DUE, schedule.milestones[1].status)
        assertTrue(schedule.securityNote.contains("advance", ignoreCase = true))
    }

    @Test
    fun testTwoDayTripSchedule() {
        val totalFare = 12000
        val schedule = DynamicPaymentScheduleEngine.generateSchedule(
            totalFare = totalFare,
            tripDurationDays = 2,
            pickupDate = "15 Oct",
            routeSummary = "Delhi → Jaipur",
            distanceKm = 560
        )

        assertEquals(3, schedule.milestones.size)
        assertEquals(15, schedule.milestones[0].percentage)
        assertEquals(45, schedule.milestones[1].percentage)
        assertEquals(40, schedule.milestones[2].percentage)
        assertEquals(100, schedule.milestones.sumOf { it.percentage })
        assertEquals(totalFare, schedule.milestones.sumOf { it.amount })
    }

    @Test
    fun testThreeDayTripSchedule() {
        val totalFare = 18500
        val schedule = DynamicPaymentScheduleEngine.generateSchedule(
            totalFare = totalFare,
            tripDurationDays = 3,
            pickupDate = "12 Sep",
            routeSummary = "Delhi → Jaipur → Agra",
            distanceKm = 720
        )

        assertEquals(4, schedule.milestones.size)
        assertEquals(10, schedule.milestones[0].percentage)
        assertEquals(40, schedule.milestones[1].percentage)
        assertEquals(30, schedule.milestones[2].percentage)
        assertEquals(20, schedule.milestones[3].percentage)
        assertEquals(100, schedule.milestones.sumOf { it.percentage })
        assertEquals(totalFare, schedule.milestones.sumOf { it.amount })
    }

    @Test
    fun testMultiDayTripScheduleCalculation() {
        val totalFare = 32000
        val days = 6
        val schedule = DynamicPaymentScheduleEngine.generateSchedule(
            totalFare = totalFare,
            tripDurationDays = days,
            pickupDate = "01 Nov",
            routeSummary = "Delhi → Himachal Circuit",
            distanceKm = 1400
        )

        assertEquals(4, schedule.milestones.size)
        assertEquals(10, schedule.milestones[0].percentage)
        assertEquals(40, schedule.milestones[1].percentage)
        assertEquals(30, schedule.milestones[2].percentage)
        assertEquals(20, schedule.milestones[3].percentage)
        assertEquals(totalFare, schedule.milestones.sumOf { it.amount })
        assertTrue(schedule.milestones[2].title.contains("Day 3"))
        assertTrue(schedule.milestones[3].title.contains("Day 6"))
    }

    @Test
    fun testAdvancePercentageLookup() {
        assertEquals(20, DynamicPaymentScheduleEngine.getAdvancePercentage(1))
        assertEquals(15, DynamicPaymentScheduleEngine.getAdvancePercentage(2))
        assertEquals(10, DynamicPaymentScheduleEngine.getAdvancePercentage(3))
        assertEquals(10, DynamicPaymentScheduleEngine.getAdvancePercentage(4))
        assertEquals(10, DynamicPaymentScheduleEngine.getAdvancePercentage(7))
    }

    @Test
    fun testOtpDataGeneration() {
        val schedule = DynamicPaymentScheduleEngine.generateSchedule(
            totalFare = 20000,
            tripDurationDays = 4
        )
        val milestone = schedule.milestones[1]
        val otpData = DynamicPaymentScheduleEngine.createOtpDataForMilestone(
            milestone = milestone,
            schedule = schedule,
            driverName = "Vikram Singh",
            vehicleDetails = "Innova Crysta • DL 01 AB 1234"
        )

        assertEquals(8000, otpData.amount)
        assertEquals("7429", otpData.otpCode)
        assertEquals("Progress: 2 of 4 advance payments verified", otpData.progressText)
        assertEquals("Vikram Singh", otpData.driverName)
    }
}
