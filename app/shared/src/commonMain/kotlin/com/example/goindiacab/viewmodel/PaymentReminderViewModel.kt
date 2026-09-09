package com.example.goindiacab.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for Payment Reminder Popup matching payment-reminder-popup.svg.
 */
@Immutable
data class PaymentReminderUiState(
    val title: String = "Payment Due Soon!",
    val milestonePercent: Int = 30,
    val milestoneName: String = "mid-trip payment",
    val routeOrigin: String = "Delhi",
    val routeDestination: String = "Jaipur",
    val dueInMinutes: Int = 15,
    val amountDueInr: Int = 3150,
    val remindAgainMinutes: Int = 5,
    val isDismissed: Boolean = false,
    val isProceedingToPayment: Boolean = false,
    val userMessage: String? = null
)

/**
 * ViewModel managing milestone payment reminder countdown, actions, and user intents.
 */
class PaymentReminderViewModel(
    initialAmountDue: Int = 3150,
    initialRouteOrigin: String = "Delhi",
    initialRouteDestination: String = "Jaipur",
    initialMilestonePercent: Int = 30
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PaymentReminderUiState(
            amountDueInr = initialAmountDue,
            routeOrigin = initialRouteOrigin,
            routeDestination = initialRouteDestination,
            milestonePercent = initialMilestonePercent
        )
    )
    val uiState: StateFlow<PaymentReminderUiState> = _uiState.asStateFlow()

    /**
     * User clicks Remind Later: snoozes reminder for 5 minutes.
     */
    fun onRemindLaterClicked() {
        val snooze = _uiState.value.remindAgainMinutes
        _uiState.update {
            it.copy(
                isDismissed = true,
                userMessage = "Payment reminder snoozed for $snooze minutes"
            )
        }
    }

    /**
     * User clicks Pay Now: proceeds forward to payment gateway.
     */
    fun onPayNowClicked() {
        _uiState.update { it.copy(isProceedingToPayment = true) }
    }

    /**
     * Resets state when modal is reopened.
     */
    fun resetState() {
        _uiState.update {
            it.copy(
                isDismissed = false,
                isProceedingToPayment = false,
                userMessage = null
            )
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
