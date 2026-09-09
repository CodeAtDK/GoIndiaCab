package com.example.goindiacab.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.viewmodel.NotificationCategory
import com.example.goindiacab.viewmodel.NotificationItem
import com.example.goindiacab.viewmodel.NotificationType
import com.example.goindiacab.viewmodel.NotificationsUiState
import com.example.goindiacab.viewmodel.NotificationsViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val DarkNavyHeader = Color(0xFF020C1B)
private val BrandOrange = Color(0xFFFF6D00)
private val CardBorderColor = Color(0xFFE2E8F0)
private val SecondaryTextColor = Color(0xFF64748B)
private val PrimaryTextColor = Color(0xFF0F172A)
private val SoftBackground = Color(0xFFF8FAFC)
private val IconBgLight = Color(0xFFF1F5F9)

/**
 * Notifications Screen matching notifications-screen.svg.
 */
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = remember { NotificationsViewModel() },
    onBackClick: () -> Unit = {},
    onNotificationClick: (NotificationItem) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            NotificationsTopBar(
                onBackClick = onBackClick,
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onDismissMenu = { showMenu = false },
                onMarkAllRead = {
                    viewModel.markAllAsRead()
                    showMenu = false
                }
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NotificationTabChip(
                    title = "All",
                    isSelected = uiState.selectedCategory == NotificationCategory.ALL,
                    onClick = { viewModel.selectCategory(NotificationCategory.ALL) }
                )
                NotificationTabChip(
                    title = "Rides",
                    isSelected = uiState.selectedCategory == NotificationCategory.RIDES,
                    onClick = { viewModel.selectCategory(NotificationCategory.RIDES) }
                )
                NotificationTabChip(
                    title = "Offers",
                    isSelected = uiState.selectedCategory == NotificationCategory.OFFERS,
                    onClick = { viewModel.selectCategory(NotificationCategory.OFFERS) }
                )
            }

            // Notifications List
            val displayList = uiState.filteredNotifications
            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications right now",
                        color = SecondaryTextColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayList, key = { it.id }) { item ->
                        NotificationCard(
                            item = item,
                            onClick = {
                                viewModel.markAsRead(item.id)
                                onNotificationClick(item)
                            }
                        )
                    }

                    item(key = "list_bottom_space") {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsTopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onMarkAllRead: () -> Unit
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
                text = "Notifications",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
            Box {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_more_vert),
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onDismissMenu
                ) {
                    DropdownMenuItem(
                        text = { Text("Mark all as read") },
                        onClick = onMarkAllRead
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationTabChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .shadow(elevation = if (isSelected) 2.dp else 0.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) BrandOrange else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SecondaryTextColor
            )
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left Category Badge Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(IconBgLight),
                contentAlignment = Alignment.Center
            ) {
                NotificationTypeIcon(type = item.type, size = 20.dp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Body
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTextColor,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.timestamp,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        // Orange unread dot
                        if (item.isUnread) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandOrange)
                            )
                        }
                    }
                }

                Text(
                    text = item.message,
                    fontSize = 13.sp,
                    color = SecondaryTextColor,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun NotificationTypeIcon(
    type: NotificationType,
    size: Dp = 20.dp
) {
    when (type) {
        NotificationType.RIDE_CONFIRMED -> {
            // Checkmark in orange circle
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val stroke = 2.dp.toPx()
                val path = Path().apply {
                    moveTo(w * 0.25f, h * 0.52f)
                    lineTo(w * 0.44f, h * 0.72f)
                    lineTo(w * 0.78f, h * 0.30f)
                }
                drawPath(path, color = BrandOrange, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        NotificationType.PAYMENT_DUE,
        NotificationType.PROMO_OFFER,
        NotificationType.FINAL_PAYMENT -> {
            // Tag icon
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val stroke = 1.8.dp.toPx()
                val path = Path().apply {
                    moveTo(w * 0.20f, h * 0.50f)
                    lineTo(w * 0.50f, h * 0.20f)
                    lineTo(w * 0.80f, h * 0.20f)
                    lineTo(w * 0.80f, h * 0.50f)
                    lineTo(w * 0.50f, h * 0.80f)
                    close()
                }
                drawPath(path, color = BrandOrange, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawCircle(color = BrandOrange, radius = w * 0.07f, center = Offset(w * 0.65f, h * 0.35f))
            }
        }
        NotificationType.RIDE_COMPLETED -> {
            // Route / Flag Map icon
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val stroke = 1.8.dp.toPx()
                val path = Path().apply {
                    moveTo(w * 0.25f, h * 0.25f)
                    lineTo(w * 0.75f, h * 0.25f)
                    lineTo(w * 0.60f, h * 0.50f)
                    lineTo(w * 0.75f, h * 0.75f)
                    lineTo(w * 0.25f, h * 0.75f)
                    close()
                }
                drawPath(path, color = BrandOrange, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        NotificationType.SAFETY_UPDATE -> {
            // Shield icon
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val stroke = 1.8.dp.toPx()
                val path = Path().apply {
                    moveTo(w * 0.50f, h * 0.15f)
                    lineTo(w * 0.80f, h * 0.28f)
                    lineTo(w * 0.80f, h * 0.55f)
                    cubicTo(w * 0.80f, h * 0.75f, w * 0.50f, h * 0.88f, w * 0.50f, h * 0.88f)
                    cubicTo(w * 0.50f, h * 0.88f, w * 0.20f, h * 0.75f, w * 0.20f, h * 0.55f)
                    lineTo(w * 0.20f, h * 0.28f)
                    close()
                }
                drawPath(path, color = BrandOrange, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}

/**
 * Standalone Preview for NotificationsScreen.
 */
@Composable
@Preview
fun NotificationsScreenPreview() {
    GoIndiaCabTheme {
        NotificationsScreen()
    }
}
