package com.example.easyteeth.model



data class Document(
    val id: Long? = null,
    val name: String,
    val type: String,
    val file: String,
    val creationDate: String,
    val patient: Patient? = null
)