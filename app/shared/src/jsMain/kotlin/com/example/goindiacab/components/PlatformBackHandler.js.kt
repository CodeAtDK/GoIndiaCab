package com.example.goindiacab.components

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // Web JS fallback
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
