package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PrimaryButton
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_location_pin
import org.jetbrains.compose.resources.painterResource

import com.example.goindiacab.components.rememberLocationPermissionRequester

@Composable
fun LocationPermissionScreen(
    onAllowLocationClick: () -> Unit = {},
    onEnterManuallyClick: () -> Unit = {}
) {
    val requestLocationPermission = rememberLocationPermissionRequester(
        onGranted = onAllowLocationClick,
        onDenied = onEnterManuallyClick
    )

    AdaptiveContainer(backgroundColor = BackgroundWhite) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Shift location icon card downwards
            Spacer(modifier = Modifier.height(32.dp))

            // Main Graphic Container Card with flexible responsive bounds
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 340.dp)
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceGray),
                contentAlignment = Alignment.Center
            ) {
                // Outer Peach Circle
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(BadgePeachBg),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner Vibrant Orange Circle with Elevation
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                ambientColor = BrandOrange.copy(alpha = 0.5f),
                                spotColor = BrandOrange.copy(alpha = 0.6f)
                            )
                            .clip(CircleShape)
                            .background(BrandOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_location_pin),
                            contentDescription = "Location Pin",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            // Reduced gap so Enable Location Access stays at the perfect vertical spot
            Spacer(modifier = Modifier.height(12.dp))

            // Title & Description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Enable Location Access",
                    fontSize = 29.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "GoIndiaCab needs your location to find nearby cabs, calculate precise ETA, and automatically set your pickup point.",
                    fontSize = 16.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = outfitFontFamily(),
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Reduced spacer so Allow Location Access button moves up closer
            Spacer(modifier = Modifier.height(18.dp))

            // Actions Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                PrimaryButton(
                    text = "Allow Location Access",
                    backgroundColor = BrandOrange,
                    onClick = requestLocationPermission,
                    height = 56.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Enter Manually with 48dp minimum accessible touch target
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onEnterManuallyClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enter Manually",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LocationPermissionScreenPreview() {
    GoIndiaCabTheme {
        LocationPermissionScreen()
    }
}
