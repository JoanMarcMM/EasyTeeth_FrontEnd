package model


data class Appointment(
    val motive: String,
    val date: String,
    val patientId: Long,
    val boxId: Long,
    val odontologistId: Long,
    val treatmentId: Long
)
