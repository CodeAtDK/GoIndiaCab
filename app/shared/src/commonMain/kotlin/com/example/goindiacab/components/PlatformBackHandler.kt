package com.example.goindiacab.components

import androidx.compose.runtime.Composable

/**
 * Multiplatform abstraction for hardware/gesture back button handling.
 * On Android, this registers an active OnBackPressedCallback via androidx.activity.compose.BackHandler.
 * On other platforms, it safely operates as a no-op.
 */
@Composable
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)

/**
 * Provides a platform-native toast notification (e.g. Android Toast).
 */
@Composable
expect fun rememberPlatformToast(): (String) -> Unit

/**
 * Provides a platform-native exit app handler (e.g. Activity.finish() on Android).
 */
@Composable
expect fun rememberAppExiter(): () -> Unit
