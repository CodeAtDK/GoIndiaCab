package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.*

// Styling tokens matching edit-profile.svg
private val EditProfileHeaderDark = Color(0xFF020C1B)
private val EditProfileOrange = Color(0xFFF47B20)
private val EditProfileScreenBg = Color(0xFFF4F6F9)
private val EditProfileFieldBg = Color.White
private val EditProfileFieldBorder = Color(0xFFE5E7EB)
private val EditProfileLabelGray = Color(0xFF6B7280)
private val EditProfileTextDark = Color(0xFF111827)

@Composable
fun EditProfileScreen(
    initialFullName: String = "Rahul Sharma",
    initialEmail: String = "rahul.sharma@example.com",
    initialPhone: String = "+91 9876543210",
    initialGender: String = "Male",
    initialDob: String = "15 Jan 1995",
    initialInitials: String = "RS",
    onBackClick: () -> Unit = {},
    onSaveClick: (fullName: String, email: String, phone: String, gender: String, dob: String) -> Unit = { _, _, _, _, _ -> }
) {
    var fullName by remember { mutableStateOf(initialFullName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var gender by remember { mutableStateOf(initialGender) }
    var dob by remember { mutableStateOf(initialDob) }

    var isGenderDropdownExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

    var showDobDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = EditProfileScreenBg,
        topBar = {
            // Dark Header matching edit-profile.svg
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EditProfileHeaderDark)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Circular Button
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

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Edit Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White
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
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with camera action badge
            Box(
                modifier = Modifier.padding(bottom = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .border(3.dp, EditProfileOrange, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(EditProfileHeaderDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialInitials,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = EditProfileOrange
                    )
                }

                // Camera Badge Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(EditProfileOrange)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { /* Photo Picker Action */ },
                    contentAlignment = Alignment.Center
                ) {
                    CameraIcon(size = 16.dp, color = Color.White)
                }
            }

            // Input Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Full Name
                EditProfileInputField(
                    label = "FULL NAME",
                    value = fullName,
                    onValueChange = { fullName = it },
                    keyboardType = KeyboardType.Text
                )

                // Email
                EditProfileInputField(
                    label = "EMAIL",
                    value = email,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )

                // Phone Number
                EditProfileInputField(
                    label = "PHONE NUMBER",
                    value = phone,
                    onValueChange = { phone = it },
                    keyboardType = KeyboardType.Phone
                )

                // Gender Selector
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "GENDER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = EditProfileLabelGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .border(1.dp, EditProfileFieldBorder, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            color = EditProfileFieldBg,
                            onClick = { isGenderDropdownExpanded = !isGenderDropdownExpanded }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = gender,
                                    fontSize = 15.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = EditProfileTextDark,
                                    fontWeight = FontWeight.Medium
                                )
                                ChevronDownIcon(size = 18.dp, color = Color(0xFF6B7280))
                            }
                        }

                        DropdownMenu(
                            expanded = isGenderDropdownExpanded,
                            onDismissRequest = { isGenderDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(Color.White)
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            fontFamily = dmSansFontFamily(),
                                            fontSize = 14.sp,
                                            color = if (option == gender) EditProfileOrange else EditProfileTextDark,
                                            fontWeight = if (option == gender) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        gender = option
                                        isGenderDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Date of Birth Selector
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DATE OF BIRTH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = EditProfileLabelGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, EditProfileFieldBorder, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        color = EditProfileFieldBg,
                        onClick = { showDobDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = dob,
                                fontSize = 15.sp,
                                fontFamily = dmSansFontFamily(),
                                color = EditProfileTextDark,
                                fontWeight = FontWeight.Medium
                            )
                            CalendarIcon(size = 18.dp, color = Color(0xFF6B7280))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Save Changes Primary Button
            Button(
                onClick = {
                    onSaveClick(fullName, email, phone, gender, dob)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EditProfileOrange),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Simple Date of Birth Quick Edit Dialog
    if (showDobDialog) {
        var tempDob by remember { mutableStateOf(dob) }
        AlertDialog(
            onDismissRequest = { showDobDialog = false },
            title = {
                Text(
                    text = "Update Date of Birth",
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = EditProfileTextDark
                )
            },
            text = {
                OutlinedTextField(
                    value = tempDob,
                    onValueChange = { tempDob = it },
                    label = { Text("DD MMM YYYY") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EditProfileOrange,
                        cursorColor = EditProfileOrange
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dob = tempDob
                        showDobDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditProfileOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDobDialog = false }) {
                    Text("Cancel", color = EditProfileLabelGray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun EditProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = outfitFontFamily(),
            color = EditProfileLabelGray,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(EditProfileFieldBg, RoundedCornerShape(14.dp))
                .border(1.dp, EditProfileFieldBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Medium,
                    color = EditProfileTextDark
                ),
                cursorBrush = SolidColor(EditProfileOrange),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun EditProfileScreenPreview() {
    GoIndiaCabTheme {
        EditProfileScreen()
    }
}
