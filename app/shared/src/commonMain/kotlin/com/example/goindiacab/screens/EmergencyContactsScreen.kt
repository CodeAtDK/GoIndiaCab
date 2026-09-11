package com.example.goindiacab.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
private val BrandBlue = Color(0xFF0052CC)
private val SafetyRed = Color(0xFFEF4444)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

data class EmergencyContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phone: String
)

/**
 * Screen: Manage Trusted Emergency SOS Contacts.
 * Backed by API-61.
 * Allows adding and deleting emergency contacts who receive automatic SOS alerts.
 */
@Composable
fun EmergencyContactsScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()
    var autoShareEnabled by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }

    val contacts = remember {
        mutableStateListOf(
            EmergencyContact("c_1", "Sanjay Sharma", "Parent", "+91 98111 22233"),
            EmergencyContact("c_2", "Pooja Sharma", "Spouse", "+91 98765 43210")
        )
    }

    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var selectedRelation by remember { mutableStateOf("Parent") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Emergency Contact",
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it.filter { ch -> ch.isDigit() }.take(10) },
                        label = { Text("10-Digit Mobile Number", fontSize = 12.sp) },
                        prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Relationship:", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Parent", "Spouse", "Sibling", "Friend").forEach { rel ->
                            val isSelected = selectedRelation == rel
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BrandOrange else Color(0xFFF1F5F9))
                                    .clickable { selectedRelation = rel }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = rel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextDark
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newPhone.length == 10) {
                            contacts.add(
                                EmergencyContact(
                                    id = "c_${kotlin.random.Random.nextInt(100, 999)}",
                                    name = newName.trim(),
                                    relationship = selectedRelation,
                                    phone = "+91 $newPhone"
                                )
                            )
                            newName = ""
                            newPhone = ""
                            showAddDialog = false
                            toast("✓ Emergency contact saved")
                        } else {
                            toast("Please enter a valid name and 10-digit number")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Contact", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                        text = "Emergency SOS Contacts",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Safety Shield Header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.2.dp, SafetyRed.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SafetyRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "SOS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emergency Safety Guard",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your emergency contacts will receive an SMS alert with live GPS tracking link if you trigger the SOS button.",
                                fontSize = 12.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 2. Auto-share Toggle Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Share Outstation Trips",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Send live ride link to contacts when your cab starts",
                                fontSize = 12.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted
                            )
                        }

                        Switch(
                            checked = autoShareEnabled,
                            onCheckedChange = { autoShareEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrandOrange
                            )
                        )
                    }
                }
            }

            // 3. Contacts List Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRUSTED CONTACTS (${contacts.size}/3)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    if (contacts.size < 3) {
                        TextButton(
                            onClick = { showAddDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+ Add Contact",
                                color = BrandBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = outfitFontFamily()
                            )
                        }
                    }
                }
            }

            // 4. Contact Cards
            items(contacts, key = { it.id }) { contact ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.name.take(1).uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = contact.name,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = TextDark
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(BrandOrange.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = contact.relationship,
                                            fontSize = 10.sp,
                                            fontFamily = dmSansFontFamily(),
                                            fontWeight = FontWeight.Bold,
                                            color = BrandOrange
                                        )
                                    }
                                }

                                Text(
                                    text = contact.phone,
                                    fontSize = 12.5.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                contacts.remove(contact)
                                toast("Removed ${contact.name}")
                            }
                        ) {
                            CloseIcon(size = 18.dp, color = TextMuted)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview
@Composable
fun EmergencyContactsScreenPreview() {
    GoIndiaCabTheme {
        EmergencyContactsScreen()
    }
}
