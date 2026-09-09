package com.example.goindiacab.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
 * Screen: Help - Trip Issues & Lost Item Recovery.
 * Allows riders to resolve ride-related disputes, report lost belongings,
 * and get assistance on past/active trips.
 */
@Composable
fun HelpTripIssuesScreen(
    onBackClick: () -> Unit = {},
    onRaiseTicketClick: (category: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()
    var selectedTripId by remember { mutableStateOf("TRIP-101") }
    var selectedItemType by remember { mutableStateOf("Mobile Phone") }
    var itemDescription by remember { mutableStateOf("") }
    var showLostSuccessDialog by remember { mutableStateOf(false) }

    val commonIssues = remember {
        listOf(
            "Driver arrived late or cancelled",
            "Driver demanded extra cash for tolls",
            "Vehicle AC was not functioning",
            "Driver took an unapproved route",
            "Vehicle was dirty or had luggage issues"
        )
    }

    if (showLostSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showLostSuccessDialog = false },
            title = {
                Text(
                    text = "Lost Item Report Submitted",
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )
            },
            text = {
                Text(
                    text = "Case #LOST-7492 has been opened for $selectedItemType. Driver Rajesh Kumar (+91 98765 43210) has been notified to check the vehicle. Our agent will call you within 15 minutes.",
                    fontFamily = dmSansFontFamily(),
                    color = TextDark,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showLostSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
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
                        text = "Trip Issues & Lost Items",
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
            // Section 1: Lost & Found Item Report Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.5f)),
                shadowElevation = 2.dp
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎒", fontSize = 16.sp)
                            }

                            Text(
                                text = "Lost an Item in Cab?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFF7ED)
                        ) {
                            Text(
                                text = "PRIORITY",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandOrange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "Forgot a bag, wallet, or phone during your journey? Report it below and we will contact the driver partner immediately.",
                        fontSize = 12.5.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextMuted
                    )

                    // Trip Selector
                    Text("Select Trip:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("TRIP-101 (Jaipur)", "TRIP-102 (Agra)").forEach { tripChoice ->
                            val isSelected = selectedTripId in tripChoice
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTripId = tripChoice.take(8) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFFFF7ED) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSelected) BrandOrange else Color.Transparent)
                            ) {
                                Text(
                                    text = tripChoice,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BrandOrange else TextDark,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                                )
                            }
                        }
                    }

                    // Item Category Chips
                    Text("Item Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Mobile Phone", "Wallet / Purse", "Backpack / Luggage", "Keys", "Other").forEach { itemCat ->
                            val isSelected = selectedItemType == itemCat
                            Surface(
                                modifier = Modifier.clickable { selectedItemType = itemCat },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) BrandOrange else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = itemCat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Description text input
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .border(1.dp, CardBorderColor, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFAFAFA)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            if (itemDescription.isEmpty()) {
                                Text(
                                    text = "Describe the item (color, brand, where left in car)...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF),
                                    fontFamily = dmSansFontFamily()
                                )
                            }
                            BasicTextField(
                                value = itemDescription,
                                onValueChange = { itemDescription = it },
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextDark
                                ),
                                cursorBrush = SolidColor(BrandOrange),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (itemDescription.isBlank()) {
                                toast("Please enter a brief description of the lost item")
                            } else {
                                showLostSuccessDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Text("Submit Lost Item Claim", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                }
            }

            // Section 2: Common Trip Issues
            Text(
                text = "COMMON TRIP DISPUTES",
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    commonIssues.forEachIndexed { index, issue ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRaiseTicketClick(issue)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = issue,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = dmSansFontFamily(),
                                color = TextDark,
                                modifier = Modifier.weight(1f)
                            )
                            ChevronRightIcon(size = 14.dp, color = Color(0xFF94A3B8))
                        }

                        if (index < commonIssues.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
