package com.example.goindiacab.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goindiacab.theme.*
import goindiacab.app.shared.generated.resources.Res
import goindiacab.app.shared.generated.resources.ic_back_arrow
import goindiacab.app.shared.generated.resources.ic_cab
import org.jetbrains.compose.resources.painterResource

/**
 * Adaptive container that centers content and limits width to 480dp,
 * providing optimal layouts across smartphones, tablets, and foldables.
 */
@Composable
fun AdaptiveContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BackgroundWhite,
    backgroundBrush: androidx.compose.ui.graphics.Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val bgModifier = if (backgroundBrush != null) {
        Modifier.background(backgroundBrush)
    } else {
        Modifier.background(backgroundColor)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(bgModifier)
            .then(modifier),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            content = content
        )
    }
}

/**
 * The signature GoIndiaCab rounded orange squircle icon with the white taxi symbol.
 */
@Composable
fun AppLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    cornerRadius: Dp = 26.dp,
    iconSize: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(cornerRadius), ambientColor = BrandOrange.copy(alpha = 0.4f), spotColor = BrandOrange.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(cornerRadius))
            .background(BrandOrange),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_cab),
            contentDescription = "GoIndiaCab Logo",
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Brand Logo Text: "Go" + "India" (in BrandOrange) + "Cab"
 */
@Composable
fun BrandTextLogo(
    modifier: Modifier = Modifier,
    isLightOnDark: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = 38.sp
) {
    val primaryColor = if (isLightOnDark) TextWhite else TextDark
    val brandString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.ExtraBold)) {
            append("Go")
        }
        withStyle(style = SpanStyle(color = BrandOrange, fontWeight = FontWeight.ExtraBold)) {
            append("India")
        }
        withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.ExtraBold)) {
            append("Cab")
        }
    }

    Text(
        text = brandString,
        fontSize = fontSize,
        fontFamily = outfitFontFamily(),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

/**
 * Primary action button (Full width, rounded corners)
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrandBlue,
    contentColor: Color = TextWhite,
    enabled: Boolean = true,
    height: Dp = 56.dp,
    cornerRadius: Dp = 14.dp
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.45f),
            disabledContentColor = contentColor.copy(alpha = 0.7f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (enabled) 6.dp else 0.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = backgroundColor.copy(alpha = 0.3f),
                spotColor = backgroundColor.copy(alpha = 0.4f)
            )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = outfitFontFamily(),
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Pill style status badge (e.g., "SAFE & RELIABLE")
 */
@Composable
fun PillBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BadgePeachBg,
    textColor: Color = BadgeOrangeText
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = outfitFontFamily(),
            letterSpacing = 0.8.sp
        )
    }
}

/**
 * Top Bar Back Button (Grey squircle with 48dp minimum accessible touch target)
 */
@Composable
fun TopBarBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_back_arrow),
                contentDescription = "Back",
                tint = TextDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Top App Bar with back button and centered title
 */
@Composable
fun ScreenTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        TopBarBackButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Text(
            text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = outfitFontFamily(),
            color = TextDark,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Single OTP Digit Box
 */
@Composable
fun OtpBox(
    value: String,
    isFocused: Boolean = false,
    isError: Boolean = false,
    isExpired: Boolean = false,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isError -> ErrorRed
        isFocused -> BrandOrange
        value.isNotEmpty() -> BrandOrange
        else -> BorderGray
    }

    val textColor = when {
        isError -> ErrorRed
        else -> TextDark
    }

    val backgroundColor = when {
        isError -> ErrorBgLight.copy(alpha = 0.5f)
        isExpired -> SurfaceGray
        else -> BackgroundWhite
    }

    // Blinking cursor when focused and empty
    val infiniteTransition = rememberInfiniteTransition()
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .heightIn(min = 58.dp, max = 68.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isFocused || value.isNotEmpty() || isError) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value.isNotEmpty()) {
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = outfitFontFamily(),
                color = textColor
            )
        } else if (isFocused) {
            // Blinking cursor bar
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(26.dp)
                    .background(BrandOrange.copy(alpha = cursorAlpha))
            )
        }
    }
}

/**
 * Production-ready Interactive 4-Digit OTP Input
 * Handles keyboard input, auto-advancing, backspace, paste, and focus.
 */
@Composable
fun InteractiveOtpInput(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isExpired: Boolean = false,
    onComplete: (String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = otpValue,
                selection = TextRange(otpValue.length)
            )
        )
    }

    // Keep textFieldValue in sync whenever external otpValue changes
    LaunchedEffect(otpValue) {
        if (textFieldValue.text != otpValue) {
            textFieldValue = TextFieldValue(
                text = otpValue,
                selection = TextRange(otpValue.length)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isExpired) {
                    focusRequester.requestFocus()
                    textFieldValue = TextFieldValue(otpValue, TextRange(otpValue.length))
                }
            }
    ) {
        // Hidden BasicTextField capturing keyboard entry with guaranteed end cursor position
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newTfv ->
                val digitsOnly = newTfv.text.filter { it.isDigit() }.take(4)
                textFieldValue = TextFieldValue(
                    text = digitsOnly,
                    selection = TextRange(digitsOnly.length)
                )
                if (digitsOnly != otpValue) {
                    onOtpChange(digitsOnly)
                    if (digitsOnly.length == 4) {
                        onComplete(digitsOnly)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (otpValue.length == 4) onComplete(otpValue)
                }
            ),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
                        if (otpValue.isNotEmpty()) {
                            val newText = otpValue.dropLast(1)
                            textFieldValue = TextFieldValue(newText, TextRange(newText.length))
                            onOtpChange(newText)
                            true
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                },
            decorationBox = { }
        )

        // Visible 4 OTP Display Boxes with individual box click to focus/delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in 0 until 4) {
                val digit = if (i < otpValue.length) otpValue[i].toString() else ""
                val isFocused = !isExpired && (i == otpValue.length || (otpValue.length == 4 && i == 3))
                OtpBox(
                    value = digit,
                    isFocused = isFocused,
                    isError = isError,
                    isExpired = isExpired,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isExpired) {
                                focusRequester.requestFocus()
                                if (i < otpValue.length) {
                                    val newText = otpValue.take(i)
                                    textFieldValue = TextFieldValue(newText, TextRange(newText.length))
                                    onOtpChange(newText)
                                } else {
                                    textFieldValue = TextFieldValue(otpValue, TextRange(otpValue.length))
                                }
                            }
                        }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isExpired) {
            focusRequester.requestFocus()
        }
    }
}

/**
 * Social Auth Button (e.g. Google or Email)
 */
@Composable
fun SocialButton(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    cornerRadius: Dp = 12.dp
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.dp, BorderGray),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = BackgroundWhite,
            contentColor = TextDark
        ),
        modifier = modifier
            .height(height)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                tint = TextDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = outfitFontFamily(),
                color = TextDark
            )
        }
    }
}

/**
 * Modern "──── OR ────" Divider
 */
@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = BorderGray
        )
        Text(
            text = "OR",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = outfitFontFamily(),
            color = TextSubtle,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = BorderGray
        )
    }
}
