package com.example.easyteeth.utils

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.Shape

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