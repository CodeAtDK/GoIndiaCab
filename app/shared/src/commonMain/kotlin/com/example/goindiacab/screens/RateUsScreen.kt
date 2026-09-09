package com.example.goindiacab.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.components.ChevronRightIcon
import com.example.goindiacab.components.StarIcon
import com.example.goindiacab.di.AppContainer
import com.example.goindiacab.theme.*
import com.example.goindiacab.viewmodel.RateUsViewModel
import goindiacab.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

// Styling tokens matching Rate Us.svg
private val RateUsHeaderDark = Color(0xFF020C1B)
private val RateUsScreenBg = Color(0xFFF4F6F9)
private val RateUsOrange = Color(0xFFFF6B00)
private val RateUsTextNavy = Color(0xFF0A1A3A)
private val RateUsTextSub = Color(0xFF626D7F)
private val RateUsStarInactive = Color(0xFFE5E7EB)

@Composable
fun RateUsScreen(
    viewModel: RateUsViewModel = remember { AppContainer.createRateUsViewModel() },
    onBackClick: () -> Unit = {},
    onRatePlayStoreClick: (Int) -> Unit = {},
    onMaybeLaterClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        containerColor = RateUsScreenBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RateUsHeaderDark)
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
                        text = "Rate Us",
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
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Illustration Elevation Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    RateUsPhone3DIllustration(
                        modifier = Modifier.size(200.dp, 190.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Heading & Subtitle
            Text(
                text = "Enjoying GoIndiaCab?",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = outfitFontFamily(),
                color = RateUsTextNavy,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your feedback helps us provide you the best intercity rides.",
                fontSize = 14.sp,
                fontFamily = dmSansFontFamily(),
                color = RateUsTextSub,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Interactive 5-Star Rating Row with Spring Scale Animations
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { starIndex ->
                    val isSelected = starIndex <= uiState.rating
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 0.95f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                    )

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.setRating(starIndex)
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        StarIcon(
                            size = 36.dp,
                            color = if (isSelected) RateUsOrange else RateUsStarInactive,
                            isFilled = isSelected
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap a star to rate us on Play Store",
                fontSize = 12.sp,
                fontFamily = dmSansFontFamily(),
                color = Color(0xFF8C97A7),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Primary Action: Rate on Play Store
            Button(
                onClick = {
                    viewModel.submitRating {
                        onRatePlayStoreClick(uiState.rating)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RateUsOrange),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Rate on Play Store",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFontFamily(),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Action: Maybe Later
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                onClick = onMaybeLaterClick
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Maybe Later",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outfitFontFamily(),
                        color = RateUsTextSub
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Custom Canvas rendering of the 3D smartphone with glowing stars matching Rate Us.svg
 */
@Composable
fun RateUsPhone3DIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Isometric Phone Base Shadow
        drawOval(
            color = Color(0xFF0052CC).copy(alpha = 0.12f),
            topLeft = Offset(w * 0.22f, h * 0.58f),
            size = androidx.compose.ui.geometry.Size(w * 0.56f, h * 0.28f)
        )

        // 3D Isometric Phone Body
        val phonePath = Path().apply {
            moveTo(w * 0.50f, h * 0.44f) // top vertex
            lineTo(w * 0.74f, h * 0.56f) // right vertex
            lineTo(w * 0.50f, h * 0.70f) // bottom vertex
            lineTo(w * 0.26f, h * 0.56f) // left vertex
            close()
        }

        // Phone screen gradient
        drawPath(
            path = phonePath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF5AC8FA), Color(0xFF007AFF), Color(0xFF0051C6)),
                start = Offset(w * 0.5f, h * 0.44f),
                end = Offset(w * 0.5f, h * 0.70f)
            )
        )

        // Phone bevel border
        drawPath(
            path = phonePath,
            color = Color(0xFF00388A),
            style = Stroke(width = 2.5.dp.toPx())
        )

        // Floating glowing stars
        val starCenters = listOf(
            Offset(w * 0.52f, h * 0.25f) to 14.dp.toPx(),
            Offset(w * 0.48f, h * 0.35f) to 18.dp.toPx(),
            Offset(w * 0.43f, h * 0.44f) to 12.dp.toPx(),
            Offset(w * 0.47f, h * 0.52f) to 15.dp.toPx(),
            Offset(w * 0.56f, h * 0.38f) to 11.dp.toPx(),
            Offset(w * 0.55f, h * 0.21f) to 8.dp.toPx()
        )

        starCenters.forEach { (center, starSize) ->
            drawStarPath(center, starSize, Color(0xFFFFB800), Color(0xFFFF8A00))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStarPath(
    center: Offset,
    size: Float,
    fillColor: Color,
    borderColor: Color
) {
    val path = Path()
    val innerRadius = size * 0.42f
    val outerRadius = size

    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = (i * 36 - 90) * (kotlin.math.PI / 180.0)
        val x = center.x + (radius * kotlin.math.cos(angle)).toFloat()
        val y = center.y + (radius * kotlin.math.sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    drawPath(path, fillColor, style = Fill)
    drawPath(path, borderColor, style = Stroke(width = 1.dp.toPx()))
}

@Preview
@Composable
fun RateUsScreenPreview() {
    GoIndiaCabTheme {
        RateUsScreen()
    }
}
