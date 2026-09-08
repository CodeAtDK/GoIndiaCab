#!/usr/bin/env python3
"""
Generates CSV and TSV files for importing directly into Google Sheets
for GoIndiaCab Mobile App API Requirements.
"""

import csv
import json
import os

OUTPUT_DIR = "/Users/dhruva/Documents/KMP Android/GoIndiaCab/docs"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# -------------------------------------------------------------
# TAB 1: API Master Tracker
# -------------------------------------------------------------
tab1_headers = [
    "API ID",
    "Module",
    "Screen Name",
    "HTTP Method",
    "Endpoint",
    "Auth Required",
    "Priority",
    "Phase",
    "Status",
    "Assignee",
    "Target Date",
    "Brief Description"
]

tab1_rows = [
    [
        "API-01", "Auth & Session", "Splash / Session Check", "GET",
        "/auth/session", "Bearer Token", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Validate cached JWT on app launch; returns active ride if exists"
    ],
    [
        "API-02", "Auth & Session", "Login", "POST",
        "/auth/send-otp", "Public", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Send 6-digit verification code to mobile (SMS) or email"
    ],
    [
        "API-03", "Auth & Session", "OTP Verification / Error", "POST",
        "/auth/verify-otp", "Public", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Validate user-entered OTP, return JWT tokens and user profile"
    ],
    [
        "API-04", "Auth & Session", "Resend OTP", "POST",
        "/auth/resend-otp", "Public", "P1 (High)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Re-trigger OTP delivery after countdown expiry"
    ],
    [
        "API-05", "Auth & Session", "Login (Social)", "POST",
        "/auth/social-login", "Public", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Validate Google/Apple ID token; check if phone is linked"
    ],
    [
        "API-06", "Auth & Session", "Login (Phone Link)", "POST",
        "/auth/link-phone", "Bearer Token", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Link verified mobile number to social login account"
    ],
    [
        "API-07", "Home Feed", "Home Screen", "GET",
        "/home/feed", "Bearer Token", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Return promo banners, active ride snippet, categories, recent routes"
    ],
    [
        "API-08", "Location & Maps", "Pickup / Destination Search", "GET",
        "/location/autocomplete", "Bearer Token", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Backend proxy for place search with lat/lng bias (Google Places)"
    ],
    [
        "API-09", "Location & Maps", "Confirm Pickup Location - Map", "GET",
        "/location/reverse-geocode", "Bearer Token", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Convert pin drop coordinates to human-readable street address"
    ],
    [
        "API-10", "Location & Maps", "City Route Selection", "GET",
        "/outstation/popular-routes", "Bearer Token", "P1 (High)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Fetch popular outstation city routes with starting fares and photos"
    ],
    [
        "API-11", "Location & Maps", "Route Confirmation", "POST",
        "/route/calculate", "Bearer Token", "P0 (Critical)", "Phase 1 - Prerequisite",
        "Pending", "Backend Team", "", "Calculate driving distance, duration, toll count, and map polyline"
    ],
    [
        "API-12", "Scheduling", "Schedule / Date / Time Selection", "GET",
        "/rides/available-slots", "Bearer Token", "P1 (High)", "Phase 2 - Booking Funnel",
        "Pending", "Backend Team", "", "Available departure time slots, booking lead buffer, night charges"
    ],
    [
        "API-13", "Vehicle Fleet", "Vehicle Partner Options", "POST",
        "/cabs/search", "Bearer Token", "P0 (Critical)", "Phase 2 - Booking Funnel",
        "Pending", "Backend Team", "", "Available cab tiers (Sedan, SUV, Innova) with live fare quotes & ETA"
    ],
    [
        "API-14", "Vehicle Fleet", "Cab Detail Screen", "GET",
        "/cabs/{vehicleTypeId}/details", "Bearer Token", "P0 (Critical)", "Phase 2 - Booking Funnel",
        "Pending", "Backend Team", "", "Detailed cab specs, luggage/seating, inclusions, exclusions, cancel terms"
    ],
    [
        "API-15", "Fare & Booking", "Fare Details / Summary", "POST",
        "/booking/fare-quote", "Bearer Token", "P0 (Critical)", "Phase 2 - Booking Funnel",
        "Pending", "Backend Team", "", "Detailed fare breakdown (Base, GST, Tolls, Advance 20% vs 100%)"
    ],
    [
        "API-16", "Fare & Booking", "Apply Coupon", "POST",
        "/coupons/apply", "Bearer Token", "P1 (High)", "Phase 2 - Booking Funnel",
        "Pending", "Backend Team", "", "Validate promo code and return calculated discount"
    ],
    [
        "API-17", "Booking & Payment", "Booking Confirmation / Pay", "POST",
        "/booking/create", "Bearer Token", "P0 (Critical)", "Phase 2 - Booking Funnel",
        "Pending", "Backend Team", "", "Create booking record and initialize Razorpay/gateway order"
    ],
    [
        "API-18", "Booking & Payment", "Payment Processing / Failed", "POST",
        "/payment/verify", "Bearer Token", "P0 (Critical)", "Phase 2 - Booking Funnel",
        "Pending", "Backend Team", "", "Verify gateway payment signature and confirm booking"
    ],
    [
        "API-19", "Dispatch & Tracking", "Partner Searching / Assigned", "GET",
        "/booking/{bookingId}/status", "Bearer Token", "P0 (Critical)", "Phase 3 - Post Booking",
        "Pending", "Backend Team", "", "Polling endpoint for driver allocation and start trip OTP"
    ],
    [
        "API-20", "Dispatch & Tracking", "Booking ID Confirmation", "GET",
        "/booking/{bookingId}", "Bearer Token", "P0 (Critical)", "Phase 3 - Post Booking",
        "Pending", "Backend Team", "", "Full confirmed booking ticket, itinerary, driver details, payment recap"
    ],
    [
        "API-21", "Cancellation", "Refund Initiated / Cancel", "POST",
        "/booking/{bookingId}/cancel", "Bearer Token", "P1 (High)", "Phase 3 - Post Booking",
        "Pending", "Backend Team", "", "Cancel ride, calculate cancellation fee, initiate refund transaction"
    ],
    [
        "API-22", "Milestones", "Trip Payment Schedule", "GET",
        "/booking/{bookingId}/payment-schedule", "Bearer Token", "P1 (High)", "Phase 3 - Post Booking",
        "Pending", "Backend Team", "", "Milestone breakdown (20% advance, 50% at pickup, 30% at drop)"
    ],
    [
        "API-23", "Milestones", "Pay Next Milestone", "POST",
        "/booking/{bookingId}/pay-milestone", "Bearer Token", "P1 (High)", "Phase 3 - Post Booking",
        "Pending", "Backend Team", "", "Create gateway order for paying the next milestone instalment"
    ],
    [
        "API-24", "Milestones", "Payment OTP Verification", "POST",
        "/payment/verify-otp", "Bearer Token", "P1 (High)", "Phase 3 - Post Booking",
        "Pending", "Backend Team", "", "Verify driver-collected cash/milestone payment via customer OTP"
    ]
]

# -------------------------------------------------------------
# TAB 2: Detailed Endpoint Specifications
# -------------------------------------------------------------
tab2_headers = [
    "API ID",
    "Endpoint",
    "Method",
    "Auth",
    "Query / Path Parameters",
    "Request Payload (Sample JSON)",
    "Success Response (Sample JSON 200/201)",
    "Common Error Codes",
    "Business Logic & Edge Cases"
]

tab2_rows = [
    [
        "API-01",
        "/auth/session",
        "GET",
        "Bearer Token",
        "None",
        "None (GET request)",
        '{"success": true, "data": {"is_authenticated": true, "user": {"id": "usr_919876543210", "phone": "+919876543210", "name": "Rahul Sharma"}, "active_booking_id": "BK-2026-9812"}, "error": null}',
        "401 Unauthorized",
        "If token expired/invalid, app clears cached tokens and navigates to Login screen."
    ],
    [
        "API-02",
        "/auth/send-otp",
        "POST",
        "Public",
        "None",
        '{"type": "PHONE", "identifier": "+919876543210"}  // Or {"type": "EMAIL", "identifier": "rahul@example.com"}',
        '{"success": true, "data": {"request_id": "otp_req_abc123", "delivery_type": "SMS", "expires_in_seconds": 60, "resend_available_after": 30}, "error": null}',
        "400 Bad Request, 429 Rate Limit",
        "Rate limit to 3 requests per phone per 10 mins. Support bypass OTP (e.g. 123456) in staging."
    ],
    [
        "API-03",
        "/auth/verify-otp",
        "POST",
        "Public",
        "None",
        '{"request_id": "otp_req_abc123", "otp": "482910"}',
        '{"success": true, "data": {"token": "jwt_access_token_xxx", "refresh_token": "refresh_token_xxx", "expires_in": 2592000, "user": {"id": "usr_919876543210", "phone": "+919876543210", "name": "Rahul Sharma", "is_new_user": false}}, "error": null}',
        "400 INVALID_OTP, 410 OTP_EXPIRED",
        "Max 3 incorrect OTP attempts before locking request_id. Works for both SMS & Email OTP."
    ],
    [
        "API-04",
        "/auth/resend-otp",
        "POST",
        "Public",
        "None",
        '{"phone": "+919876543210", "request_id": "otp_req_abc123"}',
        '{"success": true, "data": {"request_id": "otp_req_def456", "expires_in_seconds": 60, "message": "A new OTP has been sent via SMS."}, "error": null}',
        "400 INVALID_REQUEST, 429 TOO_EARLY",
        "Frontend blocks resend until 30s countdown expires. Backend invalidates previous OTP."
    ],
    [
        "API-05",
        "/auth/social-login",
        "POST",
        "Public",
        "None",
        '{"provider": "GOOGLE", "id_token": "jwt_from_google", "full_name": "Rahul Sharma", "email": "rahul@gmail.com"}',
        '{"success": true, "data": {"token": "jwt_access_token_xxx", "refresh_token": "refresh_xxx", "expires_in": 2592000, "phone_linked": false, "user": {"id": "usr_soc_123", "email": "rahul@gmail.com", "name": "Rahul Sharma", "phone": null, "is_new_user": true}}, "error": null}',
        "401 INVALID_SOCIAL_TOKEN",
        "Validate against Google/Apple certs. If phone_linked is false, app routes to /auth/link-phone."
    ],
    [
        "API-06",
        "/auth/link-phone",
        "POST",
        "Bearer Token",
        "None",
        '{"phone": "+919876543210", "request_id": "otp_req_link789", "otp": "591240"}',
        '{"success": true, "data": {"phone_linked": true, "user": {"id": "usr_soc_123", "phone": "+919876543210", "email": "rahul@gmail.com", "name": "Rahul Sharma"}}, "error": null}',
        "400 INVALID_OTP, 409 PHONE_ALREADY_LINKED",
        "Merges user profile if phone was already registered under a legacy account."
    ],
    [
        "API-07",
        "/home/feed",
        "GET",
        "Bearer Token",
        "None",
        "None (GET request)",
        '{"success": true, "data": {"active_ride": {"booking_id": "BK-2026-9812", "status": "PARTNER_ASSIGNED", "route": "Delhi to Agra", "pickup_time": "2026-09-10T06:00:00+05:30", "driver_name": "Vikram Singh", "vehicle_number": "DL 01 AB 1234"}, "banners": [{"id": "banner_1", "image_url": "https://cdn.../b1.png", "action_route": "OUTSTATION", "title": "Flat ₹300 off"}], "service_categories": [{"type": "ONE_WAY", "title": "One Way"}, {"type": "ROUND_TRIP", "title": "Round Trip"}, {"type": "AIRPORT", "title": "Airport"}], "recent_routes": [{"pickup": "Delhi", "drop": "Agra", "trip_type": "ROUND_TRIP", "distance_km": 235}]}, "error": null}',
        "401 Unauthorized",
        "If active_ride exists, Home screen renders sticky top tracking card with 1-tap navigation."
    ],
    [
        "API-08",
        "/location/autocomplete",
        "GET",
        "Bearer Token",
        "query (string, required), latitude (float, optional), longitude (float, optional)",
        "None (GET request)",
        '{"success": true, "data": {"predictions": [{"place_id": "ChIJL_PO_qq7jzkR6kP1kQ", "main_text": "IGI Airport", "secondary_text": "New Delhi, India", "latitude": 28.5562, "longitude": 77.1000}]}, "error": null}',
        "400 MISSING_QUERY",
        "Debounced at 300ms on frontend. Keeps Google Places API key secure on backend."
    ],
    [
        "API-09",
        "/location/reverse-geocode",
        "GET",
        "Bearer Token",
        "lat (float, required), lng (float, required)",
        "None (GET request)",
        '{"success": true, "data": {"formatted_address": "Block B, Connaught Place, New Delhi", "short_name": "Connaught Place", "city": "New Delhi", "state": "Delhi", "latitude": 28.6139, "longitude": 77.2090}, "error": null}',
        "400 INVALID_COORDINATES",
        "Invoked when user moves map pin or drops pin on current GPS location."
    ],
    [
        "API-10",
        "/outstation/popular-routes",
        "GET",
        "Bearer Token",
        "origin_city (string, optional, default: Delhi)",
        "None (GET request)",
        '{"success": true, "data": {"routes": [{"id": "del_agr", "origin": "Delhi NCR", "destination": "Agra", "distance_km": 230, "starting_fare": 2499, "image_url": "https://cdn.../agra.jpg"}, {"id": "del_jpr", "origin": "Delhi NCR", "destination": "Jaipur", "distance_km": 280, "starting_fare": 2999, "image_url": "https://cdn.../jaipur.jpg"}]}, "error": null}',
        "400 Bad Request",
        "Powers the quick city selection pills on the Outstation screen."
    ],
    [
        "API-11",
        "/route/calculate",
        "POST",
        "Bearer Token",
        "None",
        '{"pickup": {"address": "Connaught Place, New Delhi", "latitude": 28.6315, "longitude": 77.2167}, "drop": {"address": "Taj East Gate, Agra", "latitude": 27.1731, "longitude": 78.0421}, "waypoints": []}',
        '{"success": true, "data": {"distance_km": 232.5, "duration_minutes": 215, "formatted_duration": "3h 35m", "toll_count": 3, "estimated_tolls_inr": 415, "encoded_polyline": "a~l~FjkztO_@e@..."}, "error": null}',
        "400 UNROUTABLE_PATH",
        "Returns encoded polyline for drawing path on map and tolls calculation."
    ],
    [
        "API-12",
        "/rides/available-slots",
        "GET",
        "Bearer Token",
        "date (string YYYY-MM-DD, required), trip_type (string, required)",
        "None (GET request)",
        '{"success": true, "data": {"selected_date": "2026-09-10", "is_immediate_booking_allowed": false, "min_advance_hours": 2, "available_slots": [{"time": "06:00 AM", "is_available": true, "night_charge_applies": false}, {"time": "11:30 PM", "is_available": true, "night_charge_applies": true}]}, "error": null}',
        "400 INVALID_DATE",
        "Outstation trips enforce min 2-hour advance booking; night charges flag 10 PM - 6 AM."
    ],
    [
        "API-13",
        "/cabs/search",
        "POST",
        "Bearer Token",
        "None",
        '{"pickup_lat": 28.6315, "pickup_lng": 77.2167, "drop_lat": 27.1731, "drop_lng": 78.0421, "pickup_datetime": "2026-09-10T06:00:00+05:30", "trip_type": "ROUND_TRIP", "return_datetime": "2026-09-11T20:00:00+05:30"}',
        '{"success": true, "data": {"vehicles": [{"vehicle_type_id": "SEDAN", "category_name": "Sedan", "model_examples": "Dzire, Etios", "seating_capacity": 4, "luggage_capacity": 2, "ac": true, "total_fare": 5499, "discounted_fare": 4999, "per_km_rate": 11.5, "eta_minutes": 15, "badge": "Most Popular", "image_url": "https://cdn.../sedan.png"}]}, "error": null}',
        "400 NO_CABS_AVAILABLE",
        "Calculates pricing for Sedan, SUV, Innova Crysta; returns badge for top choices."
    ],
    [
        "API-14",
        "/cabs/{vehicleTypeId}/details",
        "GET",
        "Bearer Token",
        "vehicleTypeId (path param, e.g. SEDAN, SUV)",
        "None (GET request)",
        '{"success": true, "data": {"vehicle_type_id": "SEDAN", "name": "Sedan (Dzire / Etios)", "description": "Comfortable AC sedan for 4 passengers.", "specifications": {"passengers": 4, "large_bags": 2, "small_bags": 1, "ac": true, "carrier": false}, "inclusions": ["Base fare for 465 km", "Driver allowance", "Tolls & State permit", "GST 5%"], "exclusions": ["Parking charges", "Extra km beyond 465 km @ ₹11.5/km"], "cancellation_policy": "Free cancellation up to 6 hours before pickup."}, "error": null}',
        "404 VEHICLE_NOT_FOUND",
        "Renders Review Cab Details screen cards: vehicle specs, inclusions, exclusions, cancel terms."
    ],
    [
        "API-15",
        "/booking/fare-quote",
        "POST",
        "Bearer Token",
        "None",
        '{"vehicle_type_id": "SEDAN", "distance_km": 465.0, "trip_type": "ROUND_TRIP", "coupon_code": "WELCOME100", "pickup_datetime": "2026-09-10T06:00:00+05:30"}',
        '{"success": true, "data": {"base_fare": 4799, "distance_charges": 0, "driver_allowance": 400, "toll_and_taxes": 415, "gst_amount": 280.70, "discount_amount": 100.00, "total_fare": 5794.70, "round_off_fare": 5795, "advance_payable_options": {"twenty_percent": 1159, "full_amount": 5795}}, "error": null}',
        "400 CALCULATION_ERROR",
        "Outputs both 20% advance option and 100% full pay amount; accounts for coupon discount."
    ],
    [
        "API-16",
        "/coupons/apply",
        "POST",
        "Bearer Token",
        "None",
        '{"code": "GOCAB300", "trip_type": "ROUND_TRIP", "order_amount": 5499}',
        '{"success": true, "data": {"code": "GOCAB300", "is_valid": true, "discount_amount": 300, "message": "₹300 discount applied successfully."}, "error": null}',
        "400 COUPON_EXPIRED, 400 MIN_ORDER_NOT_MET",
        "Clear validation message shown on screen if minimum cart value not met."
    ],
    [
        "API-17",
        "/booking/create",
        "POST",
        "Bearer Token",
        "None",
        '{"trip_type": "ROUND_TRIP", "vehicle_type_id": "SEDAN", "pickup": {"address": "Delhi", "latitude": 28.63, "longitude": 77.21}, "drop": {"address": "Agra", "latitude": 27.17, "longitude": 78.04}, "pickup_datetime": "2026-09-10T06:00:00+05:30", "return_datetime": "2026-09-11T20:00:00+05:30", "passenger_name": "Rahul Sharma", "passenger_phone": "+919876543210", "coupon_code": "GOCAB300", "payment_mode": "ADVANCE_ONLINE"}',
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "booking_status": "PAYMENT_PENDING", "amount_to_pay": 1159, "total_fare": 5495, "remaining_balance": 4336, "payment_gateway": {"provider": "RAZORPAY", "order_id": "order_RzP109", "key_id": "rzp_live_xxx", "currency": "INR", "amount_in_paise": 115900}}, "error": null}',
        "400 INVALID_BOOKING_PARAMS, 409 DUPLICATE",
        "If payment_mode is PAY_TO_DRIVER, gateway is null and status is immediately CONFIRMED."
    ],
    [
        "API-18",
        "/payment/verify",
        "POST",
        "Bearer Token",
        "None",
        '{"booking_id": "BK-2026-9812", "payment_id": "pay_RzP56789", "order_id": "order_RzP109", "signature": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"}',
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "payment_status": "PAID", "booking_status": "CONFIRMED", "advance_paid": 1159, "remaining_balance": 4336, "receipt_id": "RCP-2026-4412"}, "error": null}',
        "400 PAYMENT_VERIFICATION_FAILED",
        "Validates HMAC SHA256 signature using Razorpay Secret. On failure, shows Payment Failed screen."
    ],
    [
        "API-19",
        "/booking/{bookingId}/status",
        "GET",
        "Bearer Token",
        "bookingId (path param, string)",
        "None (GET request)",
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "status": "ASSIGNED", "start_trip_otp": "7421", "driver": {"id": "drv_8841", "name": "Vikram Singh", "phone": "+919811122233", "rating": 4.85, "photo_url": "https://cdn.../vikram.jpg", "vehicle": {"model": "Maruti Dzire", "license_plate": "DL 01 AB 1234"}}}, "error": null}',
        "404 BOOKING_NOT_FOUND",
        "Polled by mobile frontend every 4-5s while radar animation is active."
    ],
    [
        "API-20",
        "/booking/{bookingId}",
        "GET",
        "Bearer Token",
        "bookingId (path param, string)",
        "None (GET request)",
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "status": "CONFIRMED", "trip_type": "ROUND_TRIP", "pickup": {"address": "Delhi", "datetime": "2026-09-10T06:00:00+05:30"}, "drop": {"address": "Agra", "datetime": "2026-09-11T20:00:00+05:30"}, "start_trip_otp": "7421", "vehicle": {"name": "Sedan", "registration": "DL 01 AB 1234"}, "driver": {"name": "Vikram Singh", "phone": "+919811122233"}, "payment_summary": {"total_fare": 5495, "amount_paid": 1159, "balance_due": 4336}}, "error": null}',
        "404 BOOKING_NOT_FOUND",
        "Populates confirmed ticket screen with driver contact, vehicle number, and trip summary."
    ],
    [
        "API-21",
        "/booking/{bookingId}/cancel",
        "POST",
        "Bearer Token",
        "bookingId (path param, string)",
        '{"reason": "Change of travel plans"}',
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "status": "CANCELLED", "cancellation_fee": 0, "refund_amount": 1159, "refund_status": "PROCESSING", "refund_reference": "ref_9018420", "expected_by": "3-5 business days"}, "error": null}',
        "400 CANCELLATION_NOT_PERMITTED",
        "Free cancellation > 6h before pickup; 50% fee within 2-6h. Refund pushed to gateway."
    ],
    [
        "API-22",
        "/booking/{bookingId}/payment-schedule",
        "GET",
        "Bearer Token",
        "bookingId (path param, string)",
        "None (GET request)",
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "total_fare": 5495, "milestones": [{"id": "m1", "title": "Advance Deposit (20%)", "amount": 1159, "status": "PAID", "paid_at": "2026-09-08T11:05:00+05:30"}, {"id": "m2", "title": "At Pickup (50%)", "amount": 2748, "status": "DUE", "due_at": "2026-09-10T06:00:00+05:30"}, {"id": "m3", "title": "At Drop (30%)", "amount": 1588, "status": "PENDING", "due_at": "2026-09-11T20:00:00+05:30"}]}, "error": null}',
        "404 BOOKING_NOT_FOUND",
        "Renders milestone timeline on Trip Payment Schedule screen."
    ],
    [
        "API-23",
        "/booking/{bookingId}/pay-milestone",
        "POST",
        "Bearer Token",
        "bookingId (path param, string)",
        '{"milestone_id": "m2", "payment_method": "ONLINE"}',
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "milestone_id": "m2", "amount": 2748, "payment_gateway": {"provider": "RAZORPAY", "order_id": "order_m2_99812", "key_id": "rzp_live_xxx", "amount_in_paise": 274800}}, "error": null}',
        "400 ALREADY_PAID, 404 MILESTONE_NOT_FOUND",
        "Generates payment order for subsequent milestone instalments while on trip."
    ],
    [
        "API-24",
        "/payment/verify-otp",
        "POST",
        "Bearer Token",
        "None",
        '{"booking_id": "BK-2026-9812", "milestone_id": "m2", "otp": "6109"}',
        '{"success": true, "data": {"booking_id": "BK-2026-9812", "milestone_id": "m2", "status": "PAID", "message": "Payment verified and recorded successfully."}, "error": null}',
        "400 INVALID_PAYMENT_OTP",
        "Ensures cash collected by driver is authenticated and confirmed on passenger device."
    ]
]

# -------------------------------------------------------------
# TAB 3: Screen to API Mapping
# -------------------------------------------------------------
tab3_headers = [
    "Screen File / Name",
    "Screen Category",
    "Primary API Call",
    "Secondary API Call",
    "Trigger Action",
    "UI Behavior / Success State",
    "Error / Retry Handling"
]

tab3_rows = [
    [
        "SessionCheckScreen.kt", "Splash & Auth",
        "GET /auth/session", "None",
        "App Start (On Launch)",
        "If token valid: proceed to Home; If active ride: route to Booking Status; Else: Login",
        "If 401: clear local cache, go to Login"
    ],
    [
        "LoginScreen.kt", "Splash & Auth",
        "POST /auth/send-otp", "POST /auth/social-login",
        "Tap 'Get OTP' or Social Button",
        "Navigate to OTP Verification screen with countdown timer (60s)",
        "Show inline red error text; enable retry"
    ],
    [
        "OtpVerificationScreen.kt", "Splash & Auth",
        "POST /auth/verify-otp", "POST /auth/resend-otp",
        "Enter 6th digit of OTP",
        "Save JWT token to local encrypted storage, transition to Home screen",
        "Vibrate device, clear OTP boxes, show 'Incorrect code'"
    ],
    [
        "SocialLinkPhoneScreen.kt", "Splash & Auth",
        "POST /auth/send-otp", "POST /auth/link-phone",
        "Tap 'Verify Phone'",
        "Link phone to social account and complete onboarding",
        "Show error message if phone already in use"
    ],
    [
        "HomeScreen.kt", "Discovery",
        "GET /home/feed", "GET /outstation/popular-routes",
        "Screen Initial Composition",
        "Render banners carousel, active trip card, service type pills, recent routes",
        "Pull-to-refresh; render cached offline feed if offline"
    ],
    [
        "SearchDestinationScreen.kt", "Location & Maps",
        "GET /location/autocomplete", "None",
        "Typing in search box (300ms debounce)",
        "Display suggestions list with landmark & distance",
        "Show 'No places found' empty state"
    ],
    [
        "ConfirmPickupLocationMapScreen.kt", "Location & Maps",
        "GET /location/reverse-geocode", "POST /route/calculate",
        "Map Drag End / Pin Drop",
        "Update address card with short address and formatted string",
        "Keep previous valid address if geocoding fails"
    ],
    [
        "ScheduleRideScreen.kt", "Scheduling",
        "GET /rides/available-slots", "None",
        "Date or Time picker selection",
        "Enable 'Confirm Time' button, display night fare alert if after 10 PM",
        "Disable unavailable slots"
    ],
    [
        "VehiclePartnerOptionsScreen.kt", "Vehicle Selection",
        "POST /cabs/search", "None",
        "Screen Load after Route Selection",
        "Render vehicle category cards (Sedan, SUV, Innova) with prices and ETAs",
        "Show 'No cabs available' banner with retry button"
    ],
    [
        "CabDetailScreen.kt", "Vehicle Selection",
        "GET /cabs/{vehicleTypeId}/details", "POST /booking/fare-quote",
        "Card Tap from Vehicle Options",
        "Display specs (seating, luggage), inclusions, exclusions, cancellation policy",
        "Show retry card if details fail to load"
    ],
    [
        "ApplyCouponScreen.kt", "Fare & Booking",
        "POST /coupons/apply", "None",
        "Tap 'Apply' button",
        "Update total fare with discount; return to Summary screen",
        "Show red snackbar with reason (e.g. min order required)"
    ],
    [
        "BookingConfirmationScreen.kt", "Booking & Payment",
        "POST /booking/create", "POST /payment/verify",
        "Tap 'Proceed to Pay' button",
        "Launch Razorpay / Gateway SDK with order details",
        "Show payment failure screen with retry"
    ],
    [
        "PaymentProcessingScreen.kt", "Booking & Payment",
        "POST /payment/verify", "None",
        "Payment Gateway Success Callback",
        "Verify signature, transition to Partner Searching Screen",
        "Redirect to Payment Failed Screen if signature invalid"
    ],
    [
        "PartnerSearchingScreen.kt", "Dispatch",
        "GET /booking/{bookingId}/status", "None",
        "Poll every 4 seconds",
        "Transition to Partner Assigned when status == 'ASSIGNED'",
        "If timeout > 60s: show prompt to expand search radius"
    ],
    [
        "PartnerAssignedScreen.kt", "Dispatch",
        "GET /booking/{bookingId}", "None",
        "On Driver Assignment",
        "Display driver name, rating, vehicle license plate, and start OTP",
        "Allow direct tap to call driver"
    ],
    [
        "RefundInitiatedScreen.kt", "Cancellation",
        "POST /booking/{bookingId}/cancel", "None",
        "Tap 'Confirm Cancellation'",
        "Show refund amount, transaction ref, and 3-5 days ETA",
        "Show alert if cancellation fee applies"
    ],
    [
        "TripPaymentScheduleScreen.kt", "Milestones",
        "GET /booking/{bookingId}/payment-schedule", "POST /booking/{bookingId}/pay-milestone",
        "Tap 'View Payment Schedule'",
        "Show milestone timeline with paid, due, and pending instalments",
        "Pull to refresh current balance"
    ],
    [
        "PaymentOtpVerificationScreen.kt", "Milestones",
        "POST /payment/verify-otp", "None",
        "Driver collects cash & customer enters verification OTP",
        "Mark milestone as PAID and update receipt",
        "Show 'Incorrect OTP' attempt count"
    ]
]

# -------------------------------------------------------------
# TAB 4: Environment & Keys Required
# -------------------------------------------------------------
tab4_headers = [
    "Item / Key",
    "Type",
    "Environment",
    "Recommended Value / Format",
    "Purpose / Usage",
    "Status"
]

tab4_rows = [
    [
        "API Base URL", "Network Endpoint", "Staging",
        "https://staging-api.goindiacab.com/api/v1",
        "Primary API host for KMP client network requests", "Pending from Backend"
    ],
    [
        "API Base URL", "Network Endpoint", "Production",
        "https://api.goindiacab.com/api/v1",
        "Live production API cluster with CDN edge caching", "Pending from Backend"
    ],
    [
        "Standard Response Envelope", "Protocol Standard", "All",
        '{"success": boolean, "data": object|null, "error": {"code": string, "message": string}|null}',
        "Universal JSON wrapper for all 200/400/500 responses", "Agreed Standard"
    ],
    [
        "Auth Header Format", "Header", "All",
        "Authorization: Bearer <jwt_access_token>",
        "Sent in all authenticated endpoint requests", "Agreed Standard"
    ],
    [
        "SMS Gateway Test Mode", "Credentials", "Staging",
        "Fixed OTP: 123456 or 000000 for test numbers (+919876543210)",
        "Prevents SMS gateway costs and OTP delays during development & QA", "Requested"
    ],
    [
        "Razorpay Test Key ID", "Gateway Credential", "Staging",
        "rzp_test_xxxxxxxxxxxxxxxx",
        "Used for sandbox payments on Android/iOS/Web", "Requested"
    ],
    [
        "Razorpay Test Key Secret", "Gateway Credential", "Staging (Backend)",
        "Stored securely on backend for HMAC SHA256 signature verification",
        "Required for /payment/verify endpoint signature validation", "Backend Internal"
    ],
    [
        "Google Places API Key", "Map Credential", "Backend Server",
        "Google Cloud Console Server Key (IP Restricted)",
        "Backend uses this to proxy autocomplete & reverse geocode queries", "Backend Internal"
    ]
]

# Helper to write CSV
def write_csv_file(filename, headers, rows):
    path = os.path.join(OUTPUT_DIR, filename)
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(headers)
        writer.writerows(rows)
    print(f"Written: {path}")

# Helper to write TSV (Tab Separated Values - pastes cleanly into Google Sheets cells!)
def write_tsv_file(filename, headers, rows):
    path = os.path.join(OUTPUT_DIR, filename)
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f, delimiter="\t")
        writer.writerow(headers)
        writer.writerows(rows)
    print(f"Written TSV: {path}")

# 1. Write individual Tab CSVs
write_csv_file("01_API_Master_Tracker.csv", tab1_headers, tab1_rows)
write_csv_file("02_Endpoint_Specifications.csv", tab2_headers, tab2_rows)
write_csv_file("03_Screen_to_API_Mapping.csv", tab3_headers, tab3_rows)
write_csv_file("04_Environment_Config.csv", tab4_headers, tab4_rows)

# 2. Write individual Tab TSVs for 1-click clipboard paste into Google Sheets
write_tsv_file("01_API_Master_Tracker.tsv", tab1_headers, tab1_rows)
write_tsv_file("02_Endpoint_Specifications.tsv", tab2_headers, tab2_rows)
write_tsv_file("03_Screen_to_API_Mapping.tsv", tab3_headers, tab3_rows)
write_tsv_file("04_Environment_Config.tsv", tab4_headers, tab4_rows)

# 3. Write Master Consolidated CSV
master_headers = [
    "API ID", "Module", "Screen Name", "HTTP Method", "Endpoint",
    "Auth", "Priority", "Phase", "Status", "Request Sample",
    "Success Response Sample", "Error Codes", "Business Rules"
]
master_rows = []
for r1, r2 in zip(tab1_rows, tab2_rows):
    master_rows.append([
        r1[0], # ID
        r1[1], # Module
        r1[2], # Screen
        r1[3], # Method
        r1[4], # Endpoint
        r1[5], # Auth
        r1[6], # Priority
        r1[7], # Phase
        r1[8], # Status
        r2[5], # Request sample
        r2[6], # Response sample
        r2[7], # Error codes
        r2[8]  # Business rules
    ])
write_csv_file("GoIndiaCab_All_APIs_Master_Google_Sheet.csv", master_headers, master_rows)
write_tsv_file("GoIndiaCab_All_APIs_Master_Google_Sheet.tsv", master_headers, master_rows)

print("All CSV and TSV spreadsheets successfully generated!")
