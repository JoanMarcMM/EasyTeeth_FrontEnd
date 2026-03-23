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
    showLabels: Boolean = false
) {
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
                drawPath(topPath, topColor)
                drawPath(leftPath, leftColor)
                drawPath(rightPath, rightColor)
                drawPath(bottomPath, bottomColor)
                drawRect(centerColor, topLeft = inner.topLeft, size = inner.size)

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
                drawPath(topPath, topColor)
                drawPath(leftPath, leftColor)
                drawPath(rightPath, rightColor)
                drawPath(bottomPath, bottomColor)

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