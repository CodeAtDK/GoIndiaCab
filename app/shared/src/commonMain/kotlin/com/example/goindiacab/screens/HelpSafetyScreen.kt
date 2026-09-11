package com.example.goindiacab.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val TopBarNavy = Color(0xFF0A1128)
private val ColorBg = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val SafetyRed = Color(0xFFEF4444)
private val SafetyRedBg = Color(0xFFFCE8E6)

/**
 * Screen: Help - Safety & Security.
 * Displays rider safety features, emergency SOS protocols, 
 * live tracking guidelines, and incident reporting.
 */
@Composable
fun HelpSafetyScreen(
    onBackClick: () -> Unit = {},
    onCallSosClick: () -> Unit = {},
    onReportIncidentClick: () -> Unit = {},
    onManageEmergencyContactsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()
    var expandedFaqId by remember { mutableStateOf<String?>("safety_faq_1") }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedConcern by remember { mutableStateOf("Rash Driving") }

    val safetyFaqs = remember {
        listOf(
            FaqItem(
                id = "safety_faq_1",
                question = "How do I share my live trip location with family?",
                answer = "During any active ride, open the Ongoing Trip screen and tap 'Share Live Location'. You can send a live tracking link via WhatsApp, SMS, or Telegram allowing family members to watch your route in real time."
            ),
            FaqItem(
                id = "safety_faq_2",
                question = "How are GoIndiaCab drivers verified?",
                answer = "Every driver partner undergoes comprehensive criminal background checks, commercial driving license verification, address verification, and in-person vehicle safety fitness inspections."
            ),
            FaqItem(
                id = "safety_faq_3",
                question = "What should I do in case of an emergency?",
                answer = "Press the SOS button in the app to instantly alert our 24x7 Rapid Response Team and trigger emergency dispatch to police helpline 112 with your live vehicle GPS coordinates."
            ),
            FaqItem(
                id = "safety_faq_4",
                question = "Are night trips safe for solo women travelers?",
                answer = "Yes. Night trips feature active route deviation monitoring. If a vehicle stops unexpectedly or deviates from the optimal highway route, our safety operations team automatically contacts the driver and rider."
            )
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = {
                Text(
                    text = "Report Safety Incident",
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = SafetyRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select the nature of your concern. Our safety manager will call you within 5 minutes.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        fontFamily = dmSansFontFamily()
                    )

                    listOf("Rash Driving", "Unprofessional Behavior", "Route Deviation", "Overcharging in Cash", "Vehicle Issue").forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedConcern = option }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedConcern == option,
                                onClick = { selectedConcern = option },
                                colors = RadioButtonDefaults.colors(selectedColor = SafetyRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = option, fontSize = 14.sp, color = TextDark, fontFamily = dmSansFontFamily())
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        toast("✓ Incident filed: $selectedConcern. Safety team is calling you shortly.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit & Request Call", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ColorBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TopBarNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
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
                        text = "Safety & Security",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
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
            // Emergency SOS Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SafetyRedBg,
                border = BorderStroke(1.5.dp, SafetyRed)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SafetyRed)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "SOS 24x7",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = outfitFontFamily(),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Emergency Response",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = SafetyRed
                            )
                        }
                    }

                    Text(
                        text = "Need urgent help on the road? Tap below to connect instantly with police emergency dispatch and GoIndiaCab safety desk.",
                        fontSize = 13.sp,
                        color = TextDark,
                        fontFamily = dmSansFontFamily(),
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onCallSosClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyRed)
                        ) {
                            Text(
                                text = "Call Helpline (112)",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, SafetyRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SafetyRed)
                        ) {
                            Text(
                                text = "Report Issue",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SafetyRed
                            )
                        }
                    }

                    // Manage Trusted Contacts Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onManageEmergencyContactsClick),
                        color = Color.White,
                        border = BorderStroke(1.dp, SafetyRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "🛡️", fontSize = 14.sp)
                                Text(
                                    text = "Manage Trusted Emergency Contacts",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = SafetyRed
                                )
                            }
                            ChevronRightIcon(size = 16.dp, color = SafetyRed)
                        }
                    }
                }
            }

            // Safety Highlights Grid
            Text(
                text = "YOUR SAFETY SHIELD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SafetyFeatureRow(
                        title = "100% Police Verified Drivers",
                        subtitle = "Commercial licenses, background checks, and identity clearance."
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    SafetyFeatureRow(
                        title = "24×7 GPS Route Tracking",
                        subtitle = "Continuous satellite tracking with deviation and halt detection."
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    SafetyFeatureRow(
                        title = "Share Live Trip with Family",
                        subtitle = "One-tap WhatsApp live sharing with estimated time of arrival."
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    SafetyFeatureRow(
                        title = "Commercial Fleet Insurance",
                        subtitle = "Every passenger is covered under commercial transit insurance."
                    )
                }
            }

            // Safety FAQs Section
            Text(
                text = "SAFETY FAQS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                safetyFaqs.forEach { faq ->
                    val isExpanded = expandedFaqId == faq.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFaqId = if (isExpanded) null else faq.id },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, CardBorderColor)
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
                                    text = faq.question,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (isExpanded) "▲" else "▼",
                                    fontSize = 12.sp,
                                    color = BrandOrange
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Text(
                                    text = faq.answer,
                                    fontSize = 13.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextMuted,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SafetyFeatureRow(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFECFDF5)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✓", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted
            )
        }
    }
}
