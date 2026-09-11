package com.example.goindiacab.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.ChevronRightIcon
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.*
import com.example.goindiacab.viewmodel.ReferEarnViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Styling tokens matching Refer&Earn.svg
private val ReferEarnHeaderDark = Color(0xFF020C1B)
private val ReferEarnHeroBg = Color(0xFF0A1A3A)
private val ReferEarnHeroIconBg = Color(0xFF162E5C)
private val ReferEarnScreenBg = Color(0xFFF4F6F9)
private val ReferEarnOrange = Color(0xFFFF6B00)
private val ReferEarnTextDark = Color(0xFF0A1A3A)
private val ReferEarnTextSub = Color(0xFF626D7F)
private val ReferEarnWhatsAppGreen = Color(0xFF22C55E)
private val ReferEarnSmsBg = Color(0xFFFFF5ED)

@Composable
fun ReferEarnScreen(
    viewModel: ReferEarnViewModel = remember { AppContainer.createReferEarnViewModel() },
    onBackClick: () -> Unit = {},
    onWhatsAppShareClick: (String) -> Unit = {},
    onSmsShareClick: (String) -> Unit = {},
    onEnterReferralCodeClick: () -> Unit = {},
    onViewWalletClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        containerColor = ReferEarnScreenBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ReferEarnHeaderDark)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Refer & Earn",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { /* Overflow menu */ },
                        contentAlignment = Alignment.Center
                    ) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dark Hero Card matching Refer&Earn.svg
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = ReferEarnHeroBg,
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(ReferEarnHeroIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_gift),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Spread safety. Earn cash.",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = outfitFontFamily(),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Get ₹${uiState.rewardPerReferralInr} directly in your wallet for every friend who completes their first intercity trip.",
                        fontSize = 13.sp,
                        fontFamily = dmSansFontFamily(),
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Referral Code Container Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "YOUR REFERRAL CODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF4F6F9))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.referralCode,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = ReferEarnTextDark,
                            letterSpacing = 1.sp
                        )

                        Button(
                            onClick = {
                                viewModel.copyReferralCode { code ->
                                    clipboardManager.setText(AnnotatedString(code))
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isCopied) Color(0xFF10B981) else ReferEarnOrange
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            AnimatedContent(
                                targetState = uiState.isCopied,
                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                            ) { isCopied ->
                                Text(
                                    text = if (isCopied) "Copied!" else "Copy",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = outfitFontFamily(),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Dual Stats Grid (Friends Referred & Earned Rewards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Friends Referred Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${uiState.friendsReferred}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = ReferEarnOrange
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Friends Referred",
                            fontSize = 12.sp,
                            fontFamily = dmSansFontFamily(),
                            color = ReferEarnTextSub,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Earned Rewards Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "₹${uiState.earnedRewardsInr}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = outfitFontFamily(),
                            color = ReferEarnTextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Earned Rewards",
                            fontSize = 12.sp,
                            fontFamily = dmSansFontFamily(),
                            color = ReferEarnTextSub,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action: Share via WhatsApp
            Button(
                onClick = {
                    viewModel.shareViaWhatsApp { msg ->
                        onWhatsAppShareClick(msg)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ReferEarnWhatsAppGreen),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Share via WhatsApp",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }

            // Action: Invite via SMS
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.5.dp, ReferEarnOrange, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                color = ReferEarnSmsBg,
                onClick = {
                    viewModel.inviteViaSms { msg ->
                        onSmsShareClick(msg)
                    }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Invite via SMS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = ReferEarnOrange
                    )
                }
            }

            // Enter Referral Code Entry Point
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onEnterReferralCodeClick),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 1.dp
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ReferEarnHeroIconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎁", fontSize = 16.sp)
                        }

                        Column {
                            Text(
                                text = "Have a Friend's Referral Code?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = ReferEarnTextDark
                            )
                            Text(
                                text = "Enter code to claim ₹250 signup bonus",
                                fontSize = 12.sp,
                                fontFamily = dmSansFontFamily(),
                                color = ReferEarnTextSub
                            )
                        }
                    }

                    ChevronRightIcon(size = 18.dp, color = ReferEarnOrange)
                }
            }

            // Wallet Shortcut
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onViewWalletClick),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 1.dp
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE6F0FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💳", fontSize = 16.sp)
                        }

                        Column {
                            Text(
                                text = "GoIndiaCab Wallet & Earnings",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = outfitFontFamily(),
                                color = ReferEarnTextDark
                            )
                            Text(
                                text = "View balance, redeem rewards & top-up",
                                fontSize = 12.sp,
                                fontFamily = dmSansFontFamily(),
                                color = ReferEarnTextSub
                            )
                        }
                    }

                    ChevronRightIcon(size = 18.dp, color = Color(0xFF0052CC))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun ReferEarnScreenPreview() {
    GoIndiaCabTheme {
        ReferEarnScreen()
    }
}
