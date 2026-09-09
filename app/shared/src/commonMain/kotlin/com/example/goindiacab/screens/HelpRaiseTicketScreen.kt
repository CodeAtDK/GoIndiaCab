package com.example.goindiacab.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
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

/**
 * Screen: Raise Support Ticket.
 * Form to file a formal customer service query or dispute with SLA guarantee.
 */
@Composable
fun HelpRaiseTicketScreen(
    initialCategory: String = "Trip Experience",
    onBackClick: () -> Unit = {},
    onTicketSubmitted: (ticketId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()

    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedTrip by remember { mutableStateOf("TRIP-101 (Delhi ➔ Jaipur)") }
    var selectedPriority by remember { mutableStateOf("Normal") }
    var subjectText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var isAttachingFile by remember { mutableStateOf(false) }

    val categories = listOf("Safety Issue", "Trip Experience", "Payment & Refund", "App Feedback")
    val tripOptions = listOf("TRIP-101 (Delhi ➔ Jaipur)", "TRIP-102 (Delhi ➔ Agra)", "General Inquiry")
    val priorities = listOf("Normal", "High", "Urgent")

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
                        text = "Raise Support Ticket",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = {
                            if (subjectText.isBlank()) {
                                toast("Please enter a subject for your ticket")
                            } else if (descriptionText.isBlank()) {
                                toast("Please describe your issue in detail")
                            } else {
                                val generatedId = "GIC-${(1000..9999).random()}"
                                toast("✓ Ticket #$generatedId created! Support team assigned.")
                                onTicketSubmitted(generatedId)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Submit Support Ticket",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )
                            ChevronRightIcon(size = 14.dp, color = Color.White)
                        }
                    }
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
            // SLA Notice Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⏱️", fontSize = 16.sp)
                    Text(
                        text = "Average response time: 15 minutes. Our team operates 24x7 across all major highway routes.",
                        fontSize = 12.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color(0xFF1E40AF)
                    )
                }
            }

            // Category Selection
            Text(
                text = "ISSUE CATEGORY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = cat },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) BrandOrange else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) BrandOrange else CardBorderColor)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = dmSansFontFamily(),
                            color = if (isSelected) Color.White else TextDark,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Related Trip Selection
            Text(
                text = "RELATED TRIP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tripOptions.forEach { tripChoice ->
                    val isSelected = selectedTrip == tripChoice
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTrip = tripChoice },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFFFF7ED) else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) BrandOrange else CardBorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = tripChoice,
                                fontSize = 13.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandOrange else TextDark
                            )
                            if (isSelected) {
                                Text("✓", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
                            }
                        }
                    }
                }
            }

            // Subject Line Input
            Text(
                text = "SUBJECT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (subjectText.isEmpty()) {
                        Text(
                            text = "Brief summary of the issue...",
                            fontSize = 13.5.sp,
                            color = Color(0xFF9CA3AF),
                            fontFamily = dmSansFontFamily()
                        )
                    }
                    BasicTextField(
                        value = subjectText,
                        onValueChange = { subjectText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.5.sp,
                            fontFamily = dmSansFontFamily(),
                            color = TextDark,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(BrandOrange),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Detailed Description Input
            Text(
                text = "DESCRIPTION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    if (descriptionText.isEmpty()) {
                        Text(
                            text = "Please explain what happened, including any relevant times, driver statements, or toll locations...",
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF),
                            fontFamily = dmSansFontFamily(),
                            lineHeight = 18.sp
                        )
                    }
                    BasicTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = dmSansFontFamily(),
                            color = TextDark
                        ),
                        cursorBrush = SolidColor(BrandOrange),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Priority Level
            Text(
                text = "PRIORITY LEVEL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                priorities.forEach { pr ->
                    val isSelected = selectedPriority == pr
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPriority = pr },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFFFFF7ED) else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) BrandOrange else CardBorderColor)
                    ) {
                        Text(
                            text = pr,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandOrange else TextDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
