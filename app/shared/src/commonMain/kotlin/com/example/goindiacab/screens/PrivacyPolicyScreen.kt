package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.theme.GoIndiaCabTheme
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val DarkNavyHeader = Color(0xFF020C1B)
private val BrandOrange = Color(0xFFFF6D00)
private val CardBorderColor = Color(0xFFE2E8F0)
private val SecondaryTextColor = Color(0xFF64748B)
private val PrimaryTextColor = Color(0xFF0F172A)
private val SoftBackground = Color(0xFFF8FAFC)

private data class PolicySection(
    val title: String,
    val description: String
)

/**
 * Privacy Policy Screen matching Privacy Policy.svg.
 */
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit = {},
    onContactOfficerClick: () -> Unit = {}
) {
    var showOfficerDialog by remember { mutableStateOf(false) }

    val policySections = remember {
        listOf(
            PolicySection(
                title = "1. Information We Collect",
                description = "We collect information you provide directly, such as your profile metrics, contact configurations, travel bookings, and specific GPS geolocation parameters essential to routing partners successfully."
            ),
            PolicySection(
                title = "2. How We Use Your Information",
                description = "To optimize and supply reliable travel networks, secure electronic transactional handshakes, confirm identity markers, and generate appropriate safety profiles for shared driver routes."
            ),
            PolicySection(
                title = "3. Data Sharing Policies",
                description = "Your location metrics are only distributed to drivers assigned to active requests. We do not sell passenger contact details to advertising bureaus."
            ),
            PolicySection(
                title = "4. Your Privacy Rights",
                description = "Under Indian digital protection framework laws, you hold the right to revoke GPS parameters, clear transactional history records, or ask for permanent removal of GoIndiaCab profiles."
            ),
            PolicySection(
                title = "5. Customer Protection & Contact",
                description = "For regulatory disputes or data security reports, contact our dedicated compliance officer directly at security@goindiacab.com or utilize the hotline."
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            PrivacyPolicyTopBar(onBackClick = onBackClick)

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Intro Subtitle
                Text(
                    text = "Last updated: October 2024. Your data protection matters to us. Please read through our revised privacy directives below.",
                    fontSize = 13.5.sp,
                    color = SecondaryTextColor,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // 5 Policy Cards
                policySections.forEachIndexed { index, section ->
                    PolicyCard(
                        title = section.title,
                        description = section.description,
                        isLastCard = index == policySections.lastIndex,
                        onContactClick = {
                            if (onContactOfficerClick != {}) onContactOfficerClick()
                            showOfficerDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showOfficerDialog) {
        AlertDialog(
            onDismissRequest = { showOfficerDialog = false },
            title = {
                Text(
                    text = "Data Protection Officer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryTextColor
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Email: security@goindiacab.com",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = BrandOrange
                    )
                    Text(
                        text = "Phone: 1800-419-0199 (Mon-Sat, 9AM-6PM IST)",
                        fontSize = 13.sp,
                        color = SecondaryTextColor
                    )
                    Text(
                        text = "Office: GoIndiaCab Data Grievance Cell, Cyber City, Gurugram, Haryana - 122002",
                        fontSize = 13.sp,
                        color = SecondaryTextColor,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showOfficerDialog = false }) {
                    Text("Done", color = BrandOrange, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun PrivacyPolicyTopBar(
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
                text = "Privacy Policy",
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
private fun PolicyCard(
    title: String,
    description: String,
    isLastCard: Boolean = false,
    onContactClick: () -> Unit = {}
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
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 13.5.sp,
                color = SecondaryTextColor,
                lineHeight = 21.sp
            )

            if (isLastCard) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Contact Data Protection Officer →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandOrange,
                    modifier = Modifier.clickable(onClick = onContactClick)
                )
            }
        }
    }
}

/**
 * Standalone Preview for PrivacyPolicyScreen.
 */
@Composable
@Preview
fun PrivacyPolicyScreenPreview() {
    GoIndiaCabTheme {
        PrivacyPolicyScreen()
    }
}
