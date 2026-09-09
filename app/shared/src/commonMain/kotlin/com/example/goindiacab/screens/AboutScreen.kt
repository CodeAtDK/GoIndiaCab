package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.components.ChevronRightIcon
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.viewmodel.AboutUiState
import com.example.goindiacab.viewmodel.AboutViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val DarkNavyHeader = Color(0xFF020C1B)
private val BrandOrange = Color(0xFFFF6D00)
private val CardBorderColor = Color(0xFFE2E8F0)
private val SecondaryTextColor = Color(0xFF64748B)
private val PrimaryTextColor = Color(0xFF0F172A)
private val SoftBackground = Color(0xFFF8FAFC)

/**
 * About GoIndiaCab Screen matching About GoIndiaCab.svg.
 */
@Composable
fun AboutScreen(
    viewModel: AboutViewModel = remember { AboutViewModel() },
    onBackClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLicensesDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            AboutTopBar(
                onBackClick = onBackClick
            )

            // Scrollable Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Brand Logo Emblem
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(DarkNavyHeader),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GIC",
                        color = BrandOrange,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Brand Name & Tagline
                Text(
                    text = uiState.appName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.tagline,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandOrange
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.version,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryTextColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mission & Vision Card
                MissionCard(
                    whoWeAre = uiState.whoWeAre,
                    ourMission = uiState.ourMission
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Key Platform Stats (3-card grid)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBadgeCard(
                        modifier = Modifier.weight(1f),
                        value = uiState.statCities,
                        label = "Cities"
                    )
                    StatBadgeCard(
                        modifier = Modifier.weight(1f),
                        value = uiState.statPartners,
                        label = "Partners"
                    )
                    StatBadgeCard(
                        modifier = Modifier.weight(1f),
                        value = uiState.statRides,
                        label = "Rides"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Legal & Navigation Links Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AboutLinkRow(
                            title = "Terms & Conditions",
                            onClick = {
                                if (onTermsClick != {}) onTermsClick() else showTermsDialog = true
                            }
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        AboutLinkRow(
                            title = "Privacy Policy",
                            onClick = onPrivacyPolicyClick
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        AboutLinkRow(
                            title = "Open Source Licenses",
                            onClick = { showLicensesDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Made with Love in India Footer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Made with ",
                        fontSize = 13.sp,
                        color = SecondaryTextColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "❤️",
                        fontSize = 13.sp
                    )
                    Text(
                        text = " in India",
                        fontSize = 13.sp,
                        color = SecondaryTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Licenses Dialog
    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            title = {
                Text(
                    text = "Open Source Licenses",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryTextColor
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.openSourceLicenses.forEach { lib ->
                        Text(
                            text = "• $lib",
                            fontSize = 13.sp,
                            color = SecondaryTextColor,
                            lineHeight = 18.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicensesDialog = false }) {
                    Text("Close", color = BrandOrange, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Terms & Conditions Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = "Terms & Conditions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryTextColor
                )
            },
            text = {
                Text(
                    text = "GoIndiaCab operates as a licensed intermediary ride hailing platform across India. All bookings adhere to national motor vehicles aggregators guidelines 2020. Free cancellation up to 1 hour before scheduled trip start.",
                    fontSize = 13.sp,
                    color = SecondaryTextColor,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("I Understand", color = BrandOrange, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun AboutTopBar(
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
                text = "About GoIndiaCab",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
            IconButton(onClick = { /* overflow actions */ }) {
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
private fun MissionCard(
    whoWeAre: String,
    ourMission: String
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
                .padding(20.dp)
        ) {
            Text(
                text = "WHO WE ARE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = whoWeAre,
                fontSize = 13.5.sp,
                color = SecondaryTextColor,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "OUR MISSION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ourMission,
                fontSize = 13.5.sp,
                color = SecondaryTextColor,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun StatBadgeCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = BrandOrange
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryTextColor
            )
        }
    }
}

@Composable
private fun AboutLinkRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryTextColor
        )
        ChevronRightIcon(color = Color(0xFF94A3B8), size = 16.dp)
    }
}

/**
 * Standalone Preview for AboutScreen.
 */
@Composable
@Preview
fun AboutScreenPreview() {
    GoIndiaCabTheme {
        AboutScreen()
    }
}
