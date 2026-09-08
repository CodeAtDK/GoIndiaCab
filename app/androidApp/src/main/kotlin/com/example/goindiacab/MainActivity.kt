package com.example.goindiacab

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.data.network.GoogleMapsConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Wire runtime Google Maps API key from manifest placeholder if present
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val manifestKey = appInfo.metaData?.getString("com.google.android.geo.API_KEY")
            if (!manifestKey.isNullOrBlank() && !manifestKey.startsWith("\${")) {
                GoogleMapsConfig.API_KEY = manifestKey
            }
        } catch (_: Exception) {
            // Graceful fallback to default configuration
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
