package com.example.goindiacab.components

import androidx.compose.runtime.Composable

/**
 * Multiplatform abstraction for requesting location permissions at runtime.
 * On Android, this triggers the native system location permission dialog.
 * On other platforms, it executes the granted callback directly.
 */
@Composable
expect fun rememberLocationPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
): () -> Unit
