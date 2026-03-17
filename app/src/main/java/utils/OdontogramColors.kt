package com.example.easyteeth.utils

import androidx.compose.ui.graphics.Color
import com.example.easyteeth.model.Odontogram

val OdontoRed = Color(0xFFE53935)
val OdontoBlue = Color(0xFF1E88E5)
val OdontoGreen = Color(0xFF00C853)
val OdontoYellow = Color(0xFFFFEB3B)
val OdontoBlack = Color(0xFF111111)
val OdontoGray = Color(0xFFE0E0E0)

fun getSimpleOdontogramColor(record: Odontogram?): Color {
    if (record == null) return OdontoGray

    return when {
        record.treated -> OdontoBlue
        record.pathology?.id == 11L -> OdontoBlack
        record.pathology?.id == 9L -> OdontoGreen
        record.pathology?.id == 10L -> OdontoYellow
        else -> OdontoRed
    }
}

fun isMissingTooth(records: List<Odontogram>): Boolean {
    return records.any { it.pathology?.id == 11L }
}

fun toothHasFiveSides(toothNumber: Int): Boolean {
    val lastDigit = toothNumber % 10
    return when (lastDigit) {
        1, 2, 3 -> false
        else -> true
    }
}