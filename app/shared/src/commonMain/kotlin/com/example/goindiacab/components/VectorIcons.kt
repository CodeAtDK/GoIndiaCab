package com.example.goindiacab.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.width
        val strokeWidth = 2.dp.toPx()
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(s * 0.25f, s * 0.25f),
            end = androidx.compose.ui.geometry.Offset(s * 0.75f, s * 0.75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(s * 0.75f, s * 0.25f),
            end = androidx.compose.ui.geometry.Offset(s * 0.25f, s * 0.75f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ChevronRightIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFF626D7F)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.35f, h * 0.22f)
            lineTo(w * 0.65f, h * 0.50f)
            lineTo(w * 0.35f, h * 0.78f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun ArrowRightIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFFFF6B00)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.5f),
            end = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        val path = Path().apply {
            moveTo(w * 0.55f, h * 0.25f)
            lineTo(w * 0.8f, h * 0.5f)
            lineTo(w * 0.55f, h * 0.75f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun CalendarIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        // Outline box
        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.22f),
            size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.65f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            style = Stroke(width = stroke)
        )
        // Top line
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.42f),
            end = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.42f),
            strokeWidth = stroke
        )
        // Left prong
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.35f, h * 0.12f),
            end = androidx.compose.ui.geometry.Offset(w * 0.35f, h * 0.25f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Right prong
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.12f),
            end = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.25f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun PersonIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        // Head circle
        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.32f),
            style = Stroke(width = stroke)
        )
        // Body arc
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.82f)
            cubicTo(w * 0.2f, h * 0.62f, w * 0.8f, h * 0.62f, w * 0.8f, h * 0.82f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun HeartIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.8f)
            cubicTo(w * 0.1f, h * 0.5f, w * 0.15f, h * 0.2f, w * 0.35f, h * 0.2f)
            cubicTo(w * 0.45f, h * 0.2f, w * 0.5f, h * 0.28f, w * 0.5f, h * 0.32f)
            cubicTo(w * 0.5f, h * 0.28f, w * 0.55f, h * 0.2f, w * 0.65f, h * 0.2f)
            cubicTo(w * 0.85f, h * 0.2f, w * 0.9f, h * 0.5f, w * 0.5f, h * 0.8f)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun BellIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.68f)
            lineTo(w * 0.78f, h * 0.68f)
            cubicTo(w * 0.74f, h * 0.55f, w * 0.70f, h * 0.35f, w * 0.5f, h * 0.22f)
            cubicTo(w * 0.30f, h * 0.35f, w * 0.26f, h * 0.55f, w * 0.22f, h * 0.68f)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Clapper bottom
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.82f),
            end = androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.82f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.62f, h * 0.38f)
            lineTo(w * 0.88f, h * 0.40f)
            lineTo(w * 0.68f, h * 0.58f)
            lineTo(w * 0.74f, h * 0.85f)
            lineTo(w * 0.5f, h * 0.71f)
            lineTo(w * 0.26f, h * 0.85f)
            lineTo(w * 0.32f, h * 0.58f)
            lineTo(w * 0.12f, h * 0.40f)
            lineTo(w * 0.38f, h * 0.38f)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun UsersIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()
        // Primary user head & body
        drawCircle(
            color = color,
            radius = w * 0.16f,
            center = androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.35f),
            style = Stroke(width = stroke)
        )
        val p1 = Path().apply {
            moveTo(w * 0.18f, h * 0.82f)
            cubicTo(w * 0.18f, h * 0.64f, w * 0.66f, h * 0.64f, w * 0.66f, h * 0.82f)
        }
        drawPath(p1, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // Secondary user offset
        drawCircle(
            color = color,
            radius = w * 0.13f,
            center = androidx.compose.ui.geometry.Offset(w * 0.72f, h * 0.32f),
            style = Stroke(width = stroke)
        )
        val p2 = Path().apply {
            moveTo(w * 0.66f, h * 0.60f)
            cubicTo(w * 0.76f, h * 0.58f, w * 0.86f, h * 0.68f, w * 0.86f, h * 0.80f)
        }
        drawPath(p2, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
}

@Composable
fun SettingsIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = stroke)
        )
        drawCircle(
            color = color,
            radius = w * 0.36f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = stroke)
        )
    }
}

@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.38f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = stroke)
        )
        // Dot
        drawCircle(
            color = color,
            radius = 1.5.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.34f),
            style = Fill
        )
        // Stem
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.46f),
            end = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.70f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ThumbUpIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFF0A1A3A)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.25f, h * 0.85f)
            lineTo(w * 0.75f, h * 0.85f)
            lineTo(w * 0.85f, h * 0.50f)
            lineTo(w * 0.55f, h * 0.50f)
            lineTo(w * 0.60f, h * 0.20f)
            lineTo(w * 0.45f, h * 0.20f)
            lineTo(w * 0.25f, h * 0.45f)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun LogoutIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color = Color(0xFFE53E3E)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        // Door bracket
        val path = Path().apply {
            moveTo(w * 0.55f, h * 0.2f)
            lineTo(w * 0.25f, h * 0.2f)
            lineTo(w * 0.25f, h * 0.8f)
            lineTo(w * 0.55f, h * 0.8f)
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // Arrow exiting
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.40f, h * 0.50f),
            end = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.50f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        val arrowHead = Path().apply {
            moveTo(w * 0.70f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.50f)
            lineTo(w * 0.70f, h * 0.65f)
        }
        drawPath(arrowHead, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun HamburgerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.2.dp.toPx()
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.30f),
            end = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.30f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.50f),
            end = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.50f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.70f),
            end = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.70f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun EditPencilIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFFFF6B00)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.65f)
            close()
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.55f, h * 0.25f),
            end = androidx.compose.ui.geometry.Offset(w * 0.75f, h * 0.45f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun CameraIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    color: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        // Camera body
        val body = Path().apply {
            moveTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.32f, h * 0.35f)
            lineTo(w * 0.40f, h * 0.22f)
            lineTo(w * 0.60f, h * 0.22f)
            lineTo(w * 0.68f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.80f)
            lineTo(w * 0.15f, h * 0.80f)
            close()
        }
        drawPath(body, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Lens circle
        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.56f),
            style = Stroke(width = stroke)
        )
    }
}

@Composable
fun ChevronDownIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFF626D7F)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.35f)
            lineTo(w * 0.50f, h * 0.65f)
            lineTo(w * 0.78f, h * 0.35f)
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PhoneCallIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.35f)
            cubicTo(w * 0.22f, h * 0.65f, w * 0.35f, h * 0.78f, w * 0.65f, h * 0.78f)
            lineTo(w * 0.78f, h * 0.65f)
            lineTo(w * 0.62f, h * 0.50f)
            lineTo(w * 0.52f, h * 0.56f)
            cubicTo(w * 0.44f, h * 0.50f, w * 0.40f, h * 0.46f, w * 0.34f, h * 0.38f)
            lineTo(w * 0.40f, h * 0.28f)
            lineTo(w * 0.25f, h * 0.15f)
            close()
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PlusIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFF1A253C)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.5f),
            end = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.2f),
            end = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.8f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun MinusIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFF1A253C)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 2.dp.toPx()
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.5f),
            end = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun TrashIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFFEF4444)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.8.dp.toPx()
        // Lid bar
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.28f),
            end = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.28f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Handle
        val handle = Path().apply {
            moveTo(w * 0.38f, h * 0.28f)
            lineTo(w * 0.38f, h * 0.18f)
            lineTo(w * 0.62f, h * 0.18f)
            lineTo(w * 0.62f, h * 0.28f)
        }
        drawPath(handle, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Can body
        val body = Path().apply {
            moveTo(w * 0.26f, h * 0.28f)
            lineTo(w * 0.30f, h * 0.82f)
            cubicTo(w * 0.30f, h * 0.88f, w * 0.35f, h * 0.88f, w * 0.40f, h * 0.88f)
            lineTo(w * 0.60f, h * 0.88f)
            cubicTo(w * 0.65f, h * 0.88f, w * 0.70f, h * 0.88f, w * 0.70f, h * 0.82f)
            lineTo(w * 0.74f, h * 0.28f)
        }
        drawPath(body, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Inner vertical slats
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.40f),
            end = androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.75f),
            strokeWidth = stroke * 0.85f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.40f),
            end = androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.75f),
            strokeWidth = stroke * 0.85f,
            cap = StrokeCap.Round
        )
    }
}

