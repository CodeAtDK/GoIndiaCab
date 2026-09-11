package com.example.goindiacab.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
 * Screen: GST Tax Invoice & Trip Fare Receipt.
 * Backed by API-35.
 * Displays official compliant GST breakdown, tax invoice serial, SAC code, and milestone payment logs.
 */
@Composable
fun TripInvoiceScreen(
    bookingId: String = "GIC-849201",
    invoiceNumber: String = "INV-2026-9482",
    date: String = "20 Sep 2026",
    passengerName: String = "Rahul Sharma",
    passengerPhone: String = "+91 98765 43210",
    origin: String = "New Delhi Railway Station",
    destination: String = "Agra Cantt, Taj Nagari",
    distanceKm: Int = 230,
    vehicleType: String = "Sedan (Swift Dzire)",
    driverName: String = "Rajesh Kumar",
    totalFare: Int = 2499,
    advancePaid: Int = 250,
    remainingPaid: Int = 2249,
    onBackClick: () -> Unit = {},
    onShareInvoiceClick: () -> Unit = {},
    onDownloadPdfClick: () -> Unit = {}
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()

    // Indian GST calculation (5% GST: 2.5% CGST + 2.5% SGST on passenger transport SAC 9964)
    val tollCharges = 274.0
    val taxableFare = (totalFare - tollCharges) / 1.05
    val cgst = taxableFare * 0.025
    val sgst = taxableFare * 0.025

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
                        text = "GST Tax Invoice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onShareInvoiceClick) {
                        ShareIcon(
                            size = 20.dp,
                            color = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            toast("Sharing invoice $invoiceNumber...")
                            onShareInvoiceClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, BrandBlue)
                    ) {
                        Text(
                            text = "Share Receipt",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = BrandBlue
                        )
                    }

                    Button(
                        onClick = {
                            toast("✓ PDF Invoice downloaded to Downloads/GoIndiaCab-$bookingId.pdf")
                            onDownloadPdfClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Text(
                            text = "Download PDF",
                            fontSize = 14.sp,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Invoice Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header: GoIndiaCab Entity & GSTIN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "GoIndiaCab Technologies",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = TextDark
                            )
                            Text(
                                text = "GSTIN: 07AAACG1234F1Z8",
                                fontSize = 11.5.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted
                            )
                            Text(
                                text = "SAC Code: 9964 (Passenger Transport)",
                                fontSize = 11.sp,
                                fontFamily = dmSansFontFamily(),
                                color = TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PAID IN FULL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = SuccessGreen
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Invoice Metadata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("INVOICE NO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(invoiceNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(date, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("BILLED TO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(passengerName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            Text(passengerPhone, fontSize = 11.5.sp, color = TextMuted)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("BOOKING ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(bookingId, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandOrange)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Trip Route Summary
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("TRIP ROUTE & VEHICLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("$origin → $destination", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("$vehicleType • $distanceKm km • Driver: $driverName", fontSize = 12.sp, color = TextMuted)
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    // Taxable Breakdown Table
                    Text("TAX & FARE BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 0.5.sp)

                    InvoiceLineItem(
                        label = "Base Outstation Fare (Taxable)",
                        value = "₹${(taxableFare * 100).toInt() / 100.0}"
                    )
                    InvoiceLineItem(
                        label = "CGST (2.5%)",
                        value = "₹${(cgst * 100).toInt() / 100.0}"
                    )
                    InvoiceLineItem(
                        label = "SGST (2.5%)",
                        value = "₹${(sgst * 100).toInt() / 100.0}"
                    )
                    InvoiceLineItem(
                        label = "Tolls & Inter-State Entry Permits",
                        value = "₹$tollCharges"
                    )

                    HorizontalDivider(color = NavyDark.copy(alpha = 0.15f), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount Paid:", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                        Text("₹$totalFare.00", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = BrandBlue)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Milestone Payments Schedule Record
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("PAYMENT SETTLEMENT LOG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• 10% Advance Deposit (UPI)", fontSize = 12.sp, color = TextDark)
                                Text("₹$advancePaid", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• 90% Trip Settlement (Cards)", fontSize = 12.sp, color = TextDark)
                                Text("₹$remainingPaid", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InvoiceLineItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.5.sp, color = TextMuted, fontFamily = dmSansFontFamily())
        Text(value, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextDark, fontFamily = dmSansFontFamily())
    }
}

@Preview
@Composable
fun TripInvoiceScreenPreview() {
    GoIndiaCabTheme {
        TripInvoiceScreen()
    }
}
