package com.example.goindiacab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// 1. SETTINGS VIEW MODEL & UI STATE
// ============================================================================

/**
 * UI State for Settings Screen matching Setting.svg design.
 */
data class SettingsUiState(
    val language: String = "English",
    val availableLanguages: List<String> = listOf(
        "English",
        "Hindi (हिन्दी)",
        "Marathi (मराठी)",
        "Gujarati (ગુજરાતી)",
        "Tamil (தமிழ்)",
        "Telugu (తెలుగు)",
        "Bengali (বাংলা)"
    ),
    val phoneNumber: String = "+91 9876543210",
    val defaultVehicleType: String = "Sedan",
    val availableVehicleTypes: List<String> = listOf("Sedan", "SUV", "Hatchback", "Prime Luxury"),
    val acPreference: String = "Always ON",
    val availableAcPreferences: List<String> = listOf("Always ON", "Eco Friendly", "Driver Discretion"),
    val pushNotifications: Boolean = true,
    val emailUpdates: Boolean = true,
    val smsAlerts: Boolean = false,
    val locationSharing: Boolean = true,
    val dataAnalytics: Boolean = true,
    val showLanguageDialog: Boolean = false,
    val showVehicleTypeDialog: Boolean = false,
    val showAcPreferenceDialog: Boolean = false,
    val showChangePhoneDialog: Boolean = false,
    val userFeedbackMessage: String? = null
)

/**
 * ViewModel managing user preferences, notification toggles, and ride parameters.
 */
class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setPushNotifications(enabled: Boolean) {
        _uiState.update { it.copy(pushNotifications = enabled) }
    }

    fun setEmailUpdates(enabled: Boolean) {
        _uiState.update { it.copy(emailUpdates = enabled) }
    }

    fun setSmsAlerts(enabled: Boolean) {
        _uiState.update { it.copy(smsAlerts = enabled) }
    }

    fun setLocationSharing(enabled: Boolean) {
        _uiState.update { it.copy(locationSharing = enabled) }
    }

    fun setDataAnalytics(enabled: Boolean) {
        _uiState.update { it.copy(dataAnalytics = enabled) }
    }

    fun setLanguage(language: String) {
        _uiState.update {
            it.copy(
                language = language,
                showLanguageDialog = false,
                userFeedbackMessage = "Language set to $language"
            )
        }
    }

    fun setVehicleType(type: String) {
        _uiState.update {
            it.copy(
                defaultVehicleType = type,
                showVehicleTypeDialog = false,
                userFeedbackMessage = "Default vehicle updated to $type"
            )
        }
    }

    fun setAcPreference(pref: String) {
        _uiState.update {
            it.copy(
                acPreference = pref,
                showAcPreferenceDialog = false,
                userFeedbackMessage = "AC preference updated to $pref"
            )
        }
    }

    fun updatePhoneNumber(newPhone: String) {
        if (newPhone.isNotBlank()) {
            _uiState.update {
                it.copy(
                    phoneNumber = newPhone.trim(),
                    showChangePhoneDialog = false,
                    userFeedbackMessage = "Phone number updated successfully"
                )
            }
        }
    }

    fun setShowLanguageDialog(show: Boolean) {
        _uiState.update { it.copy(showLanguageDialog = show) }
    }

    fun setShowVehicleTypeDialog(show: Boolean) {
        _uiState.update { it.copy(showVehicleTypeDialog = show) }
    }

    fun setShowAcPreferenceDialog(show: Boolean) {
        _uiState.update { it.copy(showAcPreferenceDialog = show) }
    }

    fun setShowChangePhoneDialog(show: Boolean) {
        _uiState.update { it.copy(showChangePhoneDialog = show) }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(userFeedbackMessage = null) }
    }
}

// ============================================================================
// 2. SAVED PLACES VIEW MODEL & UI STATE
// ============================================================================

/**
 * Saved address item model.
 */
data class SavedPlaceItem(
    val id: String,
    val title: String,
    val address: String,
    val tag: String = "Other"
)

/**
 * UI State for Saved Places Screen matching saved-places-screen.svg.
 */
data class SavedPlacesUiState(
    val places: List<SavedPlaceItem> = listOf(
        SavedPlaceItem(
            id = "1",
            title = "Home",
            address = "H.No 452, Double Storey, Sector 15-A, Rohini, New Delhi – 110085",
            tag = "Home"
        ),
        SavedPlaceItem(
            id = "2",
            title = "Work/Office",
            address = "Lumen Tech Park, Tower B, 4th Floor, Sector 62, Noida – 201301",
            tag = "Work"
        )
    ),
    val isAddEditSheetVisible: Boolean = false,
    val editingPlace: SavedPlaceItem? = null,
    val placeToDelete: SavedPlaceItem? = null,
    val showDeleteConfirm: Boolean = false,
    val userNotification: String? = null
)

/**
 * ViewModel managing bookmarked and saved passenger locations.
 */
class SavedPlacesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SavedPlacesUiState())
    val uiState: StateFlow<SavedPlacesUiState> = _uiState.asStateFlow()

    fun openAddPlace() {
        _uiState.update { it.copy(isAddEditSheetVisible = true, editingPlace = null) }
    }

    fun openEditPlace(place: SavedPlaceItem) {
        _uiState.update { it.copy(isAddEditSheetVisible = true, editingPlace = place) }
    }

    fun dismissAddEdit() {
        _uiState.update { it.copy(isAddEditSheetVisible = false, editingPlace = null) }
    }

    fun savePlace(title: String, address: String, tag: String) {
        val trimmedTitle = title.trim()
        val trimmedAddress = address.trim()
        if (trimmedTitle.isEmpty() || trimmedAddress.isEmpty()) return

        val currentEditing = _uiState.value.editingPlace
        if (currentEditing != null) {
            // Update
            _uiState.update { state ->
                val updatedList = state.places.map {
                    if (it.id == currentEditing.id) {
                        it.copy(title = trimmedTitle, address = trimmedAddress, tag = tag)
                    } else it
                }
                state.copy(
                    places = updatedList,
                    isAddEditSheetVisible = false,
                    editingPlace = null,
                    userNotification = "\"$trimmedTitle\" updated successfully"
                )
            }
        } else {
            // Insert
            val newPlace = SavedPlaceItem(
                id = (System.currentTimeMillis() % 100000).toString(),
                title = trimmedTitle,
                address = trimmedAddress,
                tag = tag
            )
            _uiState.update { state ->
                state.copy(
                    places = state.places + newPlace,
                    isAddEditSheetVisible = false,
                    editingPlace = null,
                    userNotification = "\"$trimmedTitle\" added to saved places"
                )
            }
        }
    }

    fun requestDeletePlace(place: SavedPlaceItem) {
        _uiState.update { it.copy(placeToDelete = place, showDeleteConfirm = true) }
    }

    fun confirmDelete() {
        val target = _uiState.value.placeToDelete ?: return
        _uiState.update { state ->
            state.copy(
                places = state.places.filterNot { it.id == target.id },
                placeToDelete = null,
                showDeleteConfirm = false,
                userNotification = "\"${target.title}\" deleted"
            )
        }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(placeToDelete = null, showDeleteConfirm = false) }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotification = null) }
    }
}

// ============================================================================
// 3. ABOUT VIEW MODEL & UI STATE
// ============================================================================

/**
 * UI State for About Screen matching About GoIndiaCab.svg.
 */
data class AboutUiState(
    val appName: String = "GoIndiaCab",
    val tagline: String = "Your Ride, Your Way",
    val version: String = "Version 2.4.1",
    val whoWeAre: String = "GoIndiaCab is India's premier intercity and local ride-hailing companion, focused on safe, affordable, and certified travel experiences.",
    val ourMission: String = "To empower local drivers with sustainable revenue models while giving passengers reliable, verified, and premium multi-city cab booking frameworks.",
    val statCities: String = "1000+",
    val statPartners: String = "50K+",
    val statRides: String = "2M+",
    val openSourceLicenses: List<String> = listOf(
        "Kotlin Multiplatform (JetBrains - Apache 2.0)",
        "Jetpack Compose Multiplatform (Apache 2.0)",
        "Kotlinx Coroutines & Serialization (Apache 2.0)",
        "Ktor HTTP Client (JetBrains - Apache 2.0)"
    )
)

/**
 * ViewModel providing company info, versioning, and app mission.
 */
class AboutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()
}
