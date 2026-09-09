package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.LocationItem
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BackgroundWhite
import com.example.goindiacab.theme.SurfaceGray
import com.example.goindiacab.theme.BrandBlue
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.TextMuted
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import com.example.goindiacab.viewmodel.LocationSelectionViewModel
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_back_arrow
import goindiacab.app.shared.generated.resources.ic_chevron_right
import goindiacab.app.shared.generated.resources.ic_gps_crosshair
import goindiacab.app.shared.generated.resources.ic_pin_drop_blue
import goindiacab.app.shared.generated.resources.ic_route_swap
import org.jetbrains.compose.resources.painterResource

/**
 * Screen 16: Pickup Location Selection Screen (Phase 3).
 *
 * Architecture & Lifecycle Role:
 * - Provides search box for pickup street/hub/airport address, GPS current location resolver,
 *   and recent pickup locations list.
 * - Navigation Flow:
 *     - Selecting an address pushes [CONFIRM_PICKUP_MAP] for pin placement confirmation.
 * - Back Button Contract:
 *     - Hardware back and top back arrow invoke [onBackClick], returning to [HOME].
 */
@Composable
fun PickupLocationSelectionScreen(
    viewModel: LocationSelectionViewModel = remember { AppContainer.createLocationSelectionViewModel() },
    onBackClick: () -> Unit,
    onLocationSelected: (LocationItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer(backgroundColor = SurfaceGray) {
        Scaffold(
            containerColor = SurfaceGray,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Top Header Input Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // 1. FROM Location Card (Active Blue Border)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, BrandBlue),
                        shadowElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Back Button Icon in soft blue container
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEBF3FF))
                                    .clickable(onClick = onBackClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_back_arrow),
                                    contentDescription = "Back",
                                    tint = BrandBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // FROM Label & Value
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "FROM",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = BrandBlue,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                BasicTextField(
                                    value = uiState.fromLocation,
                                    onValueChange = { viewModel.onFromLocationChanged(it) },
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextDark
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(BrandBlue),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. TO Destination Search Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        shadowElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Route Swap Icon in soft blue container
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEBF3FF))
                                    .clickable(onClick = { viewModel.swapLocations() }),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_route_swap),
                                    contentDescription = "Swap Locations",
                                    tint = BrandBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // TO Label & Search Input
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "TO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = Color(0xFF6B7280),
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (uiState.toQuery.isEmpty()) {
                                        Text(
                                            text = "Search destination",
                                            fontSize = 15.sp,
                                            fontFamily = dmSansFontFamily(),
                                            color = Color(0xFF9CA3AF)
                                        )
                                    }
                                    BasicTextField(
                                        value = uiState.toQuery,
                                        onValueChange = { viewModel.onToQueryChanged(it) },
                                        textStyle = TextStyle(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = dmSansFontFamily(),
                                            color = TextDark
                                        ),
                                        singleLine = true,
                                        cursorBrush = SolidColor(BrandBlue),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // Clear query button
                            if (uiState.toQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.clearToQuery() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text(
                                        text = "✕",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. "Use Current Location" GPS Action Banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.useCurrentLocation { resolvedItem ->
                                    onLocationSelected(resolvedItem)
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEBF3FF),
                        border = BorderStroke(1.5.dp, BrandBlue)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Dark blue GPS square badge
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isGpsLocating) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_gps_crosshair),
                                        contentDescription = "GPS Location",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Use Current Location",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = BrandBlue
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (uiState.isGpsLocating) "Acquiring GPS location..." else "Locate using device GPS",
                                    fontSize = 13.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = Color(0xFF4B5563)
                                )
                            }

                            // Right arrow
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_chevron_right),
                                    contentDescription = "Select GPS",
                                    tint = BrandBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Section Header: "SEARCH RESULTS"
                Text(
                    text = "SEARCH RESULTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = Color(0xFF6B7280),
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                // Search Results LazyColumn inside elevated white card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 1.dp
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = uiState.searchResults,
                            key = { it.id }
                        ) { item ->
                            val isFavorite = uiState.favoriteIds.contains(item.id)

                            SearchResultRow(
                                item = item,
                                isFavorite = isFavorite,
                                onItemClick = { onLocationSelected(item) },
                                onFavoriteClick = { viewModel.toggleFavorite(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    item: LocationItem,
    isFavorite: Boolean,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blue Pin in soft blue badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F7FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pin_drop_blue),
                    contentDescription = "Location Pin",
                    tint = BrandBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title and Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 13.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Favorite Heart Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isFavorite) Color(0xFFEBF3FF) else Color(0xFFF3F4F6))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onFavoriteClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFavorite) "♥" else "♡",
                    fontSize = 18.sp,
                    color = if (isFavorite) BrandBlue else Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Thin Separator Divider
        HorizontalDivider(
            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
            color = Color(0xFFF3F4F6),
            thickness = 1.dp
        )
    }
}

@Preview
@Composable
fun PickupLocationSelectionScreenPreview() {
    GoIndiaCabTheme {
        PickupLocationSelectionScreen(
            viewModel = AppContainer.createLocationSelectionViewModel(),
            onBackClick = {},
            onLocationSelected = {}
        )
    }
}
