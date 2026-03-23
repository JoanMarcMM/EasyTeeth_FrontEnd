package com.example.easyteeth.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun topToothShape(): Shape = GenericShape { size, _ ->
    moveTo(size.width * 0.2f, size.height)
    lineTo(size.width * 0.8f, size.height)
    lineTo(size.width, 0f)
    lineTo(0f, 0f)
}

fun bottomToothShape(): Shape = GenericShape { size, _ ->
    moveTo(0f, size.height)
    lineTo(size.width, size.height)
    lineTo(size.width * 0.8f, 0f)
    lineTo(size.width * 0.2f, 0f)
}

fun leftToothShape(): Shape = GenericShape { size, _ ->
    moveTo(size.width, size.height * 0.2f)
    lineTo(size.width, size.height * 0.8f)
    lineTo(0f, size.height)
    lineTo(0f, 0f)
}

fun rightToothShape(): Shape = GenericShape { size, _ ->
    moveTo(0f, size.height * 0.2f)
    lineTo(0f, size.height * 0.8f)
    lineTo(size.width, size.height)
    lineTo(size.width, 0f)
}
@Composable
fun ToothDivisions(
    hasFiveSides: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {

        // Línea vertical
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.5.dp)
                .align(Alignment.Center)
                .background(Color.Black)
        )

        // Línea horizontal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .align(Alignment.Center)
                .background(Color.Black)
        )

        // Si tiene 5 lados → cuadrado interior
        if (hasFiveSides) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .border(1.5.dp, Color.Black)
            )
        }
    }
}