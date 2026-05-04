package com.example.easyteeth.model


data class Treatment(
    val id: Long? = null,
    val name: String,
    val duration: Int,
    val pathologies: List<Pathology>? = emptyList(),
    val specialities: List<Speciality>? = emptyList(),
    val description: String? = null
)
