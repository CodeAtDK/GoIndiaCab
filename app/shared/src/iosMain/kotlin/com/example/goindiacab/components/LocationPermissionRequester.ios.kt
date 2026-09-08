package com.example.goindiacab.components

import androidx.compose.runtime.Composable

@Composable
actual fun rememberLocationPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    return {
        onGranted()
    }
}
