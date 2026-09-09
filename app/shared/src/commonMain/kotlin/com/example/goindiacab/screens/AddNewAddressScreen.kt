package com.example.goindiacab.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.data.models.LocationItem
import com.example.goindiacab.viewmodel.SavedPlaceItem
import com.example.goindiacab.theme.*
import com.example.goindiacab.viewmodel.SavedPlacesViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val TopBarNavy = Color(0xFF0A1128)
private val BrandOrange = Color(0xFFFF6B00)
private val FieldBorderColor = Color(0xFFE2E8F0)
private val FieldBackground = Color.White
private val LabelDarkColor = Color(0xFF1E293B)
private val PlaceholderColor = Color(0xFF94A3B8)

enum class AddressLabelType(val label: String) {
    HOME("Home"),
    OFFICE("Office"),
    OTHER("Other")
}

/**
 * Screen 36: Add New Address Screen (Phase 4).
 * Dedicated full screen matching add-new-address.svg.
 */
@Composable
fun AddNewAddressScreen(
    viewModel: SavedPlacesViewModel = remember { SavedPlacesViewModel() },
    selectedMapLocation: LocationItem? = null,
    onPickOnMapClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onAddressSaved: (SavedPlaceItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var selectedLabel by remember { mutableStateOf(AddressLabelType.HOME) }
    var fullAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedMapLocation) {
        selectedMapLocation?.let { loc ->
            if (loc.title.isNotBlank()) {
                fullAddress = loc.title
                if (loc.subtitle.isNotBlank()) {
                    val segments = loc.subtitle.split(",").map { it.trim() }
                    if (segments.isNotEmpty()) {
                        city = segments.first()
                        if (segments.size > 1) {
                            state = segments[1]
                        }
                    }
                }
            }
        }
    }

    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TopBarNavy
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
                        text = "Add New Address",
                        fontSize = 18.sp,
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
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (fullAddress.isBlank()) {
                                errorMessage = "Please enter full address"
                                return@Button
                            }
                            val constructedAddress = buildString {
                                append(fullAddress.trim())
                                if (city.isNotBlank()) append(", ${city.trim()}")
                                if (state.isNotBlank()) append(", ${state.trim()}")
                                if (pincode.isNotBlank()) append(" - ${pincode.trim()}")
                            }
                            viewModel.savePlace(
                                title = selectedLabel.label,
                                address = constructedAddress,
                                tag = selectedLabel.label
                            )
                            val newItem = SavedPlaceItem(
                                id = "addr_${System.currentTimeMillis()}",
                                title = selectedLabel.label,
                                address = constructedAddress,
                                tag = selectedLabel.label
                            )
                            onAddressSaved(newItem)
                            onBackClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Text(
                            text = "Save Address",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = Color.White
                        )
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. "Use Current Location" Orange Outlined Button
            Surface(
                onClick = onPickOnMapClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFF7ED),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandOrange)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_gps_crosshair),
                        contentDescription = null,
                        tint = BrandOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Use Current Location",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = BrandOrange
                    )
                }
            }

            // 2. Address Label Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Address Label",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = outfitFontFamily(),
                    color = LabelDarkColor
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AddressLabelChip(
                        label = "Home",
                        isSelected = selectedLabel == AddressLabelType.HOME,
                        icon = { isSelected ->
                            Icon(
                                painter = painterResource(Res.drawable.ic_nav_home),
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = { selectedLabel = AddressLabelType.HOME }
                    )

                    AddressLabelChip(
                        label = "Office",
                        isSelected = selectedLabel == AddressLabelType.OFFICE,
                        icon = { isSelected ->
                            Icon(
                                painter = painterResource(Res.drawable.ic_luggage_bag),
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = { selectedLabel = AddressLabelType.OFFICE }
                    )

                    AddressLabelChip(
                        label = "Other",
                        isSelected = selectedLabel == AddressLabelType.OTHER,
                        icon = { isSelected ->
                            Icon(
                                painter = painterResource(Res.drawable.ic_location_pin),
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = { selectedLabel = AddressLabelType.OTHER }
                    )
                }
            }

            // 3. Full Address Input Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Full Address",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = outfitFontFamily(),
                    color = LabelDarkColor
                )

                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = {
                        fullAddress = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            text = "Enter flat no, building, street, colony...",
                            fontSize = 14.sp,
                            color = PlaceholderColor,
                            fontFamily = dmSansFontFamily()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = FieldBorderColor
                    ),
                    maxLines = 4
                )
            }

            // 4. City Input Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "City",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = outfitFontFamily(),
                    color = LabelDarkColor
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            text = "e.g. New Delhi",
                            fontSize = 14.sp,
                            color = PlaceholderColor,
                            fontFamily = dmSansFontFamily()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = FieldBorderColor
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            // 5. State Input Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "State",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = outfitFontFamily(),
                    color = LabelDarkColor
                )

                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            text = "e.g. Delhi",
                            fontSize = 14.sp,
                            color = PlaceholderColor,
                            fontFamily = dmSansFontFamily()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = FieldBorderColor
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            // 6. Pincode Input Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pincode",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = outfitFontFamily(),
                    color = LabelDarkColor
                )

                OutlinedTextField(
                    value = pincode,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            pincode = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        Text(
                            text = "e.g. 110085",
                            fontSize = 14.sp,
                            color = PlaceholderColor,
                            fontFamily = dmSansFontFamily()
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = FieldBorderColor
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontFamily = dmSansFontFamily()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Address Label Chip matching SVG style.
 */
@Composable
private fun AddressLabelChip(
    label: String,
    isSelected: Boolean,
    icon: @Composable (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) BrandOrange else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) BrandOrange else Color(0xFFCBD5E1)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon(isSelected)
            Text(
                text = label,
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = dmSansFontFamily(),
                color = if (isSelected) Color.White else Color(0xFF334155)
            )
        }
    }
}

@Preview
@Composable
fun AddNewAddressScreenPreview() {
    GoIndiaCabTheme {
        AddNewAddressScreen()
    }
}
