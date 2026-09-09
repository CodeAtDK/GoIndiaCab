package com.example.goindiacab.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Color Tokens matching side-drawer-menu.svg
private val DrawerHeaderDark = Color(0xFF0A1A3A)
private val DrawerHeaderLight = Color(0xFF162E5C)
private val DrawerBg = Color(0xFFF4F6F9)
private val DrawerTextDark = Color(0xFF1A253C)
private val DrawerTextSub = Color(0xFF626D7F)
private val DrawerTextHeaderSub = Color(0xFFA5B3CF)
private val DrawerIconBg = Color(0xFFF0F4F8)
private val DrawerReferCardBg = Color(0xFFFFF0E6)
private val DrawerOrange = Color(0xFFFF6B00)
private val DrawerLogoutRed = Color(0xFFE53E3E)
private val DrawerDividerColor = Color(0xFFEAEFF3)

@Composable
fun SideDrawerMenu(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    userName: String = "Rahul Sharma",
    userPhone: String = "+91 9876543210",
    userInitials: String = "RS",
    onItemClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = tween(250)),
        exit = fadeOut(animationSpec = tween(250))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.90f)
                    .align(Alignment.CenterStart)
            ) {
                // Drawer Surface Panel
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(DrawerBg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {} // block clicking through
                        )
                ) {
                    // Header Area with Dark Gradient
                    DrawerHeader(
                        name = userName,
                        phone = userPhone,
                        initials = userInitials,
                        onProfileClick = { onItemClick("Profile") }
                    )

                    // Scrollable Drawer Body
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Quick Actions (3 Cards)
                        QuickActionsRow(onItemClick = onItemClick)

                        // 2. Refer & Earn Promo Card
                        ReferAndEarnCard(onClick = { onItemClick("Refer & Earn") })

                        // 3. Main Navigation Group
                        PrimaryNavigationCard(onItemClick = onItemClick)

                        // 4. Secondary Navigation Group
                        SecondaryNavigationCard(onItemClick = onItemClick)

                        // 5. Log Out Card
                        LogoutCard(onClick = onLogoutClick)

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Floating Close Button outside drawer panel
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(top = 26.dp, start = 8.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    CloseIcon(size = 20.dp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    name: String,
    phone: String,
    initials: String,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(DrawerHeaderDark, DrawerHeaderLight)
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onProfileClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initials Avatar
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = outfitFontFamily(),
                    color = DrawerHeaderDark
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = phone,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = dmSansFontFamily(),
                    color = DrawerTextHeaderSub
                )
            }

            // Right Chevron Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                ChevronRightIcon(size = 18.dp, color = Color.White)
            }
        }
    }
}

@Composable
private fun QuickActionsRow(onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // My Trips
        QuickActionItem(
            modifier = Modifier.weight(1f),
            title = "My Trips",
            icon = { CalendarIcon(size = 20.dp, color = DrawerHeaderDark) },
            onClick = { onItemClick("My Trips") }
        )

        // Profile
        QuickActionItem(
            modifier = Modifier.weight(1f),
            title = "Profile",
            icon = { PersonIcon(size = 20.dp, color = DrawerHeaderDark) },
            onClick = { onItemClick("Profile") }
        )

        // Saved Places
        QuickActionItem(
            modifier = Modifier.weight(1f),
            title = "Saved\nPlaces",
            icon = { HeartIcon(size = 20.dp, color = DrawerHeaderDark) },
            onClick = { onItemClick("Saved Places") }
        )
    }
}

@Composable
private fun QuickActionItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DrawerIconBg),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmSansFontFamily(),
                color = DrawerTextDark,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun ReferAndEarnCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, DrawerOrange, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = DrawerReferCardBg,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gift Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DrawerOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_gift),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // "Refer & Earn" title — shrinks first to give badge space
                    Text(
                        text = "Refer & Earn ₹500",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = DrawerTextDark,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // "NEW" badge — fixed-width, never wraps or shrinks
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(DrawerOrange)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Invite friends and earn rewards",
                    fontSize = 11.sp,
                    fontFamily = dmSansFontFamily(),
                    color = DrawerTextSub
                )
            }

            ArrowRightIcon(size = 18.dp, color = DrawerOrange)
        }
    }
}

@Composable
private fun PrimaryNavigationCard(onItemClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DrawerMenuItem(
                title = "Customer Support",
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_headset),
                        contentDescription = null,
                        tint = DrawerHeaderDark,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = { onItemClick("Customer Support") }
            )
            DrawerDivider()

            DrawerMenuItem(
                title = "Notifications",
                icon = { BellIcon(size = 20.dp, color = DrawerHeaderDark) },
                badgeText = "2",
                onClick = { onItemClick("Notifications") }
            )

            DrawerDivider()

            DrawerMenuItem(
                title = "My Reviews",
                icon = { StarIcon(size = 20.dp, color = DrawerHeaderDark) },
                onClick = { onItemClick("My Reviews") }
            )
            DrawerDivider()

            DrawerMenuItem(
                title = "Refer & Earn",
                icon = { UsersIcon(size = 20.dp, color = DrawerHeaderDark) },
                onClick = { onItemClick("Refer & Earn") }
            )
        }
    }
}

@Composable
private fun SecondaryNavigationCard(onItemClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DrawerMenuItem(
                title = "Settings",
                icon = { SettingsIcon(size = 20.dp, color = DrawerHeaderDark) },
                onClick = { onItemClick("Settings") }
            )
            DrawerDivider()

            DrawerMenuItem(
                title = "About GoIndiaCab",
                icon = { InfoIcon(size = 20.dp, color = DrawerHeaderDark) },
                onClick = { onItemClick("About GoIndiaCab") }
            )
            DrawerDivider()

            DrawerMenuItem(
                title = "Rate Us",
                icon = { ThumbUpIcon(size = 20.dp, color = DrawerHeaderDark) },
                onClick = { onItemClick("Rate Us") }
            )
            DrawerDivider()

            DrawerMenuItem(
                title = "Privacy Policy",
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_shield_check),
                        contentDescription = null,
                        tint = DrawerHeaderDark,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = { onItemClick("Privacy Policy") }
            )
        }
    }
}

@Composable
private fun LogoutCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoutIcon(size = 20.dp, color = DrawerLogoutRed)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Log Out",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = DrawerLogoutRed
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    title: String,
    icon: @Composable () -> Unit,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(22.dp), contentAlignment = Alignment.Center) {
            icon()
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = dmSansFontFamily(),
            color = DrawerTextDark,
            modifier = Modifier.weight(1f)
        )

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(DrawerOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        ChevronRightIcon(size = 16.dp, color = DrawerTextSub)
    }
}

@Composable
private fun DrawerDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(DrawerDividerColor)
    )
}

@Preview
@Composable
fun SideDrawerMenuPreview() {
    GoIndiaCabTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            SideDrawerMenu(
                isOpen = true,
                onDismiss = {}
            )
        }
    }
}
