package com.example.easyteeth.model

data class Image(
    val id: Long? = null,
    val image: String,
    val type: String,
    val patient: Patient? = null
)