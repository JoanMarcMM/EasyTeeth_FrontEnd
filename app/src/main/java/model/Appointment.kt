package com.example.easyteeth.model



data class Appointment(
    val id: Long? = null,
    val motive: String,
    val date: String,
    val patient: Patient?,
    val box: Box?,          // Asegúrate de tener estas clases o cámbialas a Any? temporalmente
    val odontologist: Any?,
    val treatment: Treatment?
)