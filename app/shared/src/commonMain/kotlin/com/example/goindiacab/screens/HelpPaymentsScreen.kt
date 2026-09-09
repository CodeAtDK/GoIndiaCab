package com.example.goindiacab.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.*
import com.example.goindiacab.theme.BrandOrange
import com.example.goindiacab.theme.dmSansFontFamily
import com.example.goindiacab.theme.outfitFontFamily
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val TopBarNavy = Color(0xFF0A1128)
private val ColorBg = Color(0xFFF8FAFC)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

/**
 * Screen: Help - Payments & Refunds.
 * Explains advance payment schedule policies, live refund tracking,
 * fare disputes, and invoice downloads.
 */
@Composable
fun HelpPaymentsScreen(
    onBackClick: () -> Unit = {},
    onDisputeFareClick: (String) -> Unit = {},
    onDownloadInvoiceClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlatformBackHandler(enabled = true) {
        onBackClick()
    }

    val toast = rememberPlatformToast()
    var expandedFaqId by remember { mutableStateOf<String?>("pay_faq_1") }

    val paymentFaqs = remember {
        listOf(
            FaqItem(
                id = "pay_faq_1",
                question = "Why are all payments required in advance?",
                answer = "In accordance with GoIndiaCab highway safety and fleet reservation standards, every installment is collected in advance before each respective trip segment begins. This ensures your dedicated commercial vehicle, interstate permits, and driver partner are 100% reserved."
            ),
            FaqItem(
                id = "pay_faq_2",
                question = "How long does a refund take to reach my bank?",
                answer = "UPI and Netbanking refunds are credited back to your original source account within 2 to 4 business hours. Credit and debit card refunds typically settle in 2 to 3 banking days depending on your issuing bank."
            ),
            FaqItem(
                id = "pay_faq_3",
                question = "What if money was deducted but booking failed?",
                answer = "If your bank debited your account during an interrupted session, our automated payment reconciler triggers an instant auto-refund within 20 minutes with a reference SMS."
            ),
            FaqItem(
                id = "pay_faq_4",
                question = "Are state highway tolls included in the estimated fare?",
                answer = "Yes! All state entry taxes, national highway tolls, and green taxes are 100% pre-included in your booking quote. You never need to pay cash at toll plazas."
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ColorBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TopBarNavy
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
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
                        text = "Payments & Refunds",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Active Refund Status Tracker
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE REFUND STATUS",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFECFDF5)
                        ) {
                            Text(
                                text = "REFUND PROCESSED",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Refund for TRIP-104 (Cancelled)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Arn: 49201938210 • UPI Source Bank",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = "₹850",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF059669)
                        )
                    }

                    Text(
                        text = "✓ Credit dispatched to your original payment mode on 15 Aug. If not reflected, please check your bank UTR statement.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF059669),
                        fontFamily = dmSansFontFamily()
                    )
                }
            }

            // Section 2: Advance Payment Rule Explainer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF7ED),
                border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🛡️", fontSize = 16.sp)
                        Text(
                            text = "100% Advance Payment Policy",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = outfitFontFamily(),
                            color = BrandOrange
                        )
                    }

                    Text(
                        text = "For safety and dedicated commercial fleet allocation, all GoIndiaCab rides operate on sequential advance milestones (e.g. 10% Booking Advance ➔ 40% Trip Start ➔ 30% Mid-Trip ➔ 20% Final Leg). No post-paid cash dues remain upon destination arrival.",
                        fontSize = 12.5.sp,
                        fontFamily = dmSansFontFamily(),
                        color = TextDark,
                        lineHeight = 17.sp
                    )
                }
            }

            // Section 3: Quick Action Disputes
            Text(
                text = "PAYMENT DISPUTES & INVOICES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PaymentDisputeActionRow(
                        title = "Driver asked for cash tolls / extra payment",
                        onClick = { onDisputeFareClick("Extra Cash Demanded") }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    PaymentDisputeActionRow(
                        title = "Double deduction or bank debited twice",
                        onClick = { onDisputeFareClick("Double Deduction") }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    PaymentDisputeActionRow(
                        title = "Download GST Invoice with Tax Breakdown",
                        onClick = {
                            toast("Downloading official GST Tax Invoice PDF...")
                            onDownloadInvoiceClick()
                        }
                    )
                }
            }

            // Section 4: Payment FAQs
            Text(
                text = "PAYMENT FAQS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                paymentFaqs.forEach { faq ->
                    val isExpanded = expandedFaqId == faq.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFaqId = if (isExpanded) null else faq.id },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.question,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = TextDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (isExpanded) "▲" else "▼",
                                    fontSize = 12.sp,
                                    color = BrandOrange
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Text(
                                    text = faq.answer,
                                    fontSize = 13.sp,
                                    fontFamily = dmSansFontFamily(),
                                    color = TextMuted,
                                    lineHeight = 18.sp
                                )
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
private fun PaymentDisputeActionRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = dmSansFontFamily(),
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
        ChevronRightIcon(size = 14.dp, color = Color(0xFF94A3B8))
    }
}
