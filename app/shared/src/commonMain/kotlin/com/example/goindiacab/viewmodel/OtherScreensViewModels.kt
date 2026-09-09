package com.example.goindiacab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Rate Us Screen.
 */
data class RateUsUiState(
    val rating: Int = 5,
    val feedbackText: String = "",
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val userMessage: String? = null
)

/**
 * ViewModel managing user feedback, star rating, and Play Store submission.
 */
class RateUsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RateUsUiState())
    val uiState: StateFlow<RateUsUiState> = _uiState.asStateFlow()

    fun setRating(stars: Int) {
        _uiState.update { it.copy(rating = stars.coerceIn(1, 5)) }
    }

    fun updateFeedback(feedback: String) {
        _uiState.update { it.copy(feedbackText = feedback) }
    }

    fun submitRating(onComplete: () -> Unit = {}) {
        val currentRating = _uiState.value.rating
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            delay(400) // smooth interaction feedback
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    isSubmitted = true,
                    userMessage = "Thank you for rating GoIndiaCab $currentRating stars!"
                )
            }
            onComplete()
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}

/**
 * UI State for Refer & Earn Screen.
 */
data class ReferEarnUiState(
    val referralCode: String = "RAHUL200",
    val friendsReferred: Int = 5,
    val earnedRewardsInr: Int = 1000,
    val rewardPerReferralInr: Int = 500,
    val isCopied: Boolean = false,
    val userMessage: String? = null
)

/**
 * ViewModel managing referral code copying, social sharing payloads, and rewards tracking.
 */
class ReferEarnViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReferEarnUiState())
    val uiState: StateFlow<ReferEarnUiState> = _uiState.asStateFlow()

    fun copyReferralCode(onCopySuccess: (String) -> Unit) {
        val code = _uiState.value.referralCode
        viewModelScope.launch {
            _uiState.update { it.copy(isCopied = true, userMessage = "Referral code $code copied to clipboard!") }
            onCopySuccess(code)
            delay(2000)
            _uiState.update { it.copy(isCopied = false) }
        }
    }

    fun shareViaWhatsApp(onLaunch: (shareText: String) -> Unit) {
        val code = _uiState.value.referralCode
        val shareMessage = "Hey! Use my GoIndiaCab referral code *$code* to get flat discounts on your outstation intercity cab bookings! Download now: https://goindiacab.com/app"
        onLaunch(shareMessage)
    }

    fun inviteViaSms(onLaunch: (smsText: String) -> Unit) {
        val code = _uiState.value.referralCode
        val smsMessage = "Book your next intercity trip with GoIndiaCab using code $code. Download: https://goindiacab.com/app"
        onLaunch(smsMessage)
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
