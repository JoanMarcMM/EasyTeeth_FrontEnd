package com.example.easyteeth.model

data class Odontogram(
    val id: Long,
    val patient: Patient?,
    val tooth: Tooth?,
    val side: Side?,
    val pathology: Pathology?,
    val treated: Boolean,
    val note: String?
)