package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.di.AppContainer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.goindiacab.viewmodel.BookingFlowViewModel

/**
 * Screen: Trip Start Payment & Partner OTP Notice (trip-start-payment.svg).
 * Delegates directly to [TripStartPaymentScreen] matching the official Figma SVG design.
 */
@Composable
fun PaymentOtpVerificationScreen(
    viewModel: BookingFlowViewModel,
    onPaymentVerified: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TripStartPaymentScreen(
        viewModel = viewModel,
        onPaymentConfirmed = onPaymentVerified,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Preview
@Composable
fun PaymentOtpVerificationScreenPreview() {
    GoIndiaCabTheme {
        PaymentOtpVerificationScreen(
            viewModel = AppContainer.createBookingFlowViewModel(),
            onPaymentVerified = {},
            onBackClick = {}
        )
    }
}
