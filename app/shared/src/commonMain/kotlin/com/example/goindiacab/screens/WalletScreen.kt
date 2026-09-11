package com.example.goindiacab.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
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
private val BrandBlue = Color(0xFF0052CC)
private val BrandOrange = Color(0xFFFF6B00)
private val SuccessGreen = Color(0xFF10B981)
private val DebitRed = Color(0xFFEF4444)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

data class WalletTransaction(
    val id: String,
    val title: String,
    val date: String,
    val amount: Int,
    val isCredit: Boolean,
    val category: String
)

enum class WalletFilterTab(val label: String) {
    ALL("All"),
    CREDITED("Credits"),
    DEBITED("Debits")
}

/**
 * Screen: GoIndiaCab Wallet & Credits
 * Backed by API-68, API-69, API-70.
 * Displays balance, top-up shortcuts, referral reward redemption, and transaction history.
 */
@Composable
fun WalletScreen(
    onBackClick: () -> Unit = {},
    onReferralRedeemClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()
    var balance by remember { mutableStateOf(500) }
    var referralEarnings by remember { mutableStateOf(1500) }
    var selectedFilter by remember { mutableStateOf(WalletFilterTab.ALL) }
    var showTopupDialog by remember { mutableStateOf(false) }
    var customTopupAmount by remember { mutableStateOf("500") }

    val transactions = remember {
        mutableStateListOf(
            WalletTransaction("txn_1", "Referral Bonus Credited", "Today, 11:30 AM", 250, isCredit = true, "Referral"),
            WalletTransaction("txn_2", "Trip Payment - Delhi to Agra", "Yesterday, 04:15 PM", 1200, isCredit = false, "Ride"),
            WalletTransaction("txn_3", "Wallet Top-Up via UPI", "05 Sep 2026", 1000, isCredit = true, "Topup"),
            WalletTransaction("txn_4", "Trip Cancellation Refund", "28 Aug 2026", 520, isCredit = true, "Refund"),
            WalletTransaction("txn_5", "Trip Payment - Jaipur Tour", "15 Aug 2026", 2400, isCredit = false, "Ride")
        )
    }

    val filteredTransactions = remember(selectedFilter, transactions) {
        when (selectedFilter) {
            WalletFilterTab.ALL -> transactions
            WalletFilterTab.CREDITED -> transactions.filter { it.isCredit }
            WalletFilterTab.DEBITED -> transactions.filter { !it.isCredit }
        }
    }

    // Top-Up Dialog
    if (showTopupDialog) {
        AlertDialog(
            onDismissRequest = { showTopupDialog = false },
            title = {
                Text(
                    text = "Add Money to Wallet",
                    fontFamily = outfitFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Enter amount to top up via UPI, NetBanking or Credit/Debit Card.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        fontFamily = dmSansFontFamily()
                    )

                    OutlinedTextField(
                        value = customTopupAmount,
                        onValueChange = { customTopupAmount = it.filter { ch -> ch.isDigit() }.take(5) },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = TextDark) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(500, 1000, 2000).forEach { amt ->
                            OutlinedButton(
                                onClick = { customTopupAmount = amt.toString() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("+₹$amt", fontSize = 12.sp, color = BrandBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = customTopupAmount.toIntOrNull() ?: 500
                        balance += amt
                        transactions.add(
                            0,
                            WalletTransaction(
                                id = "txn_${kotlin.random.Random.nextInt(1000, 9999)}",
                                title = "Wallet Top-Up via UPI",
                                date = "Just now",
                                amount = amt,
                                isCredit = true,
                                category = "Topup"
                            )
                        )
                        showTopupDialog = false
                        toast("✓ ₹$amt added to your GoIndiaCab wallet!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Proceed to Pay", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopupDialog = false }) {
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
                        text = "GoIndiaCab Wallet",
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
            // 1. Hero Balance Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    shadowElevation = 3.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0052CC), Color(0xFF0A1A3A))
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AVAILABLE BALANCE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = Color.White.copy(alpha = 0.8f),
                                    letterSpacing = 0.8.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.18f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "100% Safe & Secure",
                                        fontSize = 11.sp,
                                        fontFamily = dmSansFontFamily(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "₹$balance.00",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = outfitFontFamily(),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showTopupDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                                ) {
                                    Text(
                                        text = "+ Add Money",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = Color.White
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showTopupDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, Color.White),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Text(
                                        text = "Top-Up via UPI",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = outfitFontFamily(),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Referral Rewards Redemption Banner
            if (referralEarnings > 0) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF7ED),
                        border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.5f))
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
                                    text = "Referral Cash Ready to Redeem",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "₹$referralEarnings earned from friend invites",
                                    fontSize = 12.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextMuted
                                )
                            }

                            Button(
                                onClick = {
                                    balance += referralEarnings
                                    transactions.add(
                                        0,
                                        WalletTransaction(
                                            id = "txn_${kotlin.random.Random.nextInt(1000, 9999)}",
                                            title = "Redeemed Referral Rewards",
                                            date = "Just now",
                                            amount = referralEarnings,
                                            isCredit = true,
                                            category = "Referral"
                                        )
                                    )
                                    referralEarnings = 0
                                    toast("✓ Transferred referral rewards to wallet balance!")
                                    onReferralRedeemClick()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Transfer", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // 3. Transactions Header & Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT TRANSACTIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        WalletFilterTab.values().forEach { tab ->
                            val isSelected = selectedFilter == tab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NavyDark else Color.White)
                                    .clickable { selectedFilter = tab }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tab.label,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // 4. Transaction List
            items(filteredTransactions, key = { it.id }) { txn ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                                    .background(
                                        if (txn.isCredit) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (txn.isCredit) "↓" else "↑",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (txn.isCredit) SuccessGreen else DebitRed
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = txn.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark
                                )
                                Text(
                                    text = txn.date,
                                    fontSize = 12.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextMuted
                                )
                            }
                        }

                        Text(
                            text = "${if (txn.isCredit) "+" else "-"}₹${txn.amount}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = if (txn.isCredit) SuccessGreen else DebitRed
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun WalletScreenPreview() {
    GoIndiaCabTheme {
        WalletScreen()
    }
}
