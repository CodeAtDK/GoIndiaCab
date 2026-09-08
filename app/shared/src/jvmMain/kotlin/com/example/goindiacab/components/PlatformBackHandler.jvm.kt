package com.example.goindiacab.components

import androidx.compose.runtime.Composable
import kotlin.system.exitProcess

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // Desktop JVM does not have an Android-style hardware back button
}

@Composable
actual fun rememberPlatformToast(): (String) -> Unit {
    return { message ->
        println("[Toast] $message")
    }
}

@Composable
actual fun rememberAppExiter(): () -> Unit {
    return {
        exitProcess(0)
    }
}
