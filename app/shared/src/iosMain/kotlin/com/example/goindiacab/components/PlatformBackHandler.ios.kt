package com.example.goindiacab.components

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // iOS gesture back handling fallback
}

@Composable
actual fun rememberPlatformToast(): (String) -> Unit {
    return { message ->
        println("[Toast] $message")
    }
}

@Composable
actual fun rememberAppExiter(): () -> Unit {
    return {}
}
