package com.example.goindiacab.screens
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.AdaptiveContainer
import com.example.goindiacab.components.PlatformBackHandler
import com.example.goindiacab.data.models.PopularGetawayItem
import com.example.goindiacab.data.models.RecentDestinationItem
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.SurfaceGray
import com.example.goindiacab.theme.TextDark
import com.example.goindiacab.theme.TextMuted
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import com.example.goindiacab.viewmodel.SearchDestinationViewModel
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.img_getaway_agra
import goindiacab.app.shared.generated.resources.img_getaway_haridwar
import goindiacab.app.shared.generated.resources.img_getaway_jaipur
import org.jetbrains.compose.resources.painterResource

/**
 * Screen 19: Search Destination Screen (Phase 4).
 *
 * Architecture & Lifecycle Role:
 * - Positioned immediately after [CITY_ROUTE_SELECTION] in the outstation booking funnel.
 * - Provides debounced destination search input, recent search chips, and visual popular getaway destination cards (Agra, Jaipur, Haridwar).
 * - Navigation Flow:
 *     - Selecting a destination updates the drop location and connects the route with the chosen origin city,
 *       then advances the user directly to [ROUTE_CONFIRMATION] to review the multi-stop timeline and fare breakdown.
 * - Back Button Contract:
 *     - Both hardware back gesture and top app bar back arrow invoke [onBackClick], cleanly popping back
 *       to [CITY_ROUTE_SELECTION] so the user can modify their trip modality or origin city.
 */
@Composable
fun SearchDestinationScreen(
    viewModel: SearchDestinationViewModel = remember { AppContainer.createSearchDestinationViewModel() },
    title: String = "Search Destination",
    onBackClick: () -> Unit,
    onDestinationSelected: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Hardware & system back button support
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    AdaptiveContainer {
        Scaffold(
            containerColor = SurfaceGray,
            topBar = {
                OutstationTopBar(
                    title = title,
                    onBackClick = onBackClick
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. Active Orange Search Input Card
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, BrandOrange),
                        shadowElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(BrandOrange)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (uiState.query.isEmpty()) {
                                    Text(
                                        text = "Search destination",
                                        fontSize = 15.sp,
                                        fontFamily = dmSansFontFamily(),
                                        color = TextMuted
                                    )
                                }
                                BasicTextField(
                                    value = uiState.query,
                                    onValueChange = { viewModel.onQueryChanged(it) },
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = dmSansFontFamily(),
                                        color = TextDark
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(BrandOrange),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        imeAction = androidx.compose.ui.text.input.ImeAction.Search
                                    ),
                                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                        onSearch = {
                                            if (uiState.query.isNotBlank()) {
                                                onDestinationSelected(uiState.query.trim())
                                            }
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (uiState.query.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.clearQuery() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✕", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 2. SEARCH RESULTS / RECENT SEARCHES Header
                item {
                    Text(
                        text = if (uiState.query.isNotBlank()) "SEARCH RESULTS" else "RECENT SEARCHES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 3. Search Results Rows
                items(
                    items = uiState.recentSearches,
                    key = { it.id }
                ) { item ->
                    RecentSearchRow(
                        item = item,
                        onClick = { onDestinationSelected(item.title) }
                    )
                }

                // 4. POPULAR GETAWAYS (displayed when query is empty)
                if (uiState.query.isBlank()) {
                    item {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "POPULAR GETAWAYS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark,
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 5. POPULAR GETAWAYS Horizontal Carousel
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(
                                items = uiState.popularGetaways,
                                key = { it.id }
                            ) { getaway ->
                                GetawayCard(
                                    getaway = getaway,
                                    onClick = { onDestinationSelected(getaway.city) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSearchRow(
    item: RecentDestinationItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Clock Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🕒", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "${item.title}, ${item.state}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
            }

            Text(
                text = "${item.distanceKm} km",
                fontSize = 13.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(start = 62.dp, end = 16.dp),
            color = Color(0xFFF1F5F9),
            thickness = 1.dp
        )
    }
}

@Composable
private fun GetawayCard(
    getaway: PopularGetawayItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(136.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Photo Image
            val painter = when (getaway.imageDrawableKey) {
                "img_getaway_agra" -> painterResource(Res.drawable.img_getaway_agra)
                "img_getaway_jaipur" -> painterResource(Res.drawable.img_getaway_jaipur)
                else -> painterResource(Res.drawable.img_getaway_haridwar)
            }

            Image(
                painter = painter,
                contentDescription = getaway.city,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp),
                contentScale = ContentScale.Crop
            )

            // Titles
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getaway.city,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = getaway.landmark,
                    fontSize = 11.sp,
                    fontFamily = dmSansFontFamily(),
                    color = TextMuted
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchDestinationScreenPreview() {
    GoIndiaCabTheme {
        SearchDestinationScreen(
            viewModel = AppContainer.createSearchDestinationViewModel(),
            onBackClick = {},
            onDestinationSelected = {}
        )
    }
}
