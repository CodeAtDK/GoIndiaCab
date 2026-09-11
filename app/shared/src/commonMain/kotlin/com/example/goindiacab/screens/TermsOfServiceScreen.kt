package com.example.goindiacab.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val NavyDark = Color(0xFF0A1A3A)
private val ScreenBg = Color(0xFFF4F6F9)
private val BrandOrange = Color(0xFFFF6B00)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

data class TermsSection(
    val id: String,
    val title: String,
    val content: String
)

/**
 * Screen: Terms of Service & User Agreement.
 * Provides comprehensive legal terms matching PrivacyPolicyScreen design language.
 */
@Composable
fun TermsOfServiceScreen(
    onBackClick: () -> Unit = {},
    onContactLegalClick: () -> Unit = {}
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    var expandedSectionId by remember { mutableStateOf<String?>("terms_sec_1") }

    val sections = remember {
        listOf(
            TermsSection(
                id = "terms_sec_1",
                title = "1. Introduction & Aggregator Role",
                content = "GoIndiaCab operates as a licensed digital transport aggregator connecting passengers with certified commercial fleet operators and independent commercial driver partners across India. All bookings and operations comply with the Motor Vehicles Aggregators Guidelines 2020 issued by the Ministry of Road Transport and Highways (MoRTH)."
            ),
            TermsSection(
                id = "terms_sec_2",
                title = "2. Milestone Advance Payment Policy",
                content = "To protect both riders and driver partners on long-distance outstation routes, bookings require milestone settlements. Typically, an initial 10%-20% advance is paid via online payment modes (UPI, Card, NetBanking) to confirm booking and reserve the vehicle. Subsequent milestone installments (e.g. 40% at trip pickup and remaining 50% at final destination) are settled as the trip progresses."
            ),
            TermsSection(
                id = "terms_sec_3",
                title = "3. Tolls, State Taxes & Highway Fees",
                content = "Unless explicitly mentioned as an excluded line item in special customized bookings, all standard highway FASTag tolls, interstate passenger entry taxes, and state border permit fees are calculated and bundled into your all-inclusive upfront estimated fare. Passengers are not required to pay extra cash to the driver partner at toll plazas for standard routes."
            ),
            TermsSection(
                id = "terms_sec_4",
                title = "4. Vehicle Standards, AC & Luggage",
                content = "All commercial cabs provided under Hatchback, Sedan, SUV, and Crysta tiers must maintain air conditioning throughout the trip (except on steep hill sections where safety demands AC cutoff). Seating capacity (4 seats for Sedan, 6-7 seats for SUV) and baggage limits (2 medium bags for Sedan, 4 bags for SUV) must be adhered to."
            ),
            TermsSection(
                id = "terms_sec_5",
                title = "5. Cancellation & Refund Guidelines",
                content = "Cancellations made up to 2 hours prior to the scheduled pickup time qualify for a 100% full refund of the advance deposit. In cases where no driver partner is available during radar search or the driver cancels due to technical issues, an immediate automatic 100% refund is initiated back to your original payment method within 12-24 banking hours."
            ),
            TermsSection(
                id = "terms_sec_6",
                title = "6. Rider Conduct & Safety Protocols",
                content = "GoIndiaCab enforces zero tolerance towards harassment, alcohol or narcotics consumption, carrying hazardous contraband, or dangerous conduct inside the vehicle. The 24x7 SOS emergency button instantly alerts law enforcement (Helpline 112) and our rapid response team with live vehicle GPS telemetry."
            )
        )
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        ChevronRightIcon(
                            size = 18.dp,
                            color = Color.White,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Terms of Service",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "GoIndiaCab User Agreement",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextDark
                    )

                    Text(
                        text = "Effective Date: September 2026 • Version 2.4.1\nPlease read these terms carefully before booking any intercity or local cab service.",
                        fontSize = 12.5.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
            }

            // Accordion Sections
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sections.forEach { section ->
                    val isExpanded = expandedSectionId == section.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedSectionId = if (isExpanded) null else section.id },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = section.title,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = if (isExpanded) "▲" else "▼",
                                    fontSize = 11.sp,
                                    color = BrandOrange
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Text(
                                    text = section.content,
                                    fontSize = 13.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextMuted,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }
            }

            // Contact Legal Desk
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Questions regarding our policies?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextDark
                        )
                        Text(
                            text = "Contact legal@goindiacab.com",
                            fontSize = 12.sp,
                            fontFamily = dmSansFontFamily(),
                            color = TextMuted
                        )
                    }

                    TextButton(onClick = onContactLegalClick) {
                        Text("Contact", color = BrandOrange, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun TermsOfServiceScreenPreview() {
    GoIndiaCabTheme {
        TermsOfServiceScreen()
    }
}
