package com.example.easyteeth.model

import java.time.LocalDateTime

data class AppointmentRequest(
    val motive: String,
    val date: String, // Enviamos como String ISO para que Jackson lo parsee a LocalDateTime
    val patientId: Long?,
    val boxId: Long?,
    val odontologistId: Long?,
    val treatmentId: Long?
)
