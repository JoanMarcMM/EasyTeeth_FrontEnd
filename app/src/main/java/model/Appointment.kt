package com.example.easyteeth.model



data class Appointment(
    val id: Long? = null,
    val motive: String,
    val date: String,
    val patient: Patient?,
    val box: Box?,
    val odontologist: Odontologist?,
    val treatment: Treatment?
)