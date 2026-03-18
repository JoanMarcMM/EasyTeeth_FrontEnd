package com.example.easyteeth.model

data class ImageRequest(
    val image: ByteArray,
    val type: String,
    val patientId: Long
)