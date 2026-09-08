package com.example.goindiacab.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.components.PlatformMapView
import com.example.goindiacab.components.rememberLocationPermissionRequester
import com.example.goindiacab.data.models.LocationItem
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BrandBlue
import com.example.goindiacab.theme.SurfaceGray
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.TextMuted
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import com.example.goindiacab.viewmodel.ConfirmPickupViewModel
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.bg_map_delhi
import goindiacab.app.shared.generated.resources.ic_back_arrow
import goindiacab.app.shared.generated.resources.ic_gps_crosshair
import org.jetbrains.compose.resources.painterResource

/**
 * Screen 17: Confirm Pickup Location Map Screen (Phase 3).
 *
 * Architecture & Lifecycle Role:
 * - Interactive draggable map pin placement with live GPS geolocation and reverse-geocoded street address.
 * - Navigation Flow:
 *     - "Confirm Pickup Location": saves the pickup location in [HomeViewModel] and calls [popBackTo(AppScreen.HOME)],
 *       unwinding the map and search screens cleanly so the user lands back on Home with their pickup point set.
 * - Back Button Contract:
 *     - Hardware back and top back arrow invoke [onBackClick], popping back to [PICKUP_LOCATION_SELECTION].
 */
@Composable
fun ConfirmPickupLocationMapScreen(
    initialLocation: LocationItem? = null,
    viewModel: ConfirmPickupViewModel = remember { AppContainer.createConfirmPickupViewModel() },
    onBackClick: () -> Unit,
    onConfirmPickup: (LocationItem) -> Unit
) {
    LaunchedEffect(initialLocation) {
        initialLocation?.let { viewModel.initializeWithLocation(it) }
    }

    val uiState by viewModel.uiState.collectAsState()

    val requestLocationPermission = rememberLocationPermissionRequester(
        onGranted = {
            viewModel.recenterToGps()
        },
        onDenied = {
            viewModel.recenterToGps()
        }
    )

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceGray)
        ) {
            // 1. Fullscreen Live Google Map Viewport with GPS and Coordinates
            PlatformMapView(
                modifier = Modifier.fillMaxSize(),
                latitude = uiState.targetPoint.latitude,
                longitude = uiState.targetPoint.longitude,
                zoom = uiState.mapZoom,
                isMyLocationEnabled = true,
                recenterTrigger = uiState.recenterTrigger,
                onCameraIdle = { lat, lng ->
                    viewModel.onCoordinatesMoved(lat, lng)
                }
            )

            // 2. Animated Center Pin Marker (Lifts up with spring when dragged, drops down on release)
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedCenterPin(
                    isDragging = uiState.isDragging,
                    isReverseGeocoding = uiState.isReverseGeocoding
                )
            }

            // 3. Top Navigation Bar
            TopBarHeader(
                onBackClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            )

            // 4. Floating GPS Recenter & Zoom Controls (Positioned above bottom sheet)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 220.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Zoom In Button
                Surface(
                    onClick = { viewModel.onZoomChange(1.2f) },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }

                // Zoom Out Button
                Surface(
                    onClick = { viewModel.onZoomChange(0.83f) },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }

                // GPS Recenter FAB
                Surface(
                    onClick = { requestLocationPermission() },
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceGray,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_gps_crosshair),
                            contentDescription = "Recenter GPS",
                            tint = BrandBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 5. Bottom Confirmation Sheet Card
            BottomConfirmationCard(
                title = uiState.locationTitle,
                address = uiState.fullAddress,
                isFavorite = uiState.isFavorite,
                isReverseGeocoding = uiState.isReverseGeocoding,
                onFavoriteToggle = { viewModel.toggleFavorite() },
                onConfirmClick = {
                    viewModel.confirmPickup { confirmedItem ->
                        onConfirmPickup(confirmedItem)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Top App Bar matching SVG 17.
 */
@Composable
private fun TopBarHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded Back Button Card
            Surface(
                onClick = onBackClick,
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back_arrow),
                        contentDescription = "Back",
                        tint = TextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Subtitle Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Confirm Pickup Location",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Drag the map to move the pin",
                    fontSize = 13.sp,
                    fontFamily = dmSansFontFamily(),
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

/**
 * Interactive multi-touch Map Viewport.
 */
@Composable
private fun InteractiveMapView(
    mapOffsetX: Float,
    mapOffsetY: Float,
    mapZoom: Float,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onZoomChange: (delta: Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (zoom != 1f) {
                        onZoomChange(zoom)
                    }
                    if (pan != Offset.Zero) {
                        onDrag(pan.x, pan.y)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
    ) {
        // High-resolution Map Layer
        Image(
            painter = painterResource(Res.drawable.bg_map_delhi),
            contentDescription = "Map Viewport",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = mapOffsetX
                    translationY = mapOffsetY
                    scaleX = mapZoom
                    scaleY = mapZoom
                },
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Animated Center Pin with elevation physics, shadow, and tactile bounce.
 */
@Composable
private fun AnimatedCenterPin(
    isDragging: Boolean,
    isReverseGeocoding: Boolean
) {
    // Lift elevation offset (spring physics)
    val liftElevation by animateDpAsState(
        targetValue = if (isDragging) (-20).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    // Shadow scaling when lifted
    val shadowAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.25f else 0.45f
    )
    val shadowScale by animateFloatAsState(
        targetValue = if (isDragging) 0.7f else 1.0f
    )

    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ground Shadow Oval
        Canvas(
            modifier = Modifier
                .size(width = 24.dp * shadowScale, height = 8.dp * shadowScale)
                .align(Alignment.Center)
                .offset(y = 18.dp)
        ) {
            drawOval(
                color = Color.Black.copy(alpha = shadowAlpha),
                topLeft = Offset.Zero,
                size = size
            )
        }

        // Floating Pin Marker
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = liftElevation)
                .size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Custom teardrop pin path
                val pinPath = Path().apply {
                    val radius = w * 0.38f
                    val centerX = w / 2f
                    val centerY = radius

                    // Circle arc at top
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            left = centerX - radius,
                            top = 0f,
                            right = centerX + radius,
                            bottom = radius * 2
                        ),
                        startAngleDegrees = 140f,
                        sweepAngleDegrees = 260f,
                        forceMoveTo = false
                    )

                    // Sharp triangle pointer pointing at center bottom
                    lineTo(centerX, h)
                    close()
                }

                // Red Pin Fill
                drawPath(
                    path = pinPath,
                    color = Color(0xFFEF4444)
                )

                // White Pin Outline
                drawPath(
                    path = pinPath,
                    color = Color.White,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Inner White Target Circle
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = Offset(w / 2f, w * 0.38f)
                )
            }

            // Pulse or loading indicator if reverse geocoding
            if (isReverseGeocoding) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 1.5.dp
                )
            }
        }
    }
}

/**
 * Bottom Confirmation Sheet Card matching SVG 17.
 */
@Composable
private fun BottomConfirmationCard(
    title: String,
    address: String,
    isFavorite: Boolean,
    isReverseGeocoding: Boolean,
    onFavoriteToggle: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Small subtle sheet indicator
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE5E7EB))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle Tag: "PICKUP LOCATION" with green indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Text(
                    text = "PICKUP LOCATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color(0xFF10B981),
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Location Details & Favorite Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isReverseGeocoding) "Locating..." else title.ifBlank { "Selected Pickup Point" },
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = address.ifBlank { "Drag map or use crosshair to fine-tune pickup point" },
                        fontSize = 13.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Favorite Heart Button
                Surface(
                    onClick = onFavoriteToggle,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0F7FF),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isFavorite) "♥" else "♡",
                            fontSize = 20.sp,
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Solid Primary CTA Button: "Confirm Pickup Location"
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Confirm Pickup Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }
        }
    }
}
