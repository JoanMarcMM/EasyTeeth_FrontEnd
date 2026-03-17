package com.example.easyteeth.model

data class OdontogramRequest(
    val patientId: Long,
    val toothId: Long,
    val sideId: Long,
    val pathologyId: Long,
    val treated: Boolean,
    val note: String?
)