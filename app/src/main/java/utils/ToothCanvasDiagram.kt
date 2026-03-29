package com.example.easyteeth.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.easyteeth.model.Odontogram

@Composable
fun ToothCanvasDiagram(
    modifier: Modifier = Modifier,
    hasFiveSides: Boolean,
    topColor: Color,
    leftColor: Color,
    rightColor: Color,
    bottomColor: Color,
    centerColor: Color = Color.Transparent,
    enableClicks: Boolean = false,
    onSideClick: ((Long) -> Unit)? = null,
    showLabels: Boolean = false,
    topData: Odontogram? = null,
    leftData: Odontogram? = null,
    rightData: Odontogram? = null,
    bottomData: Odontogram? = null,
    centerData: Odontogram? = null
) {
    // Check if any side has pathology 5 or 6
    val hasSpecialPathology = listOf(topData, leftData, rightData, bottomData, centerData).any {
        it?.pathology?.id == 5L || it?.pathology?.id == 6L
    }

    // Override colors to grey if special pathology is present
    val finalTopColor = if (hasSpecialPathology) Color(0xFFE0E0E0) else topColor
    val finalLeftColor = if (hasSpecialPathology) Color(0xFFE0E0E0) else leftColor
    val finalRightColor = if (hasSpecialPathology) Color(0xFFE0E0E0) else rightColor
    val finalBottomColor = if (hasSpecialPathology) Color(0xFFE0E0E0) else bottomColor
    val finalCenterColor = if (hasSpecialPathology) Color(0xFFE0E0E0) else centerColor

    val clickModifier = if (enableClicks && onSideClick != null) {
        Modifier.pointerInput(hasFiveSides) {
            detectTapGestures { offset ->
                val x = offset.x / size.width
                val y = offset.y / size.height
                val margin = 0.28f

                val side = if (hasFiveSides && x in margin..(1f - margin) && y in margin..(1f - margin)) {
                    5L
                } else {
                    when {
                        y <= x && y <= 1f - x -> 1L
                        x <= y && x <= 1f - y -> 2L
                        y >= x && y >= 1f - x -> 4L
                        else -> 3L
                    }
                }

                onSideClick(side)
            }
        }
    } else {
        Modifier
    }

    Box(modifier = modifier.then(clickModifier)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 2.2.dp.toPx()
            val borderColor = Color.Black
            val outer = Rect(0f, 0f, size.width, size.height)

            if (hasFiveSides) {
                val m = size.width * 0.28f
                val inner = Rect(m, m, size.width - m, size.height - m)

                val topPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(inner.right, inner.top)
                    lineTo(inner.left, inner.top)
                    close()
                }

                val leftPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(inner.left, inner.top)
                    lineTo(inner.left, inner.bottom)
                    lineTo(0f, size.height)
                    close()
                }

                val rightPath = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(inner.right, inner.bottom)
                    lineTo(inner.right, inner.top)
                    close()
                }

                val bottomPath = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(inner.left, inner.bottom)
                    lineTo(inner.right, inner.bottom)
                    lineTo(size.width, size.height)
                    close()
                }

                drawRect(color = Color.White, size = size)
                drawPath(topPath, finalTopColor)
                drawPath(leftPath, finalLeftColor)
                drawPath(rightPath, finalRightColor)
                drawPath(bottomPath, finalBottomColor)
                drawRect(finalCenterColor, topLeft = inner.topLeft, size = inner.size)

                drawRect(
                    color = borderColor,
                    topLeft = outer.topLeft,
                    size = outer.size,
                    style = Stroke(width = stroke)
                )

                drawRect(
                    color = borderColor,
                    topLeft = inner.topLeft,
                    size = inner.size,
                    style = Stroke(width = stroke)
                )

                drawLine(borderColor, Offset(0f, 0f), Offset(inner.left, inner.top), strokeWidth = stroke)
                drawLine(borderColor, Offset(size.width, 0f), Offset(inner.right, inner.top), strokeWidth = stroke)
                drawLine(borderColor, Offset(0f, size.height), Offset(inner.left, inner.bottom), strokeWidth = stroke)
                drawLine(borderColor, Offset(size.width, size.height), Offset(inner.right, inner.bottom), strokeWidth = stroke)

                // Draw overlays for special pathologies
                drawPathologyOverlay(topData, Offset(size.width / 2f, size.height / 2f), size.width * 0.25f)
                drawPathologyOverlay(leftData, Offset(size.width / 2f, size.height / 2f), size.width * 0.25f)
                drawPathologyOverlay(rightData, Offset(size.width / 2f, size.height / 2f), size.width * 0.25f)
                drawPathologyOverlay(bottomData, Offset(size.width / 2f, size.height / 2f), size.width * 0.25f)
                drawPathologyOverlay(centerData, Offset(size.width / 2f, size.height / 2f), size.width * 0.25f)

            } else {
                val center = Offset(size.width / 2f, size.height / 2f)

                val topPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(center.x, center.y)
                    close()
                }

                val leftPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(center.x, center.y)
                    lineTo(0f, size.height)
                    close()
                }

                val rightPath = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(center.x, center.y)
                    close()
                }

                val bottomPath = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(size.width, size.height)
                    lineTo(center.x, center.y)
                    close()
                }

                drawRect(color = Color.White, size = size)
                drawPath(topPath, finalTopColor)
                drawPath(leftPath, finalLeftColor)
                drawPath(rightPath, finalRightColor)
                drawPath(bottomPath, finalBottomColor)

                drawRect(
                    color = borderColor,
                    topLeft = outer.topLeft,
                    size = outer.size,
                    style = Stroke(width = stroke)
                )

                drawLine(borderColor, Offset(0f, 0f), center, strokeWidth = stroke)
                drawLine(borderColor, Offset(size.width, 0f), center, strokeWidth = stroke)
                drawLine(borderColor, Offset(0f, size.height), center, strokeWidth = stroke)
                drawLine(borderColor, Offset(size.width, size.height), center, strokeWidth = stroke)

                // Draw overlays for special pathologies - centered in tooth middle
                drawPathologyOverlay(topData, Offset(center.x, center.y), size.width * 0.25f)
                drawPathologyOverlay(leftData, Offset(center.x, center.y), size.width * 0.25f)
                drawPathologyOverlay(rightData, Offset(center.x, center.y), size.width * 0.25f)
                drawPathologyOverlay(bottomData, Offset(center.x, center.y), size.width * 0.25f)
            }
        }

        if (showLabels) {
            Text("1", modifier = Modifier.align(Alignment.TopCenter), fontWeight = FontWeight.Bold)
            Text("2", modifier = Modifier.align(Alignment.CenterStart), fontWeight = FontWeight.Bold)
            Text("3", modifier = Modifier.align(Alignment.CenterEnd), fontWeight = FontWeight.Bold)
            Text("4", modifier = Modifier.align(Alignment.BottomCenter), fontWeight = FontWeight.Bold)

            if (hasFiveSides) {
                Text("5", modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPathologyOverlay(
    data: Odontogram?,
    center: Offset,
    size: Float
) {
    if (data == null || data.pathology == null) return

    val overlayColor = if (data.treated) Color(0xFF1E88E5) else Color(0xFFE53935) // Blue if treated, Red otherwise
    val strokeWidth = 10f  // Bolder strokes
    val overlaySize = size * 0.9f  // Smaller and centered

    when (data.pathology.id) {
        5L -> {
            // Draw X for pathology ID 5
            drawLine(
                color = overlayColor,
                start = Offset(center.x - overlaySize, center.y - overlaySize),
                end = Offset(center.x + overlaySize, center.y + overlaySize),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = overlayColor,
                start = Offset(center.x + overlaySize, center.y - overlaySize),
                end = Offset(center.x - overlaySize, center.y + overlaySize),
                strokeWidth = strokeWidth
            )
        }
        6L -> {
            // Draw E for pathology ID 6
            drawLine(
                color = overlayColor,
                start = Offset(center.x - overlaySize, center.y - overlaySize),
                end = Offset(center.x + overlaySize, center.y - overlaySize),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = overlayColor,
                start = Offset(center.x - overlaySize, center.y - overlaySize),
                end = Offset(center.x - overlaySize, center.y + overlaySize),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = overlayColor,
                start = Offset(center.x - overlaySize, center.y),
                end = Offset(center.x + overlaySize, center.y),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = overlayColor,
                start = Offset(center.x - overlaySize, center.y + overlaySize),
                end = Offset(center.x + overlaySize, center.y + overlaySize),
                strokeWidth = strokeWidth
            )
        }
    }
}