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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.ChevronRightIcon
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.viewmodel.SettingsUiState
import com.example.goindiacab.viewmodel.SettingsViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val DarkNavyHeader = Color(0xFF020C1B)
private val BrandOrange = Color(0xFFFF6D00)
private val CardBorderColor = Color(0xFFE2E8F0)
private val SecondaryTextColor = Color(0xFF64748B)
private val PrimaryTextColor = Color(0xFF0F172A)
private val SoftBackground = Color(0xFFF8FAFC)

/**
 * Settings Screen matching Setting.svg design with MVVM reactive preferences.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = remember { SettingsViewModel() },
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDataAnalyticsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedbackMessage()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                SettingsTopBar(onBackClick = onBackClick)

                // Body
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    // SECTION 1: ACCOUNT DETAILS
                    SettingsSection(title = "ACCOUNT DETAILS") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                SettingsValueRow(
                                    title = "Language",
                                    value = uiState.language,
                                    onClick = { viewModel.setShowLanguageDialog(true) }
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                SettingsValueRow(
                                    title = "Change Phone Number",
                                    value = uiState.phoneNumber,
                                    onClick = { viewModel.setShowChangePhoneDialog(true) }
                                )
                            }
                        }
                    }

                    // SECTION 2: RIDE PREFERENCES
                    SettingsSection(title = "RIDE PREFERENCES") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                SettingsValueRow(
                                    title = "Default Vehicle Type",
                                    value = uiState.defaultVehicleType,
                                    onClick = { viewModel.setShowVehicleTypeDialog(true) }
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                SettingsValueRow(
                                    title = "AC Preference",
                                    value = uiState.acPreference,
                                    onClick = { viewModel.setShowAcPreferenceDialog(true) }
                                )
                            }
                        }
                    }

                    // SECTION 3: NOTIFICATIONS
                    SettingsSection(title = "NOTIFICATIONS") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                SettingsSwitchRow(
                                    title = "Push Notifications",
                                    checked = uiState.pushNotifications,
                                    onCheckedChange = { viewModel.setPushNotifications(it) }
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                SettingsSwitchRow(
                                    title = "Email Updates",
                                    checked = uiState.emailUpdates,
                                    onCheckedChange = { viewModel.setEmailUpdates(it) }
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                SettingsSwitchRow(
                                    title = "SMS Alerts",
                                    checked = uiState.smsAlerts,
                                    onCheckedChange = { viewModel.setSmsAlerts(it) }
                                )
                            }
                        }
                    }

                    // SECTION 4: PRIVACY
                    SettingsSection(title = "PRIVACY") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                SettingsSwitchRow(
                                    title = "Location Sharing",
                                    checked = uiState.locationSharing,
                                    onCheckedChange = { viewModel.setLocationSharing(it) }
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                SettingsValueRow(
                                    title = "Data & Analytics",
                                    value = "",
                                    onClick = { showDataAnalyticsDialog = true }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // Snackbar Host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }

    // Language Dialog
    if (uiState.showLanguageDialog) {
        SelectionDialog(
            title = "Select Language",
            options = uiState.availableLanguages,
            selected = uiState.language,
            onSelect = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.setShowLanguageDialog(false) }
        )
    }

    // Vehicle Type Dialog
    if (uiState.showVehicleTypeDialog) {
        SelectionDialog(
            title = "Default Vehicle Type",
            options = uiState.availableVehicleTypes,
            selected = uiState.defaultVehicleType,
            onSelect = { viewModel.setVehicleType(it) },
            onDismiss = { viewModel.setShowVehicleTypeDialog(false) }
        )
    }

    // AC Preference Dialog
    if (uiState.showAcPreferenceDialog) {
        SelectionDialog(
            title = "AC Preference",
            options = uiState.availableAcPreferences,
            selected = uiState.acPreference,
            onSelect = { viewModel.setAcPreference(it) },
            onDismiss = { viewModel.setShowAcPreferenceDialog(false) }
        )
    }

    // Change Phone Dialog
    if (uiState.showChangePhoneDialog) {
        var phoneInput by remember { mutableStateOf(uiState.phoneNumber) }
        AlertDialog(
            onDismissRequest = { viewModel.setShowChangePhoneDialog(false) },
            title = {
                Text(
                    text = "Change Phone Number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryTextColor
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter your new registered 10-digit mobile number with country code.",
                        fontSize = 13.sp,
                        color = SecondaryTextColor
                    )
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandOrange,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = PrimaryTextColor,
                            unfocusedTextColor = PrimaryTextColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.updatePhoneNumber(phoneInput) }) {
                    Text("Save", color = BrandOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowChangePhoneDialog(false) }) {
                    Text("Cancel", color = SecondaryTextColor)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Data Analytics Dialog
    if (showDataAnalyticsDialog) {
        AlertDialog(
            onDismissRequest = { showDataAnalyticsDialog = false },
            title = {
                Text(
                    text = "Data & Analytics",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryTextColor
                )
            },
            text = {
                Text(
                    text = "GoIndiaCab collects anonymized telemetry and route telemetry to calculate optimal ETAs and reduce empty dead-heading miles for driver partners. No personal biometric or location data is ever sold to third parties.",
                    fontSize = 13.sp,
                    color = SecondaryTextColor,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showDataAnalyticsDialog = false }) {
                    Text("Got It", color = BrandOrange, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingsTopBar(
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
                text = "Settings",
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
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandOrange,
            letterSpacing = 1.1.sp
        )
        content()
    }
}

@Composable
private fun SettingsValueRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryTextColor
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    fontSize = 13.5.sp,
                    color = SecondaryTextColor,
                    fontWeight = FontWeight.Normal
                )
            }
            ChevronRightIcon(color = Color(0xFF94A3B8), size = 16.dp)
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryTextColor
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandOrange,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1),
                uncheckedBorderColor = Color.Transparent,
                checkedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SelectionDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PrimaryTextColor
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEach { option ->
                    val isSelected = option == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelect(option) },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandOrange)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) BrandOrange else PrimaryTextColor
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryTextColor)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Standalone Preview for SettingsScreen.
 */
@Composable
@Preview
fun SettingsScreenPreview() {
    GoIndiaCabTheme {
        SettingsScreen()
    }
}
