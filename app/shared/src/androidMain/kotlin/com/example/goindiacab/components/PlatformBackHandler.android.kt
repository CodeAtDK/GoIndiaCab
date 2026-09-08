package com.example.goindiacab.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    BackHandler(enabled = enabled, onBack = onBack)
}

@Composable
actual fun rememberPlatformToast(): (String) -> Unit {
    val context = LocalContext.current
    return { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
actual fun rememberAppExiter(): () -> Unit {
    val context = LocalContext.current
    return {
        context.findActivity()?.finish()
    }
}
