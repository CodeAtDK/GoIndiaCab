package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.StarIcon
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.viewmodel.MyReviewsUiState
import com.example.goindiacab.viewmodel.MyReviewsViewModel
import com.example.goindiacab.viewmodel.ReviewItem
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val DarkNavyHeader = Color(0xFF020C1B)
private val BrandOrange = Color(0xFFFF6D00)
private val CardBorderColor = Color(0xFFE2E8F0)
private val SecondaryTextColor = Color(0xFF64748B)
private val PrimaryTextColor = Color(0xFF0F172A)
private val SoftBackground = Color(0xFFF8FAFC)
private val StarGold = Color(0xFFF59E0B)
private val StarInactive = Color(0xFFE2E8F0)
private val InitialsBg = Color(0xFFFFEDD5)
private val InitialsText = Color(0xFFC2410C)

/**
 * My Reviews Screen matching MyReview.svg design.
 */
@Composable
fun MyReviewsScreen(
    viewModel: MyReviewsViewModel = remember { MyReviewsViewModel() },
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            MyReviewsTopBar(onBackClick = onBackClick)

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                item(key = "review_summary") {
                    ReviewSummaryCard(
                        averageRating = uiState.averageRating,
                        totalReviews = uiState.totalReviews
                    )
                }

                // Review items
                items(uiState.reviews, key = { it.id }) { review ->
                    ReviewItemCard(review = review)
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun MyReviewsTopBar(
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkNavyHeader
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_back_arrow),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "My Reviews",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
            IconButton(onClick = { /* overflow */ }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_more_vert),
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ReviewSummaryCard(
    averageRating: Double,
    totalReviews: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Average Rating
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Average Rating",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryTextColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = averageRating.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryTextColor
                    )
                    // Star Rating Row
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        val roundedRating = averageRating.toInt()
                        for (i in 1..5) {
                            StarIcon(
                                size = 16.dp,
                                color = if (i <= roundedRating) StarGold else StarInactive,
                                isFilled = i <= roundedRating
                            )
                        }
                    }
                }
            }

            // Right: Total Reviews
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Total Reviews",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryTextColor
                )
                Text(
                    text = totalReviews.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandOrange
                )
            }
        }
    }
}

@Composable
private fun ReviewItemCard(
    review: ReviewItem
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Initials + Name + Car + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver Initials Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(InitialsBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.initials,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = InitialsText
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Cab
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.driverName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTextColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = review.vehicleInfo,
                        fontSize = 12.sp,
                        color = SecondaryTextColor
                    )
                }

                // Date
                Text(
                    text = review.date,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Stars
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                for (i in 1..5) {
                    StarIcon(
                        size = 16.dp,
                        color = if (i <= review.rating) StarGold else StarInactive,
                        isFilled = i <= review.rating
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Review text
            Text(
                text = review.reviewText,
                fontSize = 13.5.sp,
                color = PrimaryTextColor,
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * Standalone Preview for MyReviewsScreen.
 */
@Composable
@Preview
fun MyReviewsScreenPreview() {
    GoIndiaCabTheme {
        MyReviewsScreen()
    }
}
