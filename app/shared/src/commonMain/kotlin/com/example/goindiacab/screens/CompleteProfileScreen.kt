package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val NavyDark = Color(0xFF0A1A3A)
private val ScreenBg = Color(0xFFF4F6F9)
private val BrandBlue = Color(0xFF0052CC)
private val BrandOrange = Color(0xFFFF6B00)
private val SuccessGreen = Color(0xFF10B981)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

/**
 * Post-OTP Registration & Profile Setup Screen for new users.
 * Collects full name, email, city, and optional referral code.
 */
@Composable
fun CompleteProfileScreen(
    phoneNumber: String = "+91 98765 43210",
    onBackClick: () -> Unit = {},
    onProfileCompleted: (name: String, email: String, city: String, referralCode: String) -> Unit = { _, _, _, _ -> },
    onSkipClick: () -> Unit = {}
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Delhi NCR") }
    var referralCode by remember { mutableStateOf("") }
    var isExpandedCityDropdown by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val isValidName = fullName.trim().length >= 2
    val cities = listOf("Delhi NCR", "Jaipur", "Agra", "Chandigarh", "Mumbai", "Pune", "Bengaluru")

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
                        text = "Complete Your Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onSkipClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Avatar Placeholder with Edit Icon
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(BrandBlue.copy(alpha = 0.10f))
                    .border(2.dp, BrandBlue.copy(alpha = 0.30f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                PersonIcon(
                    size = 40.dp,
                    color = BrandBlue
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(BrandOrange)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "+", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to GoIndiaCab!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Please tell us your name so driver partners can identify you on your trips.",
                fontSize = 13.5.sp,
                fontFamily = dmSansFontFamily(),
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Mobile Number (Read-only verified badge)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "MOBILE NUMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = phoneNumber,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "✓", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "Verified",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = dmSansFontFamily(),
                                    color = SuccessGreen
                                )
                            }
                        }
                    }

                    // 2. Full Name Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "FULL NAME *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = { Text("e.g. Rahul Sharma", color = Color(0xFFA0AEC0), fontSize = 14.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlue,
                                unfocusedBorderColor = BorderColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // 3. Email Address Input (Optional)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "EMAIL ADDRESS (OPTIONAL FOR GST INVOICE)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("e.g. rahul@example.com", color = Color(0xFFA0AEC0), fontSize = 14.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlue,
                                unfocusedBorderColor = BorderColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // 4. Primary City Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "PRIMARY CITY / HUB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                    .clickable { isExpandedCityDropdown = true }
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = city,
                                    fontSize = 14.5.sp,
                                    fontFamily = outfitFontFamily(),
                                    fontWeight = FontWeight.Medium,
                                    color = TextDark
                                )

                                Text(
                                    text = "▼",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            DropdownMenu(
                                expanded = isExpandedCityDropdown,
                                onDismissRequest = { isExpandedCityDropdown = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                cities.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c, fontFamily = dmSansFontFamily(), fontSize = 14.sp) },
                                        onClick = {
                                            city = c
                                            isExpandedCityDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 5. Referral Code Input (Optional)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "REFERRAL CODE (OPTIONAL)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = referralCode,
                            onValueChange = { referralCode = it.uppercase().take(12) },
                            placeholder = { Text("e.g. DHRUVA500", color = Color(0xFFA0AEC0), fontSize = 14.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (isValidName) {
                                        onProfileCompleted(fullName, email, city, referralCode)
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandBlue,
                                unfocusedBorderColor = BorderColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onProfileCompleted(
                                fullName.trim().ifEmpty { "Rider" },
                                email.trim(),
                                city,
                                referralCode.trim()
                            )
                        },
                        enabled = isValidName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Text(
                            text = "Complete & Start Exploring",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Preview
@Composable
fun CompleteProfileScreenPreview() {
    GoIndiaCabTheme {
        CompleteProfileScreen()
    }
}
