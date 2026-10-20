package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PinkPrimary

@Composable
fun BalloonIcon(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    balloonColor: Color = PinkPrimary,
    accentColor: Color = GoldAccent
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Balloon body
        drawOval(
            color = balloonColor,
            topLeft = Offset(w * 0.15f, h * 0.05f),
            size = Size(w * 0.7f, h * 0.65f)
        )

        // Highlight shine
        drawOval(
            color = Color.White.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.3f, h * 0.15f),
            size = Size(w * 0.18f, h * 0.25f)
        )

        // Balloon knot
        val knotPath = Path().apply {
            moveTo(w * 0.44f, h * 0.70f)
            lineTo(w * 0.56f, h * 0.70f)
            lineTo(w * 0.5f, h * 0.77f)
            close()
        }
        drawPath(knotPath, color = balloonColor)

        // Balloon string
        val stringPath = Path().apply {
            moveTo(w * 0.5f, h * 0.77f)
            cubicTo(w * 0.42f, h * 0.85f, w * 0.58f, h * 0.92f, w * 0.5f, h * 0.98f)
        }
        drawPath(stringPath, color = accentColor, style = Stroke(width = w * 0.05f))
    }
}

@Composable
fun GiftIcon(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    boxColor: Color = PinkPrimary,
    ribbonColor: Color = GoldAccent
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Box bottom
        drawRoundRect(
            color = boxColor,
            topLeft = Offset(w * 0.15f, h * 0.4f),
            size = Size(w * 0.7f, h * 0.52f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
        )

        // Box lid
        drawRoundRect(
            color = boxColor,
            topLeft = Offset(w * 0.1f, h * 0.28f),
            size = Size(w * 0.8f, h * 0.16f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
        )

        // Vertical ribbon
        drawRect(
            color = ribbonColor,
            topLeft = Offset(w * 0.44f, h * 0.28f),
            size = Size(w * 0.12f, h * 0.64f)
        )

        // Horizontal ribbon on lid
        drawRect(
            color = ribbonColor,
            topLeft = Offset(w * 0.1f, h * 0.33f),
            size = Size(w * 0.8f, h * 0.06f)
        )

        // Bow loop left
        val bowLeft = Path().apply {
            moveTo(w * 0.5f, h * 0.28f)
            cubicTo(w * 0.25f, h * 0.12f, w * 0.35f, h * 0.08f, w * 0.5f, h * 0.24f)
        }
        drawPath(bowLeft, color = ribbonColor, style = Stroke(width = w * 0.07f))

        // Bow loop right
        val bowRight = Path().apply {
            moveTo(w * 0.5f, h * 0.28f)
            cubicTo(w * 0.75f, h * 0.12f, w * 0.65f, h * 0.08f, w * 0.5f, h * 0.24f)
        }
        drawPath(bowRight, color = ribbonColor, style = Stroke(width = w * 0.07f))
    }
}

@Composable
fun CakeIcon(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    cakeColor: Color = PinkPrimary,
    icingColor: Color = Color.White,
    candleColor: Color = GoldAccent
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Plate
        drawRoundRect(
            color = Color(0xFFE2E8F0),
            topLeft = Offset(w * 0.08f, h * 0.82f),
            size = Size(w * 0.84f, h * 0.1f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
        )

        // Bottom cake layer
        drawRoundRect(
            color = cakeColor,
            topLeft = Offset(w * 0.16f, h * 0.52f),
            size = Size(w * 0.68f, h * 0.32f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
        )

        // Top cake layer
        drawRoundRect(
            color = cakeColor.copy(alpha = 0.9f),
            topLeft = Offset(w * 0.26f, h * 0.32f),
            size = Size(w * 0.48f, h * 0.22f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
        )

        // Frosting drops
        drawRoundRect(
            color = icingColor,
            topLeft = Offset(w * 0.25f, h * 0.30f),
            size = Size(w * 0.5f, h * 0.08f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )

        // Candle
        drawRect(
            color = Color.White,
            topLeft = Offset(w * 0.47f, h * 0.16f),
            size = Size(w * 0.06f, h * 0.16f)
        )

        // Flame
        drawOval(
            color = candleColor,
            topLeft = Offset(w * 0.45f, h * 0.05f),
            size = Size(w * 0.1f, h * 0.12f)
        )
    }
}

@Composable
fun ConfettiMotif(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val confettiColors = listOf(
            GoldAccent, PinkPrimary, Color(0xFF38BDF8),
            Color(0xFFA855F7), Color(0xFF34D399), Color(0xFFF43F5E)
        )

        val dots = listOf(
            Triple(0.12f, 0.2f, confettiColors[0]),
            Triple(0.85f, 0.25f, confettiColors[1]),
            Triple(0.28f, 0.75f, confettiColors[2]),
            Triple(0.72f, 0.8f, confettiColors[3]),
            Triple(0.5f, 0.15f, confettiColors[4]),
            Triple(0.92f, 0.65f, confettiColors[5]),
            Triple(0.08f, 0.6f, confettiColors[0]),
            Triple(0.42f, 0.85f, confettiColors[1])
        )

        dots.forEach { (xRatio, yRatio, col) ->
            drawCircle(
                color = col,
                radius = 4.dp.toPx(),
                center = Offset(w * xRatio, h * yRatio)
            )
        }
    }
}
