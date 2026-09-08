package com.example.goindiacab.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
actual fun PlatformMapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Float,
    isMyLocationEnabled: Boolean,
    recenterTrigger: Int,
    onCameraIdle: (latitude: Double, longitude: Double) -> Unit,
    onMapLoaded: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(recenterTrigger) {
        if (recenterTrigger > 0) {
            offsetX = 0f
            offsetY = 0f
            onCameraIdle(latitude, longitude)
        }
    }

    LaunchedEffect(Unit) {
        onMapLoaded()
    }

    Box(
        modifier = modifier
            .background(Color(0xFFE8ECEF))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onCameraIdle(latitude - offsetY * 0.0001, longitude + offsetX * 0.0001)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f + offsetX
            val cy = size.height / 2f + offsetY

            // Draw stylized map roads
            val roadColor = Color.White
            val roadAccent = Color(0xFFFDE68A)
            val strokeMajor = Stroke(width = 10.dp.toPx())
            val strokeMinor = Stroke(width = 5.dp.toPx())

            // Main arterial roads
            drawLine(roadAccent, Offset(0f, cy - 80f), Offset(size.width, cy + 120f), strokeMajor.width)
            drawLine(roadColor, Offset(cx - 150f, 0f), Offset(cx + 100f, size.height), strokeMajor.width)
            drawLine(roadColor, Offset(0f, cy + 200f), Offset(size.width, cy - 200f), strokeMinor.width)
            drawLine(roadColor, Offset(cx + 250f, 0f), Offset(cx - 200f, size.height), strokeMinor.width)

            // Stylized river
            val riverPath = Path().apply {
                moveTo(size.width * 0.7f + offsetX * 0.5f, 0f)
                cubicTo(
                    size.width * 0.65f + offsetX * 0.5f, size.height * 0.35f,
                    size.width * 0.85f + offsetX * 0.5f, size.height * 0.7f,
                    size.width * 0.75f + offsetX * 0.5f, size.height
                )
            }
            drawPath(riverPath, Color(0xFFA5D8FF), style = Stroke(width = 32.dp.toPx()))
        }
    }
}
