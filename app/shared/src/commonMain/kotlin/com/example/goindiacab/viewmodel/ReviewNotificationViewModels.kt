package com.example.goindiacab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ============================================================================
// 1. MY REVIEWS VIEW MODEL & UI STATE
// ============================================================================

/**
 * Single driver/trip review item.
 */
data class ReviewItem(
    val id: String,
    val driverName: String,
    val vehicleInfo: String,
    val initials: String,
    val date: String,
    val rating: Int,
    val reviewText: String
)

/**
 * UI State for My Reviews Screen matching MyReview.svg.
 */
data class MyReviewsUiState(
    val averageRating: Double = 4.2,
    val totalReviews: Int = 12,
    val reviews: List<ReviewItem> = listOf(
        ReviewItem(
            id = "1",
            driverName = "Suresh Patel",
            vehicleInfo = "Maruti Dzire (DL 1Y A 4321)",
            initials = "SP",
            date = "12 Oct 2024",
            rating = 5,
            reviewText = "Fantastic experience! Driver was very professional, polite and knew the best routes. The car was spotless and well-maintained."
        ),
        ReviewItem(
            id = "2",
            driverName = "Rahul Deshmukh",
            vehicleInfo = "Toyota Etios (MH 12 B 9876)",
            initials = "RD",
            date = "05 Oct 2024",
            rating = 4,
            reviewText = "Punctual driver. AC cooling was perfect for the long journey. Highly recommended."
        ),
        ReviewItem(
            id = "3",
            driverName = "Amit Sharma",
            vehicleInfo = "Ertiga (UP 16 C 5432)",
            initials = "AS",
            date = "28 Sep 2024",
            rating = 4,
            reviewText = "Polite driver, but arrived 10 mins late due to some heavy morning traffic. Journey was smooth."
        )
    )
)

/**
 * ViewModel managing passenger ride reviews, average score calculations, and feedbacks.
 */
class MyReviewsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsUiState())
    val uiState: StateFlow<MyReviewsUiState> = _uiState.asStateFlow()

    fun addReview(driverName: String, vehicleInfo: String, initials: String, rating: Int, text: String) {
        val newReview = ReviewItem(
            id = kotlin.random.Random.nextInt(1000, 9999).toString(),
            driverName = driverName,
            vehicleInfo = vehicleInfo,
            initials = initials,
            date = "Today",
            rating = rating,
            reviewText = text
        )
        _uiState.update { state ->
            val updatedReviews = listOf(newReview) + state.reviews
            val total = state.totalReviews + 1
            val avg = (state.reviews.sumOf { it.rating } + rating).toDouble() / updatedReviews.size
            state.copy(
                reviews = updatedReviews,
                totalReviews = total,
                averageRating = (avg * 10).toInt() / 10.0
            )
        }
    }
}

// ============================================================================
// 2. NOTIFICATIONS VIEW MODEL & UI STATE
// ============================================================================

enum class NotificationCategory {
    ALL,
    RIDES,
    OFFERS
}

enum class NotificationType {
    RIDE_CONFIRMED,
    PAYMENT_DUE,
    PROMO_OFFER,
    RIDE_COMPLETED,
    SAFETY_UPDATE,
    FINAL_PAYMENT
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val isUnread: Boolean,
    val actionRoute: String? = null
)

/**
 * UI State for Notifications Screen matching notifications-screen.svg.
 */
data class NotificationsUiState(
    val selectedCategory: NotificationCategory = NotificationCategory.ALL,
    val notifications: List<NotificationItem> = listOf(
        NotificationItem(
            id = "1",
            title = "Your ride is confirmed!",
            message = "Toyota Etios • DL 1CA 1234 • Driver Assigned & arriving shortly.",
            timestamp = "2m ago",
            type = NotificationType.RIDE_CONFIRMED,
            category = NotificationCategory.RIDES,
            isUnread = true
        ),
        NotificationItem(
            id = "2",
            title = "Payment Due Reminder",
            message = "Your 30% mid-trip payment of Rs 3,150 for Delhi to Jaipur trip is due. Please pay now to continue your ride.",
            timestamp = "30m ago",
            type = NotificationType.PAYMENT_DUE,
            category = NotificationCategory.RIDES,
            isUnread = true
        ),
        NotificationItem(
            id = "3",
            title = "Flat 20% OFF Airport Rides",
            message = "Use code AIRPORT20 before Sept 30. Redeem your offer now!",
            timestamp = "1h ago",
            type = NotificationType.PROMO_OFFER,
            category = NotificationCategory.OFFERS,
            isUnread = true
        ),
        NotificationItem(
            id = "4",
            title = "Ride Completed Successfully",
            message = "Your trip Delhi to Agra was completed. Total fare: ₹2,450.",
            timestamp = "Yesterday",
            type = NotificationType.RIDE_COMPLETED,
            category = NotificationCategory.RIDES,
            isUnread = false
        ),
        NotificationItem(
            id = "5",
            title = "Safety Update: Verified Drivers",
            message = "All GoIndiaCab partners are 100% verified and background checked.",
            timestamp = "2d ago",
            type = NotificationType.SAFETY_UPDATE,
            category = NotificationCategory.ALL,
            isUnread = false
        ),
        NotificationItem(
            id = "6",
            title = "Final Payment Pending",
            message = "Your 20% final payment of Rs 2,100 for Delhi to Agra trip is pending. Complete payment to receive your trip receipt.",
            timestamp = "3h ago",
            type = NotificationType.FINAL_PAYMENT,
            category = NotificationCategory.RIDES,
            isUnread = true
        )
    )
) {
    val filteredNotifications: List<NotificationItem>
        get() = when (selectedCategory) {
            NotificationCategory.ALL -> notifications
            NotificationCategory.RIDES -> notifications.filter { it.category == NotificationCategory.RIDES }
            NotificationCategory.OFFERS -> notifications.filter { it.category == NotificationCategory.OFFERS }
        }

    val unreadCount: Int
        get() = notifications.count { it.isUnread }
}

/**
 * ViewModel managing real-time notifications, category filtering, and read states.
 */
class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun selectCategory(category: NotificationCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun markAsRead(id: String) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map {
                    if (it.id == id) it.copy(isUnread = false) else it
                }
            )
        }
    }

    fun markAllAsRead() {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { it.copy(isUnread = false) }
            )
        }
    }
}
