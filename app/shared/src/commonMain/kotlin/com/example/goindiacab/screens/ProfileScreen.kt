package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Styling tokens matching profile-screen.svg
private val ProfileHeaderOrange = Color(0xFFFF6B00)
private val ProfileDarkPill = Color(0xFF020C1B)
private val ProfileCardBg = Color.White
private val ProfileScreenBg = Color(0xFFF4F6F9)
private val ProfileTextDark = Color(0xFF111827)
private val ProfileTextSub = Color(0xFF4B5563)
private val ProfileDivider = Color(0xFFF0F2F5)
private val ProfileLogoutRed = Color(0xFFEF4444)
private val ProfileIconBoxBg = Color(0xFFF4F6F9)

@Composable
fun ProfileScreen(
    userName: String = "Rahul Sharma",
    userEmail: String = "rahul.sharma@example.com",
    userPhone: String = "+91 9876543210",
    userInitials: String = "RS",
    onEditProfileClick: () -> Unit = {},
    onMyBookingsClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onOffersCouponsClick: () -> Unit = {},
    onReferEarnClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onEmergencyContactsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onTripsTabClick: () -> Unit = {},
    onOffersTabClick: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Log Out",
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = ProfileTextDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out of GoIndiaCab?",
                    fontFamily = dmSansFontFamily(),
                    color = ProfileTextSub
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileLogoutRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = ProfileTextSub, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = ProfileScreenBg,
        bottomBar = {
            ProfileBottomNavigationBar(
                activeTab = "Profile",
                onHomeClick = onHomeTabClick,
                onTripsClick = onTripsTabClick,
                onOffersClick = onOffersTabClick,
                onProfileClick = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with Orange Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ProfileHeaderOrange)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Profile Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Initials Avatar
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color.Black, CircleShape)
                                .background(ProfileHeaderOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userInitials,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = outfitFontFamily(),
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // User Details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = outfitFontFamily(),
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$userEmail • $userPhone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = dmSansFontFamily(),
                                color = Color.Black.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Edit Profile Pill Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = ProfileDarkPill,
                        onClick = onEditProfileClick
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditPencilIcon(size = 16.dp, color = ProfileHeaderOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Edit Profile",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = ProfileHeaderOrange
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Menu Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(22.dp),
                color = ProfileCardBg,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileMenuItem(
                        title = "My Bookings",
                        icon = { CalendarIcon(size = 18.dp, color = ProfileTextDark) },
                        onClick = onMyBookingsClick
                    )
                    ProfileDividerLine()

                    ProfileMenuItem(
                        title = "Location",
                        subtitle = "Home, Office",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_location_pin),
                                contentDescription = null,
                                tint = ProfileTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onLocationClick
                    )
                    ProfileDividerLine()

                    ProfileMenuItem(
                        title = "GoIndiaCab Wallet",
                        subtitle = "₹500.00 Balance",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_wallet_card),
                                contentDescription = null,
                                tint = ProfileTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onWalletClick
                    )
                    ProfileDividerLine()

                    ProfileMenuItem(
                        title = "Emergency SOS Contacts",
                        subtitle = "Trusted Safety Shield",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_shield_check),
                                contentDescription = null,
                                tint = ProfileTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onEmergencyContactsClick
                    )
                    ProfileDividerLine()

                    ProfileMenuItem(
                        title = "Offers & Coupons",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_discount_tag),
                                contentDescription = null,
                                tint = ProfileTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onOffersCouponsClick
                    )
                    ProfileDividerLine()

                    ProfileMenuItem(
                        title = "Refer & Earn",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_gift),
                                contentDescription = null,
                                tint = ProfileTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onReferEarnClick
                    )
                    ProfileDividerLine()

                    ProfileMenuItem(
                        title = "Settings",
                        icon = { SettingsIcon(size = 18.dp, color = ProfileTextDark) },
                        onClick = onSettingsClick
                    )
                    ProfileDividerLine()

                    ProfileMenuItem(
                        title = "Help & Support",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_headset),
                                contentDescription = null,
                                tint = ProfileTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = onHelpSupportClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Log Out Outlined Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
                    .border(1.5.dp, ProfileLogoutRed, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                onClick = { showLogoutDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LogoutIcon(size = 18.dp, color = ProfileLogoutRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = ProfileLogoutRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    subtitle: String? = null,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon in light container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ProfileIconBoxBg),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and optional Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmSansFontFamily(),
                color = ProfileTextDark
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontFamily = dmSansFontFamily(),
                    color = ProfileTextSub
                )
            }
        }

        ChevronRightIcon(size = 16.dp, color = Color(0xFF9CA3AF))
    }
}

@Composable
private fun ProfileDividerLine() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = ProfileDivider
    )
}

@Composable
fun ProfileBottomNavigationBar(
    activeTab: String,
    onHomeClick: () -> Unit,
    onTripsClick: () -> Unit,
    onOffersClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Home",
                isSelected = activeTab == "Home",
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_nav_home),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (activeTab == "Home") ProfileHeaderOrange else Color(0xFF9CA3AF)
                    )
                },
                onClick = onHomeClick
            )

            BottomNavItem(
                label = "My Trips",
                isSelected = activeTab == "My Trips",
                icon = {
                    CalendarIcon(
                        size = 20.dp,
                        color = if (activeTab == "My Trips") ProfileHeaderOrange else Color(0xFF9CA3AF)
                    )
                },
                onClick = onTripsClick
            )

            BottomNavItem(
                label = "Offers",
                isSelected = activeTab == "Offers",
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_discount_tag),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (activeTab == "Offers") ProfileHeaderOrange else Color(0xFF9CA3AF)
                    )
                },
                onClick = onOffersClick
            )

            BottomNavItem(
                label = "Profile",
                isSelected = activeTab == "Profile",
                icon = {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (activeTab == "Profile") ProfileHeaderOrange else Color(0xFF9CA3AF)),
                        contentAlignment = Alignment.Center
                    ) {
                        PersonIcon(size = 16.dp, color = Color.White)
                    }
                },
                onClick = onProfileClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = dmSansFontFamily(),
            color = if (isSelected) ProfileHeaderOrange else Color(0xFF6B7280)
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    GoIndiaCabTheme {
        ProfileScreen()
    }
}
