package com.example.easyteeth.model

data class Pathology(
    val id: Long,
    val name: String,
    val treatments: List<Treatment>? = emptyList()
)