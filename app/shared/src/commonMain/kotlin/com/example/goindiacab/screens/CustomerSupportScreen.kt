package com.example.goindiacab.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Styling tokens matching customer-support.svg
private val SupportBg = Color(0xFFF4F6F9)
private val SupportCardBg = Color.White
private val SupportOrange = Color(0xFFFF6B00)
private val SupportSosRed = Color(0xFFEF4444)
private val SupportSosBg = Color(0xFFFCE8E6)
private val SupportCategoryIconBg = Color(0xFFE6F0FF)
private val SupportCategoryIconTint = Color(0xFF0052CC)
private val SupportTextDark = Color(0xFF111827)
private val SupportTextSub = Color(0xFF4B5563)
private val SupportBorder = Color(0xFFE5E7EB)
private val SupportDivider = Color(0xFFF0F2F5)

data class FaqItem(
    val id: String,
    val question: String,
    val answer: String
)

@Composable
fun CustomerSupportScreen(
    onBackClick: () -> Unit = {},
    onCallSupportClick: () -> Unit = {},
    onEmailSupportClick: () -> Unit = {},
    onSosClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedFaqId by remember { mutableStateOf<String?>("faq_1") }
    var showSosDialog by remember { mutableStateOf(false) }

    val allFaqs = remember {
        listOf(
            FaqItem(
                id = "faq_1",
                question = "How do I cancel my outstation booking?",
                answer = "You can cancel your booking up to 2 hours prior to scheduled pickup for a 100% full refund. Simply go to My Trips, select your active ride, and tap 'Cancel Booking'."
            ),
            FaqItem(
                id = "faq_2",
                question = "What are the charges for highway tolls?",
                answer = "All state highway and national toll taxes are already included in your upfront estimated fare. You do not need to pay anything extra in cash to the driver partner at toll plazas."
            ),
            FaqItem(
                id = "faq_3",
                question = "Can I change my drop location mid-trip?",
                answer = "Yes, you can request an updated route or change destination mid-trip with your driver. Additional distance beyond the booked package is billed as per the vehicle's standard per-km rate."
            ),
            FaqItem(
                id = "faq_4",
                question = "How does the milestone payment work?",
                answer = "We offer a 20%-40%-40% milestone schedule: pay 20% advance to lock your driver, 40% when the cab arrives at pickup, and the remaining 40% at trip destination."
            )
        )
    }

    val filteredFaqs = remember(searchQuery) {
        if (searchQuery.isBlank()) allFaqs
        else allFaqs.filter {
            it.question.contains(searchQuery, ignoreCase = true) ||
                    it.answer.contains(searchQuery, ignoreCase = true)
        }
    }

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = {
                Text(
                    text = "Emergency 24x7 Safety Desk",
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = SupportSosRed
                )
            },
            text = {
                Text(
                    text = "Are you facing an emergency? Tap 'Call Helpline' to immediately connect with the GoIndiaCab Rapid Response safety team and local police dispatch.",
                    fontFamily = dmSansFontFamily(),
                    color = SupportTextDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSosDialog = false
                        onSosClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SupportSosRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Call Helpline (112)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text("Cancel", color = SupportTextSub)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = SupportBg,
        topBar = {
            Surface(
                color = SupportBg,
                shadowElevation = 0.dp
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back_arrow),
                            contentDescription = "Back",
                            tint = SupportTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Help & Support",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = SupportTextDark,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { /* Overflow options */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_more_vert),
                            contentDescription = "More Options",
                            tint = SupportTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Call Support Button
                    Button(
                        onClick = onCallSupportClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SupportOrange),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PhoneCallIcon(size = 18.dp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Call Support",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )
                        }
                    }

                    // Email Us Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(1.5.dp, SupportBorder, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        onClick = onEmailSupportClick
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_social_mail),
                                contentDescription = null,
                                tint = SupportTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Email Us",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = SupportTextDark
                            )
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emergency 24x7 Safety Help Banner matching customer-support.svg
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SupportSosRed, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = SupportSosBg,
                onClick = { showSosDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SupportSosRed)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Emergency 24×7 Safety Help – Press to contact state helpline & security desk",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = dmSansFontFamily(),
                        color = SupportSosRed,
                        lineHeight = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.dp, SupportBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search_lens),
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search issues, bookings or payments",
                                fontSize = 14.sp,
                                fontFamily = dmSansFontFamily(),
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = dmSansFontFamily(),
                                color = SupportTextDark,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(SupportOrange),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable { searchQuery = "" },
                            contentAlignment = Alignment.Center
                        ) {
                            CloseIcon(size = 14.dp, color = Color(0xFF6B7280))
                        }
                    }
                }
            }

            // 2x2 Category Quick Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SupportCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Safety Issue",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_shield_check),
                                contentDescription = null,
                                tint = SupportCategoryIconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        onClick = { onCategoryClick("Safety Issue") }
                    )

                    SupportCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Trip Issues",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_info_circle),
                                contentDescription = null,
                                tint = SupportCategoryIconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        onClick = { onCategoryClick("Trip Issues") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SupportCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Payments",
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_wallet_card),
                                contentDescription = null,
                                tint = SupportCategoryIconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        onClick = { onCategoryClick("Payments") }
                    )

                    SupportCategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "App Feedback",
                        icon = {
                            ThumbUpIcon(size = 20.dp, color = SupportCategoryIconTint)
                        },
                        onClick = { onCategoryClick("App Feedback") }
                    )
                }
            }

            // Section Header: Frequently Asked Questions
            Text(
                text = "Frequently Asked Questions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = SupportTextDark,
                modifier = Modifier.padding(top = 4.dp)
            )

            // FAQ Accordion Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SupportCardBg,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    filteredFaqs.forEachIndexed { index, faq ->
                        val isExpanded = expandedFaqId == faq.id
                        SupportFaqRow(
                            faq = faq,
                            isExpanded = isExpanded,
                            onToggle = {
                                expandedFaqId = if (isExpanded) null else faq.id
                            }
                        )
                        if (index < filteredFaqs.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 1.dp,
                                color = SupportDivider
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SupportCategoryCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(16.dp),
        color = SupportCardBg,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SupportCategoryIconBg),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmSansFontFamily(),
                color = SupportTextDark
            )
        }
    }
}

@Composable
private fun SupportFaqRow(
    faq: FaqItem,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = faq.question,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = dmSansFontFamily(),
                color = SupportTextDark,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (isExpanded) {
                MinusIcon(size = 18.dp, color = SupportTextDark)
            } else {
                PlusIcon(size = 18.dp, color = SupportTextDark)
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    fontSize = 13.sp,
                    fontFamily = dmSansFontFamily(),
                    color = SupportTextSub,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun CustomerSupportScreenPreview() {
    GoIndiaCabTheme {
        CustomerSupportScreen()
    }
}
