package com.example.easyteeth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MedicalAlerts(isContagious: Boolean, hasAllergies: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isContagious) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = Color(0xFFD32F2F), shape = CircleShape)
            )
        }
        if (hasAllergies) {
            if (isContagious) Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = Color(0xFF4CAF50), shape = CircleShape)
            )
        }
    }
}
