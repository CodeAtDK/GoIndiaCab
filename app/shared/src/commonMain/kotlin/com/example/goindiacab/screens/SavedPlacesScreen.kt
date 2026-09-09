package com.example.goindiacab.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.ui.tooling.preview.Preview
import com.example.goindiacab.components.EditPencilIcon
import com.example.goindiacab.components.PlusIcon
import com.example.goindiacab.components.TrashIcon
import com.example.goindiacab.theme.GoIndiaCabTheme
import com.example.goindiacab.viewmodel.SavedPlaceItem
import com.example.goindiacab.viewmodel.SavedPlacesUiState
import com.example.goindiacab.viewmodel.SavedPlacesViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val DarkNavyHeader = Color(0xFF020C1B)
private val BrandOrange = Color(0xFFFF6D00)
private val PeachIconBg = Color(0xFFFFF3E0)
private val CardBorderColor = Color(0xFFE2E8F0)
private val SecondaryTextColor = Color(0xFF64748B)
private val PrimaryTextColor = Color(0xFF0F172A)
private val SoftBackground = Color(0xFFF8FAFC)

/**
 * Saved Places / Location Screen matching saved-places-screen.svg.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlacesScreen(
    viewModel: SavedPlacesViewModel = remember { SavedPlacesViewModel() },
    onBackClick: () -> Unit = {},
    onSelectPlace: (SavedPlaceItem) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotification()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar matching SVG ("Location")
                SavedPlacesTopBar(onBackClick = onBackClick)

                // List & Actions
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Outlined Button: "+ Add New Address"
                    item {
                        OutlinedButton(
                            onClick = { viewModel.openAddPlace() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = BrandOrange
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandOrange)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PlusIcon(color = BrandOrange, size = 18.dp)
                                Text(
                                    text = "Add New Address",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandOrange
                                )
                            }
                        }
                    }

                    // Saved Places Cards
                    items(uiState.places, key = { it.id }) { place ->
                        SavedPlaceCard(
                            place = place,
                            onClick = { onSelectPlace(place) },
                            onEditClick = { viewModel.openEditPlace(place) },
                            onDeleteClick = { viewModel.requestDeletePlace(place) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
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

    // Add / Edit Dialog
    if (uiState.isAddEditSheetVisible) {
        AddEditPlaceDialog(
            initialPlace = uiState.editingPlace,
            onDismiss = { viewModel.dismissAddEdit() },
            onSave = { title, address, tag ->
                viewModel.savePlace(title, address, tag)
            }
        )
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteConfirm && uiState.placeToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDelete() },
            title = {
                Text(
                    text = "Delete Address",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryTextColor
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove \"${uiState.placeToDelete?.title}\" from your saved places?",
                    fontSize = 14.sp,
                    color = SecondaryTextColor
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDelete() }) {
                    Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDelete() }) {
                    Text("Cancel", color = SecondaryTextColor)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SavedPlacesTopBar(
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
                text = "Location",
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
private fun SavedPlaceCard(
    place: SavedPlaceItem,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Orange Pin Circle Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PeachIconBg),
                contentAlignment = Alignment.Center
            ) {
                PinMarkerIcon(size = 22.dp, color = BrandOrange)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Address Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = place.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor
                )
                Text(
                    text = place.address,
                    fontSize = 13.sp,
                    color = SecondaryTextColor,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Icons: Edit & Delete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    EditPencilIcon(color = Color(0xFF475569), size = 18.dp)
                }
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                    TrashIcon(color = Color(0xFFEF4444), size = 18.dp)
                }
            }
        }
    }
}

@Composable
private fun PinMarkerIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = BrandOrange
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.88f)
            cubicTo(w * 0.25f, h * 0.55f, w * 0.18f, h * 0.40f, w * 0.18f, h * 0.32f)
            cubicTo(w * 0.18f, h * 0.16f, w * 0.32f, h * 0.10f, w * 0.5f, h * 0.10f)
            cubicTo(w * 0.68f, h * 0.10f, w * 0.82f, h * 0.16f, w * 0.82f, h * 0.32f)
            cubicTo(w * 0.82f, h * 0.40f, w * 0.75f, h * 0.55f, w * 0.5f, h * 0.88f)
            close()
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Inner circle
        drawCircle(
            color = color,
            radius = w * 0.13f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.32f),
            style = Stroke(width = stroke)
        )
    }
}

@Composable
private fun AddEditPlaceDialog(
    initialPlace: SavedPlaceItem?,
    onDismiss: () -> Unit,
    onSave: (title: String, address: String, tag: String) -> Unit
) {
    var title by remember { mutableStateOf(initialPlace?.title ?: "") }
    var address by remember { mutableStateOf(initialPlace?.address ?: "") }
    var tag by remember { mutableStateOf(initialPlace?.tag ?: "Home") }

    val isEditing = initialPlace != null
    val tags = listOf("Home", "Work", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Address" else "Add New Address",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PrimaryTextColor
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tag chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { currentTag ->
                        val isSelected = tag == currentTag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) BrandOrange else Color(0xFFF1F5F9))
                                .clickable { tag = currentTag }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = currentTag,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else PrimaryTextColor
                            )
                        }
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Label (e.g. Home, Work)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = CardBorderColor,
                        focusedLabelColor = BrandOrange
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Address Input
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Complete Address & Landmark") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = CardBorderColor,
                        focusedLabelColor = BrandOrange
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && address.isNotBlank()) {
                        onSave(title, address, tag)
                    }
                }
            ) {
                Text(
                    text = if (isEditing) "Update" else "Save Address",
                    color = BrandOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryTextColor)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Standalone Preview for SavedPlacesScreen.
 */
@Composable
@Preview
fun SavedPlacesScreenPreview() {
    GoIndiaCabTheme {
        SavedPlacesScreen()
    }
}
